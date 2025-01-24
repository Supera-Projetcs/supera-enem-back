package com.supera.enem.service;

import com.supera.enem.domain.Student;
import com.supera.enem.exception.ResourceUnauthorizedException;
import com.supera.enem.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedService {

    @Autowired
    private StudentRepository studentRepository;

    public Student getAuthenticatedStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String keycloakId = jwt.getClaim("sub");
            return studentRepository.findByKeycloakId(keycloakId);
        } else {
            throw new ResourceUnauthorizedException("Usuário não autorizado");
        }
    }

}
