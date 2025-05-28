package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.dto.RecommendedTrainingDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.ScheduleRecommendedDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingCreateDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingResponseDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.RecommendedTrainings;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.service.RecommendedTrainingService;
import ztpai.proj.TrainGymAppCalendarBackend.service.TrainingService;
import ztpai.proj.TrainGymAppCalendarBackend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/recommended-trainings")
@CrossOrigin
public class RecommendedTrainingController {

    private final RecommendedTrainingService recService;
    private final UserService userService;
    private final TrainingService trainingService;

    public RecommendedTrainingController(RecommendedTrainingService recService,
                                         UserService userService,
                                         TrainingService trainingService) {
        this.recService = recService;
        this.userService = userService;
        this.trainingService = trainingService;
    }

    @GetMapping("")
    public List<RecommendedTrainingDto> getAll() {
        return recService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendedTrainingDto> getById(@PathVariable Integer id) {
        return recService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{id}/schedule")
    public ResponseEntity<TrainingResponseDto> schedule(
            @PathVariable Integer id,
            @Valid @RequestBody ScheduleRecommendedDto dto
    ) {
        User user = getCurrentUser();

        // 1) pobierz szablon
        RecommendedTrainings template = recService.findEntityById(id)
            .orElseThrow(() -> new RuntimeException("Szablon nie istnieje"));

        // 2) przygotuj TrainingCreateDto
        TrainingCreateDto create = new TrainingCreateDto();
        create.setName(template.getName());
        create.setDescription(template.getDescription());
        create.setTrainingDate(dto.getTrainingDate());
        create.setAskTrainer(false);  // z szablonu zawsze accepted

        // 3) deleguj do TrainingService
        TrainingResponseDto saved = trainingService.createTraining(user.getId(), create);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/assign-to-user")
    public ResponseEntity<Void> assignAllToUser(Authentication auth) {
        User user = userService.findByMail(auth.getName())
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika.")
                );

        recService.assignAllRecommendedToUser(user.getId());
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByMail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
    }
}
