package com.kkirsii.recruitmentplatform.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class YamlConfig {

    @Autowired
    private  static RedisTemplate<String, byte[]> redisTemplate;
    @Autowired
    public void setRedisTemplate(RedisTemplate<String, byte[]> template) {
        YamlConfig.redisTemplate = template;
    }

    public static String ImagePath;
    public static String ResumePath;
    private static final String CONFIG_URL = "http://115.190.167.70";

    static {
        log.info("开始请求远程配置文件");
        OkHttpClient client = new OkHttpClient();
        String url = CONFIG_URL+":8081?file=config.yaml";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String yamlContent = response.body().string();
                log.info("请求成功，响应内容: {}", yamlContent);
                Map<String, Object> config = new Yaml().load(yamlContent);
                Map<String, String> paths = (Map<String, String>) config.get("Path");
                ImagePath = paths.get("Image");
                ResumePath = paths.get("Resume");
            } else {
                log.error("请求失败，响应码: {}", response.code());
            }
        } catch (IOException e) {
            log.error("请求异常: {}", e.getMessage());

        }
    }

    public static String getImagePath() {
        return ImagePath;
    }

    public static String getResumePath() {
        return ResumePath;
    }

    public static byte[] downloadFile(String Path, String fileName) {
//        RedisTemplate<String, byte[]> redisTemplate = new RedisTemplate<>();
        String CacheKey = Path + fileName;
        byte[] cachedFile = (byte[]) redisTemplate.opsForValue().get(CacheKey);
        if (cachedFile != null) {
            log.info("从缓存中获取文件: {}", CacheKey);
            return cachedFile;
        }



        OkHttpClient client = new OkHttpClient();
        String url = CONFIG_URL+":8081?file=" + Path + fileName;
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                 byte[] fileBytes = response.body().bytes();
                log.info("下载成功，文件大小: {} 字节", fileBytes.length);
                redisTemplate.opsForValue().set(CacheKey, fileBytes, 5 * 60, java.util.concurrent.TimeUnit.SECONDS);

                 return fileBytes;
            }
        } catch (IOException e) {
            log.error("下载异常: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return null;

    }

    public static String uploadFile(MultipartFile file, String path, String fileName) throws IOException {
        RequestBody fileBody = RequestBody.create(
                file.getBytes(),
                MediaType.parse(file.getContentType())
        );
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("destination", path + fileName)
                .addFormDataPart("file", file.getOriginalFilename(), fileBody)
                .build();

        String CacheKey = path + fileName;
        redisTemplate.delete(CacheKey);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(CONFIG_URL+":8081")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response code: " + response.code());
            System.out.println("Response body: " + response.body().string());
            String message = "上传成功";
            return message;

        } catch (IOException e) {
            log.error("上传异常: {}", e.getMessage());
            throw e;

        }
    }
}
