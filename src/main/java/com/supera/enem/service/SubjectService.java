package com.supera.enem.service;

import com.supera.enem.controller.DTOS.ContentDTO;
import com.supera.enem.domain.Subject;
import com.supera.enem.mapper.ContentMapper;
import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentMapper contentMapper;

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public List<ContentDTO> findContentsById(Long id) {
        return contentMapper.toDtoList(contentRepository.findContentsBySubjectId(id));
    }

}
