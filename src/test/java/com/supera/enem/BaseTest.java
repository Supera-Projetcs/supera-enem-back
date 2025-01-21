package com.supera.enem;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.mockito.Mockito.*;

public class BaseTest {

    @Mock
    protected SecurityContext securityContext;

    @Mock
    protected Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    protected void mockAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakIdMock");
        when(authentication.getPrincipal()).thenReturn(jwt);
    }

    protected void mockUnauthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
    }
}
