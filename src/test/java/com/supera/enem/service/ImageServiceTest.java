package com.supera.enem.service;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve salvar a imagem com sucesso")
    void shouldSaveImageSuccessfully() throws IOException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4, 5}
        );

        String result = imageService.saveImage(mockMultipartFile);

        System.out.println("Caminho retornado: " + result);

        assertNotNull(result, "O caminho retornado não deve ser nulo.");

        Path uploadPath = Path.of("upload/images").toAbsolutePath();
        assertTrue(result.contains(uploadPath.toString()), "O caminho deve conter o diretório absoluto de upload.");
    }

    @Test
    @DisplayName("Deve criar diretório quando não existir")
    void shouldCreateDirectoryWhenNotExists() throws IOException {
        mockMultipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4, 5}
        );

        Path mockPath = Path.of("upload/images");

        if (!Files.exists(mockPath)) {
            Files.createDirectories(mockPath);
        }

        imageService.saveImage(mockMultipartFile);

        assertTrue(Files.exists(mockPath), "O diretório deve ser criado.");
    }

    @Test
    @DisplayName("Deve lançar exceção para arquivo vazio")
    void shouldThrowExceptionForEmptyFile() {
        mockMultipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[]{}
        );

        Exception exception = assertThrows(IOException.class, () -> {
            imageService.saveImage(mockMultipartFile);
        });

        assertEquals("The file is empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para formato de arquivo inválido")
    void shouldThrowExceptionForInvalidFileFormat() {
        mockMultipartFile = new MockMultipartFile(
                "file",
                "image.txt",
                "text/plain",
                new byte[]{1, 2, 3, 4, 5}
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            imageService.saveImage(mockMultipartFile);
        });

        assertEquals("Invalid file format", exception.getMessage());
    }

    @Test
    @DisplayName("Deve reutilizar diretório existente")
    void shouldReuseExistingDirectory() throws IOException {
        mockMultipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4, 5}
        );

        Path mockPath = Path.of("upload/images");

        Files.createDirectories(mockPath);

        imageService.saveImage(mockMultipartFile);

        assertTrue(Files.exists(mockPath), "O diretório existente deve ser reutilizado.");
    }
}
