package com.jeecms.backup.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * HttpClient 工具类
 *
 * @author zak
 * @date 2019年8月2日 上午17:22:18
 */
@Slf4j
public class HttpClientUtil {

    /**
     * 以application/json的形式提交post请求
     * 对应spring mvc中的@RequestBody注解
     *
     * @param url    url
     * @param params 参数
     * @return java.lang.String
     * @author zak
     * @date 2019/8/2 17:23
     **/
    public static <P> String postJson(String url, P params) {
        String result = "";
        try {
            @Cleanup CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            Header applicationJson = new BasicHeader("Content-Type", "application/json;charset=UTF-8");
            post.addHeader(applicationJson);

            if (params != null) {
                StringEntity entity = new StringEntity(JSONObject.toJSONString(params), "utf-8");
                entity.setContentEncoding(applicationJson);
                post.setEntity(entity);
            }

            //发送请求并接收返回数据
            @Cleanup CloseableHttpResponse response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return result;
    }


}
