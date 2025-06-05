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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import ztpai.proj.TrainGymAppCalendarBackend.dto.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean UserService userService;

    User mockUser;
    UserResponseDto mockUserDto;
    UserResponseDto adminDto;
    User adminUser;

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
        mockUser.setMail("user@test.pl");
        mockUser.setName("Test User");
        mockUser.setAdmin(false);

        mockUserDto = new UserResponseDto();
        mockUserDto.setId(1);
        mockUserDto.setName("Test User");
        mockUserDto.setMail("user@test.pl");
        mockUserDto.setTrainer(false);
        mockUserDto.setAdmin(false);

        adminUser = new User();
        adminUser.setId(10);
        adminUser.setMail("admin@test.pl");
        adminUser.setName("Admin User");
        adminUser.setAdmin(true);

        adminDto = new UserResponseDto();
        adminDto.setId(10);
        adminDto.setName("Admin User");
        adminDto.setMail("admin@test.pl");
        adminDto.setAdmin(true);
        adminDto.setTrainer(false);

        when(userService.findByMail(eq(mockUser.getMail()))).thenReturn(Optional.of(mockUser));
        when(userService.findByMail(eq(adminUser.getMail()))).thenReturn(Optional.of(adminUser));
        when(userService.toUserResponseDto(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0, User.class);
            UserResponseDto dto = new UserResponseDto();
            dto.setId(u.getId());
            dto.setName(u.getName());
            dto.setMail(u.getMail());
            dto.setAdmin(u.getAdmin());
            dto.setTrainer(u.getTrainer());
            return dto;
        });
    }

    void setAuth(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getMail(), "haslo");
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldReturnAllUsersIfAdmin() throws Exception {
        setAuth(adminUser);
        when(userService.findAll()).thenReturn(List.of(adminUser, mockUser));

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(adminUser.getId()))
                .andExpect(jsonPath("$[1].id").value(mockUser.getId()));
    }

    @Test
    void shouldReturnForbiddenForNonAdminWhenGetAllUsers() throws Exception {
        setAuth(mockUser);

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnMe() throws Exception {
        setAuth(mockUser);
        when(userService.findByMail(eq(mockUser.getMail()))).thenReturn(Optional.of(mockUser));
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUser.getId()));
    }

    @Test
    void shouldReturn401IfNoAuthForMe() throws Exception {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnMeById() throws Exception {
        setAuth(mockUser);
        when(userService.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        mockMvc.perform(get("/api/user/" + mockUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUser.getId()));
    }

    @Test
    void shouldReturnForbiddenWhenAskOtherUserById() throws Exception {
        setAuth(mockUser);
        mockMvc.perform(get("/api/user/777"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenGetNotExistingById() throws Exception {
        setAuth(mockUser);
        when(userService.findById(mockUser.getId())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/user/" + mockUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteOwnAccount() throws Exception {
        setAuth(mockUser);
        when(userService.deleteUserById(mockUser.getId())).thenReturn(true);
        mockMvc.perform(delete("/api/user/" + mockUser.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnForbiddenWhenDeleteOtherUser() throws Exception {
        setAuth(mockUser);
        mockMvc.perform(delete("/api/user/8888"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteNotExisting() throws Exception {
        setAuth(mockUser);
        when(userService.deleteUserById(mockUser.getId())).thenReturn(false);
        mockMvc.perform(delete("/api/user/" + mockUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUserRolesAsAdmin() throws Exception {
        setAuth(adminUser);
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("nowa nazwa");
        dto.setAdmin(false);
        dto.setTrainer(false);

        when(userService.updateUserRoles(eq(mockUser.getId()), any(UserUpdateDto.class))).thenReturn(mockUserDto);

        mockMvc.perform(patch("/api/user/updateRoles/" + mockUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(request -> {
                            request.addHeader("Authorization", "Bearer admin");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUser.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateUserRolesNotExist() throws Exception {
        setAuth(adminUser);
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("nowa nazwa");
        when(userService.updateUserRoles(eq(9999), any(UserUpdateDto.class))).thenReturn(null);

        mockMvc.perform(patch("/api/user/updateRoles/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUserByIdAdmAsAdmin() throws Exception {
        setAuth(adminUser);
        when(userService.deleteUserById(mockUser.getId())).thenReturn(true);

        mockMvc.perform(delete("/api/user/deleteAdm/" + mockUser.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteUserAdmNotExist() throws Exception {
        setAuth(adminUser);
        when(userService.deleteUserById(mockUser.getId())).thenReturn(false);

        mockMvc.perform(delete("/api/user/deleteAdm/" + mockUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldJoinGroup() throws Exception {
        setAuth(mockUser);
        JoinGroupDto dto = new JoinGroupDto();
        dto.setTrainerEmail("trener@siema.pl");

        doNothing().when(userService).joinGroup(eq(dto.getTrainerEmail()), eq(mockUser));

        mockMvc.perform(post("/api/user/joinGroup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
