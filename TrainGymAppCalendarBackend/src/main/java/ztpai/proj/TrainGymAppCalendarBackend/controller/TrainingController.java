package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.models.Training;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.TrainingRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/training")
@CrossOrigin
public class TrainingController {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    public TrainingController(TrainingRepository trainingRepository, UserRepository userRepository) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my")
    public List<Training> getMyTrainings() {
        User currentUser = getCurrentUser();
        return trainingRepository.findAllByUserId(currentUser.getId());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<Training> getMyTrainingById(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        Optional<Training> trainingOptional = trainingRepository.findById(id);
        if (!trainingOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Training training = trainingOptional.get();
        if(training.getUser() == null || !training.getUser().getId().equals(currentUser.getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(training);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public ResponseEntity<Training> addTrainingToUser(@Valid @RequestBody Training training) {
        User currentUser = getCurrentUser();
        training.setUser(currentUser);
        Training savedTraining = trainingRepository.save(training);
        return new ResponseEntity<>(savedTraining, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserTrainingById(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        Optional<Training> trainingOptional = trainingRepository.findById(id);
        if (!trainingOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Training training = trainingOptional.get();
        if (training.getUser() == null || !training.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        trainingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Training> updateUserTraining(@Valid @RequestBody Training training, @PathVariable Integer id) {
        User currentUser = getCurrentUser();
        Optional<Training> trainingOptional = trainingRepository.findById(id);
        if (!trainingOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Training trainingToUpdate = trainingOptional.get();
        if (trainingToUpdate.getUser() == null || !trainingToUpdate.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (training.getName() != null && !training.getName().trim().isEmpty()) {
            trainingToUpdate.setName(training.getName());
        }
        if (training.getDescription() != null && !training.getDescription().trim().isEmpty()) {
            trainingToUpdate.setDescription(training.getDescription());
        }
        if (training.getTrainingDate() != null) {
            trainingToUpdate.setTrainingDate(training.getTrainingDate());
        }

        trainingRepository.save(trainingToUpdate);
        return ResponseEntity.ok(trainingToUpdate);
    }

    @PatchMapping("/complete/{id}")
    public ResponseEntity<Training> completeUserTraining(@Valid @RequestBody Training training, @PathVariable Integer id) {
        User currentUser = getCurrentUser();
        Optional<Training> trainingOptional = trainingRepository.findById(id);
        if (!trainingOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Training trainingToUpdate = trainingOptional.get();
        if (trainingToUpdate.getUser() == null || !trainingToUpdate.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        trainingToUpdate.setCompleted(true);
        trainingRepository.save(trainingToUpdate);
        return ResponseEntity.ok(trainingToUpdate);
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
