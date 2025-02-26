package com.supera.enem.controller;


import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private QuestionResponseDTO questionResponseDTO;

    @BeforeEach
    public void setUp() {
        // Cria um QuestionResponseDTO mockado
        questionResponseDTO = new QuestionResponseDTO();
        questionResponseDTO.setId(1L);
        questionResponseDTO.setText("What is 2 + 2?");
        questionResponseDTO.setAnswer('A');

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);
    }

    @Test
    public void testGetQuestionById_Success() throws Exception {
        // Mock do serviço para retornar o QuestionResponseDTO
        when(questionService.getQuestionById(1L)).thenReturn(questionResponseDTO);

        // Executa a requisição e verifica o resultado
        mockMvc.perform(get("/api/questions/{id}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk());
    }

    @Test
    public void testGetQuestionById_NotFound() throws Exception {
        // Mock do serviço para lançar uma exceção quando a questão não for encontrada
        when(questionService.getQuestionById(1L)).thenThrow(new ResourceNotFoundException("Question not found with ID: 1"));

        // Executa a requisição e verifica o resultado
        mockMvc.perform(get("/api/questions/{id}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isNotFound()); // Espera status 404
    }

}
