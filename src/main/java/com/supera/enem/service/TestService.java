package com.supera.enem.service;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.Test;
import com.supera.enem.mapper.TestMapper;
import com.supera.enem.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;
    private final TestMapper testMapper = TestMapper.INSTANCE;

    public List<TestResponseDTO> getCompletedTests() {
        return testRepository.findCompletedTests().stream()
                .map(testMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TestResponseDTO getTestById(Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));
        return testMapper.toDTO(test);
    }
}
