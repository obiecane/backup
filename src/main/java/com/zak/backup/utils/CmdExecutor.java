package com.zak.backup.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * 命令行执行器
 * 线程安全性未测试
 *
 * @author zak
 * @version 1.0
 * @date 2019/7/30 13:58
 */
@Slf4j
public class CmdExecutor {

    /**
     * 执行命令
     *
     * @param commonds 要执行的命令
     * @return 程序在执行命令过程中产生的各信息，执行出错时返回null
     */
    public static CmdResult executeCommand(List<String> commonds) {
        if (CollectionUtils.isEmpty(commonds)) {
            log.error(" 指令执行失败，因为要执行的指令为空！ ");
            return null;
        }
        String cmdStr = StringUtils.join(commonds, " ");
        return executeCommand(cmdStr);
    }

    public static CmdResult executeCommand(String cmd) {
        if (StringUtils.isBlank(cmd)) {
            log.error(" 指令执行失败，因为要执行的指令为空！ ");
            return null;
        }
        LinkedList<String> cmds = new LinkedList<>();
        String osName = System.getProperty("os.name");
        // 如果第一个不是cmd, 那么创建的进程将是直接调用其他程序, 而不是调用cmd
        // 所以cmd下的一些功能(如管道符,重定向符)都不被支持
        switch (OSUtil.currentPlatform()) {
            case OSUtil.PLATFORM_WINDOWS:
                cmds.add("cmd");
                // /c参数是指命令执行完毕后退出cmd
                cmds.add("/c");
                break;
            case OSUtil.PLATFORM_LINUX:
            case OSUtil.PLATFORM_MAC:
                cmds.add("sh");
                cmds.add("-c");
                break;
            default:
                log.error("不支持的操作系统: [{}]", osName);
        }

        cmds.add(cmd);
        // 设置程序所在路径
        log.debug(" 待执行的指令为：[{}]", cmd);

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            // 执行指令
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(cmds);
            process = builder.start();

            // 取出输出流和错误流的信息
            // 注意：必须要取出在执行命令过程中产生的输出信息，如果不取的话当输出流信息填满jvm存储输出留信息的缓冲区时，线程就回阻塞住
            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputStream.start();
            // 等待命令执行完
            int pr = process.waitFor();

            // 输出执行的命令信息
            String resultStr = pr == 0 ? "正常" : "异常";
            log.debug("已执行的命令：[{}]   已执行完毕,执行结果：[{}]", cmd, resultStr);
            return new CmdResult(pr, inputStream.stringBuffer.toString(), errorStream.stringBuffer.toString());
        } catch (Exception e) {
            log.error("命令执行出错！  出错信息： {}", e.getMessage());
            return null;
        } finally {
            if (null != process) {
                ProcessKiller killer = new ProcessKiller(process);
                // JVM退出时，先通过钩子关闭进程
                runtime.addShutdownHook(killer);
            }
        }
    }


    /**
     * 在程序退出前结束已有的进程
     */
    @Slf4j
    private static class ProcessKiller extends Thread {
        private Process process;

        ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
            log.debug(" 已销毁进程  进程名： [{}]", process.toString());
        }
    }


    /**
     * 用于取出线程执行过程中产生的各种输出和错误流的信息
     */
    @Slf4j
    static class PrintStream extends Thread {
        InputStream inputStream;
        BufferedReader bufferedReader;
        StringBuffer stringBuffer;

        PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                if (null == inputStream) {
                    log.error(" 读取输出流出错！因为当前输出流为空！");
                }
                String charsetName = OSUtil.platformCharsetName();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
                String line;
                stringBuffer = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    log.debug(line);
                    stringBuffer.append(line);
                }
            } catch (Exception e) {
                log.error(" 读取输入流出错了！ 错误信息：{}", e.getMessage());
            } finally {
                try {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    log.error(" 调用PrintStream读取输出流后，关闭流时出错！");
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    @ToString
    public static class CmdResult {
        private int code;
        private String out;
        private String error;
    }
}
