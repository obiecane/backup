package com.zak.backup.services;

import com.zak.backup.databasebackup.AsyncBackupWrapper;
import com.zak.backup.databasebackup.Backup;
import com.zak.backup.databasebackup.BackupConfig;
import com.zak.backup.databasebackup.DatabaseBackuperFactory;
import com.zak.backup.entity.BackupDto;
import com.zak.backup.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author zak
 * @version 1.0
 * @date 2019/8/2 9:59
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
            log.info("备份回调-> bakFilePath:[{}], errMsg:[{}]", bakFilePath, errMsg);
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
            log.info("还原回调-> success:[{}], errMsg:[{}]", success, errMsg);
            Map<String, Object> p = new HashMap<>(3);
            p.put("backupId", dto.getBackupId());
            p.put("success", success);
            p.put("errMsg", errMsg);
            HttpClientUtil.postJson(dto.getCallbackUrl(), p);
        });
    }

}
