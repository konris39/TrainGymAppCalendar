package ztpai.proj.TrainGymAppCalendarBackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import ztpai.proj.TrainGymAppCalendarBackend.dto.RecommendedTrainingDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.ScheduleRecommendedDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingCreateDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingResponseDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.RecommendedTrainings;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.service.RecommendedTrainingService;
import ztpai.proj.TrainGymAppCalendarBackend.service.TrainingService;
import ztpai.proj.TrainGymAppCalendarBackend.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendedTrainingController.class)
class RecommendedTrainingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RecommendedTrainingService recService;
    @MockBean
    UserService userService;
    @MockBean
    TrainingService trainingService;

    @Autowired
    ObjectMapper objectMapper;

    User mockUser;
    RecommendedTrainings mockRecTraining;
    RecommendedTrainingDto mockRecTrainingDto;
    TrainingResponseDto mockTrainingResponseDto;

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setMail("test@test.pl");
        mockUser.setName("Test");

        mockRecTraining = new RecommendedTrainings();
        mockRecTraining.setId(10);
        mockRecTraining.setName("Szablon push");
        mockRecTraining.setDescription("Opis szablonu");

        mockRecTrainingDto = new RecommendedTrainingDto();
        mockRecTrainingDto.setId(10);
        mockRecTrainingDto.setName("Szablon push");
        mockRecTrainingDto.setDescription("Opis szablonu");

        mockTrainingResponseDto = new TrainingResponseDto();
        mockTrainingResponseDto.setId(222);
        mockTrainingResponseDto.setName("Trening A");
        mockTrainingResponseDto.setDescription("Opis treningu");
        mockTrainingResponseDto.setTrainingDate(LocalDate.now());
        mockTrainingResponseDto.setCompleted(false);
        mockTrainingResponseDto.setAccepted(true);
    }

    @Test
    void shouldReturnAllRecommendedTrainings() throws Exception {
        when(recService.findAll()).thenReturn(List.of(mockRecTrainingDto));

        mockMvc.perform(get("/api/recommended-trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockRecTrainingDto.getId()))
                .andExpect(jsonPath("$[0].name").value(mockRecTrainingDto.getName()));
    }

    @Test
    void shouldReturnRecommendedTrainingById() throws Exception {
        when(recService.findByIdDto(10)).thenReturn(Optional.of(mockRecTrainingDto));

        mockMvc.perform(get("/api/recommended-trainings/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void shouldReturn404WhenRecommendedTrainingNotFound() throws Exception {
        when(recService.findByIdDto(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recommended-trainings/12345"))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturn400WhenMissingTrainingDate() throws Exception {
        ScheduleRecommendedDto dto = new ScheduleRecommendedDto();

        when(userService.findByMail(anyString())).thenReturn(Optional.of(mockUser));
        when(recService.findEntityById(eq(10))).thenReturn(Optional.of(mockRecTraining));

        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUser.getMail(), "haslo");
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(post("/api/recommended-trainings/10/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

}
