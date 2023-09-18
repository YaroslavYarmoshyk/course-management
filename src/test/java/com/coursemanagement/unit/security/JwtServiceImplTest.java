package com.coursemanagement.unit.security;

import com.coursemanagement.security.service.impl.JwtServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Set;

import static com.coursemanagement.util.TestDataUtils.FIRST_STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
class JwtServiceImplTest {
    @InjectMocks
    private JwtServiceImpl jwtService;
    @Mock
    private Authentication authentication;
    @Mock
    private JwtEncoder jwtEncoder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "expirationTime", 24);
    }

    @Test
    @DisplayName("Test jwt generation")
    void testJwtGeneration() {
        final String email = FIRST_STUDENT.getEmail();
        final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
        final ArgumentCaptor<JwtEncoderParameters> jwtEncoderParametersArgumentCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        final Jwt jwt = Instancio.create(Jwt.class);

        when(jwtEncoder.encode(any())).thenReturn(jwt);
        doReturn(email).when(authentication).getName();
        doReturn(authorities).when(authentication).getAuthorities();

        jwtService.generateJwt(authentication);

        verify(jwtEncoder).encode(jwtEncoderParametersArgumentCaptor.capture());
        final JwtEncoderParameters jwtParams = jwtEncoderParametersArgumentCaptor.getValue();
        final Map<String, Object> claims = jwtParams.getClaims().getClaims();
        assertEquals("self", claims.get("iss"));
        assertEquals(email, claims.get("sub"));
        assertEquals("ROLE_STUDENT", claims.get("roles"));
    }
}