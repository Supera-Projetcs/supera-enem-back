package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestController {
    @Autowired
    private TestService testService;


    @PostMapping("/generate")
    public TestResponseDTO generateTest() {
        return testService.generateTest();
    }

    @GetMapping("/completed")
    public List<TestResponseDTO> getCompletedTests() {
        return testService.getCompletedTests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResponseDTO> getTestById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getTestById(id));
    }


}
