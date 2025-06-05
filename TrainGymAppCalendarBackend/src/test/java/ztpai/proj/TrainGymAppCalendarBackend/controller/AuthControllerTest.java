package ztpai.proj.TrainGymAppCalendarBackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import ztpai.proj.TrainGymAppCalendarBackend.models.DataUser;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.DataUserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.security.JwtUtil;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private DataUserRepository dataUserRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setName("Test");
        testUser.setMail("test@test.pl");
        testUser.setPassword("encodedPassword");
        testUser.setTrainer(false);
        testUser.setAdmin(false);
    }

    @Test
    void shouldNotLoginWithBadCredentials() throws Exception {
        when(userRepository.findByMail(anyString())).thenReturn(Optional.empty());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMail("wrong@test.pl");
        loginRequest.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRefreshToken() throws Exception {
        String refreshToken = "refreshToken";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(testUser.getMail())
                .password(testUser.getPassword())
                .roles("USER")
                .build();

        when(jwtUtil.extractUsernameFromRefresh(refreshToken)).thenReturn("test@test.pl");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtUtil.validateRefreshToken(eq(refreshToken), any())).thenReturn(true);
        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any(User.class))).thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn(newRefreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("accessToken")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("newAccessToken")));
    }

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenInvalid() throws Exception {
        String refreshToken = "badToken";

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(testUser.getMail())
                .password(testUser.getPassword())
                .roles("USER")
                .build();

        when(jwtUtil.extractUsernameFromRefresh(refreshToken)).thenReturn("test@test.pl");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtUtil.validateRefreshToken(eq(refreshToken), any())).thenReturn(false);

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLogoutUserAndClearCookies() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")));
    }
}
