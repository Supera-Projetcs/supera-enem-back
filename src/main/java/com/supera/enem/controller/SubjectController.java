package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.ContentDTO;
import com.supera.enem.controller.DTOS.SubjectDTO;
import com.supera.enem.mapper.SubjectMapper;
import com.supera.enem.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectMapper subjectMapper;


    @GetMapping
    public List<SubjectDTO> findAll() {
        return subjectMapper.toDtoList(subjectService.findAll());
    }

    @GetMapping("/{subjectId}/contents")
    public List<ContentDTO> findContentsById(@PathVariable Long subjectId) {
        return subjectService.findContentsById(subjectId);
    }

}
