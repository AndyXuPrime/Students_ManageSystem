package com.javaee_hw.resource.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Component
public class MinioUtils {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 上传文件
     * @param file 前端传来的文件
     * @return 文件的访问 URL
     */
    public String upload(MultipartFile file) {
        try {
            // 1. 获取文件流
            InputStream inputStream = file.getInputStream();

            // 2. 生成唯一文件名 (防止同名覆盖)
            // 例如：test.pdf -> 550e8400-e29b...pdf
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;

            // 3. 上传到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 4. 拼接返回 URL (因为我们将桶设为了 Public，所以直接拼接即可)
            // 格式: http://localhost:9000/sims-files/xxxx.pdf
            return endpoint + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}