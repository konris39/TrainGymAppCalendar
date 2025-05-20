package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.dto.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.service.TrainingService;

import java.util.List;

@RestController
@RequestMapping("/api/training")
@CrossOrigin
public class TrainingController {

    private final TrainingService trainingService;
    private final UserRepository userRepository;

    public TrainingController(TrainingService trainingService, UserRepository userRepository) {
        this.trainingService = trainingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public List<TrainingResponseDto> getMyTrainings() {
        User currentUser = getCurrentUser();
        return trainingService.getAllUserTrainings(currentUser.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingResponseDto> getMyTrainingById(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        return trainingService.getUserTrainingById(currentUser.getId(), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<TrainingResponseDto> addTrainingToUser(@Valid @RequestBody TrainingCreateDto dto) {
        User currentUser = getCurrentUser();
        TrainingResponseDto response = trainingService.createTraining(currentUser.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserTrainingById(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        boolean deleted = trainingService.deleteTraining(currentUser.getId(), id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<TrainingResponseDto> updateUserTraining(
            @Valid @RequestBody TrainingUpdateDto dto,
            @PathVariable Integer id
    ) {
        User currentUser = getCurrentUser();
        return trainingService.updateTraining(currentUser.getId(), id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/complete/{id}")
    public ResponseEntity<TrainingResponseDto> completeUserTraining(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        return trainingService.completeTraining(currentUser.getId(), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak zalogowanego użytkownika.");
        }
        String email = auth.getName();
        return userRepository.findByMail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika."));
    }
}
