package ztpai.proj.TrainGymAppCalendarBackend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ztpai.proj.TrainGymAppCalendarBackend.dto.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDto register(UserRegisterDto dto) {
        if (userRepository.findByMail(dto.getMail()).isPresent()) {
            throw new RuntimeException("Użytkownik o podanym mailu już istnieje!");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setMail(dto.getMail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setTrainer(false);
        user.setAdmin(false);

        User savedUser = userRepository.save(user);
        return toUserResponseDto(savedUser);
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean deleteUserById(Integer id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public UserResponseDto updateUserRoles(Integer id, UserUpdateDto dto) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(dto.getName());
            if(dto.getTrainer() != null) user.setTrainer(dto.getTrainer());
            if(dto.getAdmin() != null) user.setAdmin(dto.getAdmin());
            User updated = userRepository.save(user);
            return toUserResponseDto(updated);
        }
        return null;
    }

    @Transactional
    public UserResponseDto updateUser(Integer userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(dto.getName());
        user.setMail(dto.getMail());
        User updatedUser = userRepository.save(user);

        return toUserResponseDto(updatedUser);
    }

    public UserResponseDto toUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setMail(user.getMail());
        dto.setTrainer(user.getTrainer());
        dto.setAdmin(user.getAdmin());
        return dto;
    }
}
