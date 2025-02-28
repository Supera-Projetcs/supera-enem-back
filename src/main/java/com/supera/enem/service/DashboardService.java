package com.supera.enem.service;

import com.supera.enem.domain.Student;
import com.supera.enem.repository.AnswerRepository;
import com.supera.enem.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private AuthenticatedService authenticatedService;

    public long getNumberOfRightAnswers() {
        Student student = authenticatedService.getAuthenticatedStudent();
        return answerRepository.countCorrectAnswersByStudentId(student.getId()).intValue();
    }

    public long getNumberOfWrongAnswers() {
        Student student = authenticatedService.getAuthenticatedStudent();
        return answerRepository.countWrongAnswersByStudentId(student.getId());
    }

    public long getNumberOfTests() {
        Student student = authenticatedService.getAuthenticatedStudent();
        return testRepository.countByStudent(student);
    }

    public double getHitRate() {
        long numberOfRightAnswers = getNumberOfRightAnswers();
        long numberOfWrongAnswers = getNumberOfWrongAnswers();
        if (numberOfRightAnswers + numberOfWrongAnswers == 0) {
            return 0;
        }
        return (double) numberOfRightAnswers / (numberOfRightAnswers + numberOfWrongAnswers);
    }
}
