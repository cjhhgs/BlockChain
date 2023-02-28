package com.jhchen.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jhchen.domain.modul.Account;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


public class JSONUtil {
    /**
     * 将JSON数据格式化并保存到文件中
     * @param jsonData 需要输出的json数
     * @param filePath 输出的文件地址
     * @return
     */
    public static boolean createJsonFile(Object jsonData, String filePath) {
        String content = JSON.toJSONString(jsonData, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        // 标记文件生成是否成功
        boolean flag = true;
        // 生成json格式文件
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));

            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    public static <T> T loadJSONObject(String filePath,Class<T> clazz){
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        try {

            File file = new File(filePath);
            if(!file.exists()){
                return null;
            }
            String content = new String(Files.readAllBytes(file.toPath()));
            T object =JSON.parseObject(content, clazz);
            return object;
        } catch (IOException e) {
            return null;
        }

    }

    private static String getResourceBasePath() {
        // 获取跟目录
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            // nothing to do
        }
        if (path == null || !path.exists()) {
            path = new File("");
        }

        String pathStr = path.getAbsolutePath();
        // 如果是在eclipse中运行，则和target同级目录,如果是jar部署到服务器，则默认和jar包同级
        pathStr = pathStr.replace("\\target\\classes", "");

        System.out.println(pathStr);
        return pathStr;
    }

}
