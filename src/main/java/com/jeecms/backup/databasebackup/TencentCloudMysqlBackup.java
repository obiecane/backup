package com.jeecms.backup.databasebackup;


import com.tencentcloudapi.cdb.v20170320.CdbClient;
import com.tencentcloudapi.cdb.v20170320.models.CreateBackupRequest;
import com.tencentcloudapi.cdb.v20170320.models.CreateBackupResponse;
import com.tencentcloudapi.cdb.v20170320.models.DescribeBackupsRequest;
import com.tencentcloudapi.cdb.v20170320.models.DescribeBackupsResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;


/**
 * 腾讯云Mysql数据库备份和还原
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/31 17:02
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class TencentCloudMysqlBackup extends AbstractDatabaseBackup {

    public static void main(String[] args) {
        new TencentCloudMysqlBackup().doBackup();
    }

    @Override
    protected String doBackup() {
        try{
            Credential cred = new Credential("AKIDWnnfGaOX8sLXS6FQghRvCBvPcfX87aS3", "U0GzUmAHG9WcQxDH5g9MK7dsqY1DAE3c");

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("cdb.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            CdbClient client = new CdbClient(cred, "ap-guangzhou", clientProfile);

            CreateBackupRequest request = new CreateBackupRequest();
            request.setInstanceId("cdb-0mvaub0r");
            request.setBackupMethod("physical");

            CreateBackupResponse resp = client.CreateBackup(request);

            System.out.println(CreateBackupRequest.toJsonString(resp));
            return String.valueOf(resp.getBackupId());
        } catch (TencentCloudSDKException e) {
            // OperationDenied.ActionInProcess-can not backup the instance now
            // 可能是有备份操作正在运行中,有可能是自动备份的,  可以通过DescribeBackups查看最近的备份的状态
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected boolean doRecovery() {
        return true;
    }


    private void test() {
        try{
            Credential cred = new Credential("AKIDWnnfGaOX8sLXS6FQghRvCBvPcfX87aS3", "U0GzUmAHG9WcQxDH5g9MK7dsqY1DAE3c");

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("cdb.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            CdbClient client = new CdbClient(cred, "ap-guangzhou", clientProfile);

            DescribeBackupsRequest req = new DescribeBackupsRequest();
            req.setInstanceId("cdb-0mvaub0r");

            DescribeBackupsResponse resp = client.DescribeBackups(req);

            System.out.println(DescribeBackupsRequest.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }

    }


    @Override
    protected void before() {

    }
}
