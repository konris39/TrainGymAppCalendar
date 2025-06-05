package ztpai.proj.TrainGymAppCalendarBackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import ztpai.proj.TrainGymAppCalendarBackend.dto.DataUserResponseDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.DataUserUpdateDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.UserDataDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.service.DataUserService;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataUserController.class)
class DataUserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DataUserService dataUserService;

    @MockBean
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    User mockUser;
    UsernamePasswordAuthenticationToken authentication;
    DataUserResponseDto mockResponseDto;

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(11);
        mockUser.setName("TestUser");
        mockUser.setMail("testuser@mail.com");

        authentication = new UsernamePasswordAuthenticationToken(mockUser.getMail(), "haslo", java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(mockUser));

        mockResponseDto = new DataUserResponseDto();
        mockResponseDto.setId(7);
        mockResponseDto.setWeight(70.5);
        mockResponseDto.setHeight(180.0);
        mockResponseDto.setAge(25);
        mockResponseDto.setBmi(21.76);
        mockResponseDto.setBp(100.0);
        mockResponseDto.setSq(120.0);
        mockResponseDto.setDl(130.0);
        mockResponseDto.setSum(350.0);
        UserDataDto userData = new UserDataDto();
        userData.setId(mockUser.getId());
        userData.setName(mockUser.getName());
        userData.setMail(mockUser.getMail());
        mockResponseDto.setUser(userData);
    }

    @Test
    void shouldReturnUserDataWhenExists() throws Exception {
        when(dataUserService.getByUserId(mockUser.getId())).thenReturn(Optional.of(mockResponseDto));

        mockMvc.perform(get("/api/data/my")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockResponseDto.getId()))
                .andExpect(jsonPath("$.weight").value(mockResponseDto.getWeight()))
                .andExpect(jsonPath("$.user.name").value(mockUser.getName()));
    }

    @Test
    void shouldReturnNotFoundIfNoUserData() throws Exception {
        when(dataUserService.getByUserId(mockUser.getId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/data/my")
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404IfNoAuthentication() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/data/my"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUserData() throws Exception {
        DataUserUpdateDto updateDto = new DataUserUpdateDto();
        updateDto.setWeight(77.0);
        updateDto.setHeight(183.0);

        when(dataUserService.updateByUserId(eq(mockUser.getId()), any(DataUserUpdateDto.class)))
                .thenReturn(Optional.of(mockResponseDto));

        mockMvc.perform(patch("/api/data/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockResponseDto.getId()))
                .andExpect(jsonPath("$.weight").value(mockResponseDto.getWeight()));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateNonexistent() throws Exception {
        DataUserUpdateDto updateDto = new DataUserUpdateDto();
        updateDto.setWeight(77.0);

        when(dataUserService.updateByUserId(eq(mockUser.getId()), any(DataUserUpdateDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/data/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404OnInvalidUpdateRequest() throws Exception {
        mockMvc.perform(patch("/api/data/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenUpdateNoAuthentication() throws Exception {
        SecurityContextHolder.clearContext();

        DataUserUpdateDto updateDto = new DataUserUpdateDto();
        updateDto.setWeight(80.0);

        mockMvc.perform(patch("/api/data/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenUserNotFoundInRepo() throws Exception {
        when(userRepository.findByMail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/data/my")
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }
}