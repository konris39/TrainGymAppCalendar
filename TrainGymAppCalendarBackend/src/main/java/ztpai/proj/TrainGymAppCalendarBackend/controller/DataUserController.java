package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.dto.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.service.DataUserService;

@RestController
@RequestMapping("api/data")
public class DataUserController {

    private final DataUserService dataUserService;
    private final UserRepository userRepository;

    public DataUserController(DataUserService dataUserService, UserRepository userRepository) {
        this.dataUserService = dataUserService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<DataUserResponseDto> getMyData() {
        User currentUser = getCurrentUser();
        return dataUserService.getByUserId(currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/update")
    public ResponseEntity<DataUserResponseDto> updateUserDataByUserId(@Valid @RequestBody DataUserUpdateDto dto) {
        User currentUser = getCurrentUser();
        return dataUserService.updateByUserId(currentUser.getId(), dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Brak zalogowanego użytkownika.");
        }
        String email = auth.getName();
        return userRepository.findByMail(email)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika."));
    }
}
