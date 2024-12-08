package com.supera.storage;

public class MinioTest {
    public static void main(String[] args) {
        try {
            MinioConfig minioConfig = new MinioConfig();
            
            String bucketName = "bucket-test";
            String objectName = "archive.txt";
            String filePath = "C:\\Users\\Desktop\\Desktop\\Supera ENEM\\archive.txt";

            minioConfig.uploadFile(bucketName, objectName, filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
