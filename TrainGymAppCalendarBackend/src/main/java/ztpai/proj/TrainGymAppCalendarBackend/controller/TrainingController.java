package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.dto.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.UserGroup;
import ztpai.proj.TrainGymAppCalendarBackend.models.Training;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.GroupRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.TrainingRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.service.TrainingService;

import java.util.List;

@RestController
@RequestMapping("/api/training")
@CrossOrigin
public class TrainingController {

    private final TrainingService trainingService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TrainingRepository trainingRepository;

    public TrainingController(TrainingService trainingService, UserRepository userRepository, GroupRepository groupRepository, TrainingRepository trainingRepository) {
        this.trainingService = trainingService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.trainingRepository = trainingRepository;
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


    @GetMapping("/to-accept")
    public List<TrainingResponseDto> getTrainingsToAccept(Authentication auth) {
    User currentUser = userRepository.findByMail(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika."));

    if (!currentUser.getTrainer()) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nie jesteś trenerem.");
    }

    List<UserGroup> groups = groupRepository.findAllByTrainerId(currentUser.getId());
    List<Integer> userIds = groups.stream().map(g -> g.getUser().getId()).toList();

    if (userIds.isEmpty()) {
        return List.of();
    }

    List<Training> toAccept = trainingRepository.findAllByUserIdInAndAcceptedFalse(userIds);
    return toAccept.stream()
            .map(trainingService::toTrainingResponseDto)
            .toList();
    }

    @PatchMapping("/accept/{id}")
    public ResponseEntity<Void> acceptTraining(@PathVariable Integer id, Authentication auth) {
    User currentUser = userRepository.findByMail(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika."));

    if (!currentUser.getTrainer()) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nie jesteś trenerem.");
    }

    List<UserGroup> groups = groupRepository.findAllByTrainerId(currentUser.getId());
    List<Integer> userIds = groups.stream().map(g -> g.getUser().getId()).toList();

    Training training = trainingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono treningu."));

    if (!userIds.contains(training.getUser().getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nie masz uprawnień do tego treningu.");
    }

    training.setAccepted(true);
    trainingRepository.save(training);

    return ResponseEntity.noContent().build();
    }

    @PatchMapping("/decline/{id}")
    public ResponseEntity<Void> declineTraining(@PathVariable Integer id, Authentication auth) {
    User currentUser = userRepository.findByMail(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika."));

    if (!currentUser.getTrainer()) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nie jesteś trenerem.");
    }

    List<UserGroup> groups = groupRepository.findAllByTrainerId(currentUser.getId());
    List<Integer> userIds = groups.stream().map(g -> g.getUser().getId()).toList();

    Training training = trainingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono treningu."));

    if (!userIds.contains(training.getUser().getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nie masz uprawnień do tego treningu.");
    }

    trainingRepository.deleteById(id);
    return ResponseEntity.noContent().build();
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
