package com.jeecms.backup.controller;

import com.jeecms.backup.entity.BackupDto;
import com.jeecms.backup.exception.BackupException;
import com.jeecms.backup.services.BackupServices;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zak
 * @version 1.0
 * @date 2019/8/2 13:59
 */
@Slf4j
@RestController
@AllArgsConstructor
public class BackupController {

    private BackupServices backupServices;


    @PostMapping("/backup")
    public Map<String, Object> databaseBackup(@RequestBody @Valid BackupDto dto) {
        log.info("准备启动备份任务  [{}]", dto.toString());
        backupServices.databaseBackup(dto);
        Map<String, Object> ret = new HashMap<>(3);

        ret.put("status", 200);
        ret.put("message", "备份任务启动成功");
        return ret;
    }

    @PostMapping("/recovery")
    public Map<String, Object> databaseRecovery(@RequestBody @Valid BackupDto dto) throws BackupException {
        log.info("准备启动还原任务  [{}]", dto.toString());
        backupServices.databaseRecovery(dto);
        Map<String, Object> ret = new HashMap<>(3);
        ret.put("status", 200);
        ret.put("message", "还原任务启动成功");
        return ret;
    }
}
