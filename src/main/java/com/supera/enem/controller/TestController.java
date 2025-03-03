package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.TestEntity;
import com.supera.enem.exception.GlobalExceptionHandler;
import com.supera.enem.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestController {
    @Autowired
    private TestService testService;

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @PostMapping("/generate")
    public TestResponseDTO generateTest() {
        return testService.generateTest();
    }

    @GetMapping("/completed")
    public ResponseEntity<Page<TestResponseDTO>> getCompletedTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Buscando testes completados - página: {}, tamanho: {}", page, size);
        Page<TestResponseDTO> tests = testService.getCompletedTests(page, size);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResponseDTO> getTestById(@PathVariable Long id) {
        logger.info("Buscando teste por ID: {}", id);
        TestResponseDTO test = testService.getTestById(id);
        return ResponseEntity.ok(test);
    }

    @GetMapping("/is-last-test-completed")
    public ResponseEntity<Boolean> isLastTestCompleted() {
        logger.info("Verificando se o último teste foi completado");
        boolean isCompleted = testService.wasThisWeekTestCompleted();
        return ResponseEntity.ok(isCompleted);
    }


}
