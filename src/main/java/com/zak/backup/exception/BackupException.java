package com.zak.backup.exception;

/**
 * @author zak
 * @version 1.0
 * @date 2019/8/3 14:42
 */
public class BackupException extends RuntimeException {

    public BackupException() {
    }

    public BackupException(String msg) {
        super(msg);
    }
}
