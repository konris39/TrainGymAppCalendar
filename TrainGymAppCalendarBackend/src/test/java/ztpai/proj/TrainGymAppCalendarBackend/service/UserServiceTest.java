package ztpai.proj.TrainGymAppCalendarBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import ztpai.proj.TrainGymAppCalendarBackend.dto.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.models.UserGroup;
import ztpai.proj.TrainGymAppCalendarBackend.repository.GroupRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    GroupRepository groupRepository;

    UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        groupRepository = mock(GroupRepository.class);
        userService = new UserService(userRepository, passwordEncoder, groupRepository);
    }

    @Test
    void shouldRegisterUser() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Jan");
        dto.setMail("jan@nowak.pl");
        dto.setPassword("tajne");

        when(userRepository.findByMail("jan@nowak.pl")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("tajne")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1);
            return u;
        });

        UserResponseDto resp = userService.register(dto);

        assertThat(resp.getId()).isEqualTo(1);
        assertThat(resp.getName()).isEqualTo("Jan");
        assertThat(resp.getMail()).isEqualTo("jan@nowak.pl");
        verify(passwordEncoder).encode("tajne");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowIfRegisterWithExistingMail() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setMail("test@wp.pl");

        when(userRepository.findByMail("test@wp.pl")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Użytkownik o podanym mailu już istnieje");
    }

    @Test
    void shouldFindUserByIdAndMail() {
        User user = new User();
        user.setId(123);
        user.setMail("a@b.pl");

        when(userRepository.findById(123)).thenReturn(Optional.of(user));
        when(userRepository.findByMail("a@b.pl")).thenReturn(Optional.of(user));

        assertThat(userService.findById(123)).contains(user);
        assertThat(userService.findByMail("a@b.pl")).contains(user);
    }

    @Test
    void shouldReturnAllUsers() {
        User u1 = new User(); u1.setId(1);
        User u2 = new User(); u2.setId(2);
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<User> users = userService.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void shouldDeleteUserByIdIfExists() {
        when(userRepository.existsById(12)).thenReturn(true);

        boolean deleted = userService.deleteUserById(12);

        assertThat(deleted).isTrue();
        verify(userRepository).deleteById(12);
    }

    @Test
    void shouldNotDeleteUserByIdIfNotExists() {
        when(userRepository.existsById(7)).thenReturn(false);

        boolean deleted = userService.deleteUserById(7);

        assertThat(deleted).isFalse();
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldUpdateUserRoles() {
        User user = new User();
        user.setId(10);
        user.setName("Zbyszek");
        user.setTrainer(false);
        user.setAdmin(false);

        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Zbysiu");
        dto.setTrainer(true);
        dto.setAdmin(true);

        when(userRepository.findById(10)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDto resp = userService.updateUserRoles(10, dto);

        assertThat(resp.getName()).isEqualTo("Zbysiu");
        assertThat(resp.isTrainer()).isTrue();
        assertThat(resp.isAdmin()).isTrue();
    }

    @Test
    void shouldReturnNullIfUpdateUserRolesNotFound() {
        UserUpdateDto dto = new UserUpdateDto();
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        UserResponseDto resp = userService.updateUserRoles(999, dto);

        assertThat(resp).isNull();
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setId(1);
        user.setName("Adam");
        user.setMail("a@b.pl");

        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Ewa");
        dto.setMail("e@f.pl");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDto resp = userService.updateUser(1, dto);

        assertThat(resp.getName()).isEqualTo("Ewa");
        assertThat(resp.getMail()).isEqualTo("e@f.pl");
    }

    @Test
    void shouldThrowIfUpdateUserNotFound() {
        UserUpdateDto dto = new UserUpdateDto();
        when(userRepository.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(404, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldUpdateUserNameOnly() {
        User user = new User();
        user.setId(1);
        user.setName("Janek");

        UserAdminUpdateDto dto = new UserAdminUpdateDto();
        dto.setName("Grzegorz");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDto resp = userService.updateUserNameOnly(1, dto);

        assertThat(resp.getName()).isEqualTo("Grzegorz");
    }

    @Test
    void shouldThrowIfUpdateUserNameOnlyNotFound() {
        UserAdminUpdateDto dto = new UserAdminUpdateDto();
        when(userRepository.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserNameOnly(404, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldJoinGroup() {
        User trainer = new User(); trainer.setId(5); trainer.setMail("t@tr.pl");
        User currentUser = new User(); currentUser.setId(10);

        when(userRepository.findByMail("t@tr.pl")).thenReturn(Optional.of(trainer));

        userService.joinGroup("t@tr.pl", currentUser);

        ArgumentCaptor<UserGroup> captor = ArgumentCaptor.forClass(UserGroup.class);
        verify(groupRepository).save(captor.capture());
        UserGroup saved = captor.getValue();
        assertThat(saved.getTrainer()).isEqualTo(trainer);
        assertThat(saved.getUser()).isEqualTo(currentUser);
    }

    @Test
    void shouldThrowIfTrainerNotFoundWhenJoinGroup() {
        User currentUser = new User();
        when(userRepository.findByMail("not@found.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.joinGroup("not@found.com", currentUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Nie znaleziono trenera");
    }

    @Test
    void shouldMapUserToUserResponseDto() {
        User user = new User();
        user.setId(1);
        user.setName("Tester");
        user.setMail("t@t.pl");
        user.setTrainer(true);
        user.setAdmin(false);

        UserResponseDto dto = userService.toUserResponseDto(user);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Tester");
        assertThat(dto.getMail()).isEqualTo("t@t.pl");
        assertThat(dto.isTrainer()).isTrue();
        assertThat(dto.isAdmin()).isFalse();
    }
}
