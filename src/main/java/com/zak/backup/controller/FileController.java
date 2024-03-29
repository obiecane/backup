package com.zak.backup.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author zak
 * @version 1.0
 * @date 2019/8/2 15:25
 */
@Slf4j
@Controller
public class FileController {

    @RequestMapping("/download")
    public void downloadFile(@RequestParam String filepath, HttpServletResponse response) {
        File file = new File(filepath);
        if (!file.exists()) {
            // TODO:
            return;
        }

        String filename = FilenameUtils.getName(filepath);

        // 实现文件下载
        try (
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                OutputStream os = response.getOutputStream()
        ) {
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));

            IOUtils.copy(bis, os);
            os.flush();

            log.debug("Provide download successfully! [{}]", filepath);
        } catch (Exception e) {
            log.debug("Provide download failed! [{}]", filepath);
        }
    }

    @RequestMapping("/deleteFile")
    @ResponseBody
    public void deleteFile(@RequestParam String filepath) {
        log.info("删除文件[{}]", filepath);
        new File(filepath).delete();
    }
}
