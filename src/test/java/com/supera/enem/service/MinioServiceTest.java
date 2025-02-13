package com.supera.enem.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MinioServiceTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    private final String bucketName = "test-bucket";
    private final String minioUrl = "http://localhost:9000";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
//        ReflectionTestUtils.setField(minioService, "minioClient", minioClient);
        ReflectionTestUtils.setField(minioService, "bucketName", bucketName);
        ReflectionTestUtils.setField(minioService, "minioUrl", minioUrl);
    }

    @Test
    @DisplayName("Deve fazer upload de arquivo com sucesso")
    void testUploadFile_Success() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test data".getBytes());

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        String result = minioService.uploadFile("test-file.txt", inputStream, "text/plain");

        assertEquals("http://localhost:9000/test-bucket/test-file.txt", result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao fazer upload de arquivo")
    void testUploadFile_ThrowsException() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test data".getBytes());

        doThrow(new IOException("MinIO Error")).when(minioClient).putObject(any(PutObjectArgs.class));

        assertThrows(IOException.class, () -> minioService.uploadFile("test-file.txt", inputStream, "text/plain"));
    }
}
