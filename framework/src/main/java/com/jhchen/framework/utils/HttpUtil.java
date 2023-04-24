package com.jhchen.framework.utils;

import com.jhchen.framework.domain.modul.Account;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class HttpUtil {
    /**
     * 封装POST请求（String入参）
     *
     * @param url  请求的路径
     * @param data String类型数据
     * @return
     * @throws IOException
     */
    public static String post(String url, String data) throws IOException {
//        1、创建HttpClient对象
        HttpClient httpClient = HttpClientBuilder.create().build();
//        2、创建请求方式的实例
        HttpPost httpPost = new HttpPost(url);
//        3、添加请求参数(设置请求和传输超时时间)
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
//        设置请求参数
        httpPost.setEntity(new StringEntity(data, "UTF-8"));
//        4、发送Http请求
        HttpResponse response = httpClient.execute(httpPost);
//        5、获取返回的内容
        String result = null;
        int statusCode = response.getStatusLine().getStatusCode();
        if (200 == statusCode) {
            result = EntityUtils.toString(response.getEntity());
        } else {

            return null;
        }
//        6、释放资源
        httpPost.abort();
        httpClient.getConnectionManager().shutdown();
        return result;
    }

    public static String get(String url) throws IOException{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet);
        String result = null;
        int statusCode = response.getStatusLine().getStatusCode();
        if (200 == statusCode) {
            result = EntityUtils.toString(response.getEntity());
        } else {

            return null;
        }
        httpGet.abort();
        httpClient.getConnectionManager().shutdown();
        return result;
    }

    /**
     * 向指定账户广播信息
     * @param route
     * @param data
     * @param accountList
     */
    public static void broadcastMessage(String route, String data, List<Account> accountList) throws IOException {
        for (Account account : accountList) {
            String ip = account.getIp();
            if(ip!=null){
                HttpUtil.post(ip+route,data);
            }
        }
    }
}
