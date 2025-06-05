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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingCreateDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingResponseDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingUpdateDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.Training;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.models.UserGroup;
import ztpai.proj.TrainGymAppCalendarBackend.repository.GroupRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.TrainingRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.service.TrainingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TrainingService trainingService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    GroupRepository groupRepository;
    @MockBean
    TrainingRepository trainingRepository;

    @Autowired
    ObjectMapper objectMapper;

    User mockUser;
    TrainingResponseDto mockTrainingDto;
    UsernamePasswordAuthenticationToken authentication;

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
        mockUser.setId(1);
        mockUser.setMail("test@test.pl");
        mockUser.setName("Test");

        mockTrainingDto = new TrainingResponseDto();
        mockTrainingDto.setId(1);
        mockTrainingDto.setName("Trening A");
        mockTrainingDto.setDescription("Opis");
        mockTrainingDto.setTrainingDate(LocalDate.now());
        mockTrainingDto.setCompleted(false);
        mockTrainingDto.setAccepted(true);

        authentication = new UsernamePasswordAuthenticationToken(mockUser.getMail(), "haslo", List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(mockUser));
    }

    @Test
    void shouldReturnMyTrainings() throws Exception {
        when(trainingService.getAllUserTrainings(eq(mockUser.getId())))
                .thenReturn(List.of(mockTrainingDto));

        mockMvc.perform(get("/api/training/my")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockTrainingDto.getId()))
                .andExpect(jsonPath("$[0].name").value(mockTrainingDto.getName()));
    }

    @Test
    void shouldReturnMyTrainingById() throws Exception {
        when(trainingService.getUserTrainingById(eq(mockUser.getId()), eq(1)))
                .thenReturn(Optional.of(mockTrainingDto));

        mockMvc.perform(get("/api/training/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockTrainingDto.getId()))
                .andExpect(jsonPath("$.name").value(mockTrainingDto.getName()));
    }

    @Test
    void shouldReturn404IfTrainingNotFound() throws Exception {
        when(trainingService.getUserTrainingById(eq(mockUser.getId()), eq(999)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/training/999")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteTraining() throws Exception {
        when(trainingService.deleteTraining(eq(mockUser.getId()), eq(1))).thenReturn(true);

        mockMvc.perform(delete("/api/training/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteNonexistent() throws Exception {
        when(trainingService.deleteTraining(eq(mockUser.getId()), eq(123))).thenReturn(false);

        mockMvc.perform(delete("/api/training/123")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateTraining() throws Exception {
        TrainingUpdateDto updateDto = new TrainingUpdateDto();
        updateDto.setName("Updated");

        when(trainingService.updateTraining(eq(mockUser.getId()), eq(1), any(TrainingUpdateDto.class)))
                .thenReturn(Optional.of(mockTrainingDto));

        mockMvc.perform(patch("/api/training/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockTrainingDto.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateNonexistent() throws Exception {
        TrainingUpdateDto updateDto = new TrainingUpdateDto();
        updateDto.setName("Updated");

        when(trainingService.updateTraining(eq(mockUser.getId()), eq(999), any(TrainingUpdateDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/training/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCompleteTraining() throws Exception {
        when(trainingService.completeTraining(eq(mockUser.getId()), eq(1)))
                .thenReturn(Optional.of(mockTrainingDto));

        mockMvc.perform(patch("/api/training/complete/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockTrainingDto.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenCompleteNonexistent() throws Exception {
        when(trainingService.completeTraining(eq(mockUser.getId()), eq(999)))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/training/complete/999")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn403IfTrainerEndpointCalledByNonTrainer() throws Exception {
        mockUser.setTrainer(false);
        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/training/to-accept")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404IfTrainerNotFound() throws Exception {
        when(userRepository.findByMail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/training/to-accept")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAcceptTrainingWhenTrainerAndHasPermission() throws Exception {
        mockUser.setTrainer(true);

        User podopieczny = new User();
        podopieczny.setId(2);

        UserGroup userGroup = new UserGroup();
        userGroup.setTrainer(mockUser);
        userGroup.setUser(podopieczny);

        Training training = new Training();
        training.setId(55);
        training.setUser(podopieczny);
        training.setAccepted(false);

        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(mockUser));
        when(groupRepository.findAllByTrainerId(mockUser.getId())).thenReturn(List.of(userGroup));
        when(trainingRepository.findById(55)).thenReturn(Optional.of(training));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        mockMvc.perform(patch("/api/training/accept/55")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnForbiddenWhenAcceptTrainingNoPermission() throws Exception {
        mockUser.setTrainer(true);

        User podopieczny = new User();
        podopieczny.setId(2);

        UserGroup userGroup = new UserGroup();
        userGroup.setTrainer(mockUser);
        userGroup.setUser(podopieczny);

        Training training = new Training();
        training.setId(77);
        training.setUser(new User());

        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(mockUser));
        when(groupRepository.findAllByTrainerId(mockUser.getId())).thenReturn(List.of(userGroup));
        when(trainingRepository.findById(77)).thenReturn(Optional.of(training));

        mockMvc.perform(patch("/api/training/accept/77")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeclineTrainingWhenTrainerAndHasPermission() throws Exception {
        mockUser.setTrainer(true);

        User podopieczny = new User();
        podopieczny.setId(2);

        UserGroup userGroup = new UserGroup();
        userGroup.setTrainer(mockUser);
        userGroup.setUser(podopieczny);

        Training training = new Training();
        training.setId(88);
        training.setUser(podopieczny);

        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(mockUser));
        when(groupRepository.findAllByTrainerId(mockUser.getId())).thenReturn(List.of(userGroup));
        when(trainingRepository.findById(88)).thenReturn(Optional.of(training));

        mockMvc.perform(patch("/api/training/decline/88")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnForbiddenWhenDeclineTrainingNoPermission() throws Exception {
        mockUser.setTrainer(true);

        User podopieczny = new User();
        podopieczny.setId(2);

        UserGroup userGroup = new UserGroup();
        userGroup.setTrainer(mockUser);
        userGroup.setUser(podopieczny);

        Training training = new Training();
        training.setId(99);
        training.setUser(new User());

        when(userRepository.findByMail(anyString())).thenReturn(Optional.of(mockUser));
        when(groupRepository.findAllByTrainerId(mockUser.getId())).thenReturn(List.of(userGroup));
        when(trainingRepository.findById(99)).thenReturn(Optional.of(training));

        mockMvc.perform(patch("/api/training/decline/99")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isForbidden());
    }
}
