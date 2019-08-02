package com.jeecms.backup.databasebackup;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.rds.model.v20140815.CreateBackupRequest;
import com.aliyuncs.rds.model.v20140815.CreateBackupResponse;
import com.aliyuncs.rds.model.v20140815.RecoveryDBInstanceRequest;
import com.aliyuncs.rds.model.v20140815.RecoveryDBInstanceResponse;
import com.google.gson.Gson;

/**
 * 阿里云mysql数据库备份和还原
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/31 16:45
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class AliCloudMysqlBackup extends AbstractDatabaseBackup {


    @Override
    protected String doBackup() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "<accessKeyId>", "<accessSecret>");
        IAcsClient client = new DefaultAcsClient(profile);

        CreateBackupRequest request = new CreateBackupRequest();
        request.setRegionId("cn-hangzhou");
        request.setDBInstanceId("123");
        request.setBackupMethod("Physical");
        request.setBackupType("FullBackup");

        try {
            CreateBackupResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
        return null;
    }

    @Override
    protected boolean doRecovery() {
        DefaultProfile profile = DefaultProfile.getProfile("ap-southeast-2", "<accessKeyId>", "<accessSecret>");
        IAcsClient client = new DefaultAcsClient(profile);

        RecoveryDBInstanceRequest request = new RecoveryDBInstanceRequest();
        request.setRegionId("ap-southeast-2");
        request.setDbNames("aa");
        request.setDBInstanceClass("ffff");
        request.setDBInstanceStorage(1);
        request.setPayType("ss");
        request.setInstanceNetworkType("zz");
        request.setDBInstanceId("qwer");
        request.setTargetDBInstanceId("qwe");
        request.setBackupId("wer");
        request.setRestoreTime("qerqewrqwer");
        request.setVPCId("qwererwr");
        request.setVSwitchId("jjj");
        request.setPrivateIpAddress("hhhh");
        request.setUsedTime("ggg");
        request.setPeriod("aass");

        try {
            RecoveryDBInstanceResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
        return true;
    }

    @Override
    protected void before() {

    }
}
