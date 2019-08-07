package com.jeecms.backup.services;

import com.jeecms.backup.databasebackup.AsyncBackupWrapper;
import com.jeecms.backup.databasebackup.Backup;
import com.jeecms.backup.databasebackup.BackupConfig;
import com.jeecms.backup.databasebackup.DatabaseBackuperFactory;
import com.jeecms.backup.entity.BackupDto;
import com.jeecms.backup.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/8/2 9:59
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Service
public class BackupServices {

    private AsyncBackupWrapper asyncBackupWrapper = new AsyncBackupWrapper();

    // 备份
    public void databaseBackup(final BackupDto dto) {
        BackupConfig backupConfig = BackupConfig.of(dto);
        Backup backuper = DatabaseBackuperFactory.createBackuper(backupConfig);
        asyncBackupWrapper.backup(backuper, (bakFilePath, errMsg) -> {
            bakFilePath = Optional.ofNullable(bakFilePath).map(s -> s.replaceAll("\\\\", "/")).orElse(null);
            log.debug("备份回调-> bakFilePath:[{}], errMsg:[{}]", bakFilePath, errMsg);
            Map<String, Object> p = new HashMap<>(4);
            p.put("backupId", dto.getBackupId());
            p.put("success", bakFilePath != null);
            p.put("bakFilePath", bakFilePath);
            p.put("errMsg", errMsg);
            HttpClientUtil.postJson(dto.getCallbackUrl(), p);
        });
    }

    // 还原
    public void databaseRecovery(final BackupDto dto) {
        BackupConfig backupConfig = BackupConfig.of(dto);
        Backup backuper = DatabaseBackuperFactory.createBackuper(backupConfig);
        asyncBackupWrapper.recovery(backuper, (success, errMsg) -> {
            log.debug("还原回调-> success:[{}], errMsg:[{}]", success, errMsg);
            Map<String, Object> p = new HashMap<>(3);
            p.put("backupId", dto.getBackupId());
            p.put("success", success);
            p.put("errMsg", errMsg);
            HttpClientUtil.postJson(dto.getCallbackUrl(), p);
        });
    }

}
