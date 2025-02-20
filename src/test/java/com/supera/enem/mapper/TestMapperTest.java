package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.Image;
import com.supera.enem.domain.TestEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestMapperTest {

    private final TestMapper testMapper = Mappers.getMapper(TestMapper.class);

    @Test
    @DisplayName("Deve mapear TestEntity para TestResponseDTO com dados válidos.")
    void shouldMapTestEntityToDTO_WithValidData() {
        TestEntity testEntity = new TestEntity();
        testEntity.setId(1L);

        TestResponseDTO responseDTO = testMapper.toDTO(testEntity);

        assertNotNull(responseDTO, "O DTO não deve ser nulo.");
        assertEquals(1L, responseDTO.getId(), "O ID deve ser mapeado corretamente.");
    }

    @Test
    @DisplayName("Deve lidar com TestEntity nulo ao mapear para TestResponseDTO.")
    void shouldHandleNullTestEntity_WhenMappingToDTO() {
        TestResponseDTO responseDTO = testMapper.toDTO(null);

        assertNull(responseDTO, "O DTO deve ser nulo quando a entidade de entrada for nula.");
    }

    @Test
    @DisplayName("Deve mapear lista de imagens para lista de URLs com dados válidos.")
    void shouldMapImagesToStrings_WithValidData() {
        Image image1 = new Image();
        image1.setUrl("image1.jpg");

        Image image2 = new Image();
        image2.setUrl("image2.jpg");

        List<Image> images = List.of(image1, image2);

        List<String> urls = testMapper.mapImagesToStrings(images);

        assertNotNull(urls, "A lista de URLs não deve ser nula.");
        assertEquals(2, urls.size(), "A lista deve conter 2 URLs.");
        assertEquals("image1.jpg", urls.get(0), "A primeira URL deve ser mapeada corretamente.");
        assertEquals("image2.jpg", urls.get(1), "A segunda URL deve ser mapeada corretamente.");
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao mapear lista de imagens vazia")
    void shouldReturnEmptyList_WhenMappingEmptyImageList() {
        List<Image> images = Collections.emptyList();

        List<String> urls = testMapper.mapImagesToStrings(images);

        assertNotNull(urls, "A lista de URLs não deve ser nula.");
        assertTrue(urls.isEmpty(), "A lista de URLs deve estar vazia.");
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao mapear lista de imagens nula.")
    void shouldReturnEmptyList_WhenMappingNullImageList() {
        List<String> urls = testMapper.mapImagesToStrings(null);

        assertNotNull(urls, "A lista de URLs não deve ser nula.");
        assertTrue(urls.isEmpty(), "A lista de URLs deve estar vazia.");
    }

    @Test
    @DisplayName("Deve lidar com TestEntity com campos nulos ao mapear para TestResponseDTO")
    void shouldHandleNullFieldsInTestEntity_WhenMappingToDTO() {
        TestEntity testEntity = new TestEntity();
        testEntity.setId(1L);

        TestResponseDTO responseDTO = testMapper.toDTO(testEntity);

        assertNotNull(responseDTO, "O DTO não deve ser nulo.");
        assertEquals(1L, responseDTO.getId(), "O ID deve ser mapeado corretamente.");
    }
}
