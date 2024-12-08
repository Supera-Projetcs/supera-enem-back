package com.supera.storage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.FileInputStream;
import java.io.InputStream;

public class MinioConfig {
    private final MinioClient minioClient;

    public MinioConfig() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://127.0.0.1:9000")
                .credentials("minioadmin", "minioadmin") 
                .build();
    }

    public void uploadFile(String bucketName, String objectName, String filePath) throws Exception {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1) 
                            .build()
            );
            System.out.println("Arquivo enviado com sucesso!");
        }
    }
}
