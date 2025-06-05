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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/recommended-trainings")
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

    @Operation(
            summary = "Pobierz wszystkie rekomendowane treningi",
            description = "Zwraca listę wszystkich dostępnych szablonów rekomendowanych treningów (RecommendedTrainingDto).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista rekomendowanych treningów",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = RecommendedTrainingDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Błąd serwera podczas pobierania szablonów",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("")
    public List<RecommendedTrainingDto> getAll() {
        return recService.findAll();
    }

    @Operation(
            summary = "Pobierz rekomendowany trening po ID",
            description = "Zwraca pojedynczy szablon rekomendowanego treningu (RecommendedTrainingDto) na podstawie przekazanego identyfikatora.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID szablonu rekomendowanego treningu",
                            required = true,
                            schema = @Schema(type = "integer", example = "1")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Znaleziono szablon rekomendowanego treningu",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RecommendedTrainingDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono szablonu o podanym ID",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Nieprawidłowy format ID",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<RecommendedTrainingDto> getById(@PathVariable Integer id) {
        return recService.findByIdDto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Zaplanuj trening na podstawie rekomendowanego szablonu",
            description = "Na podstawie wskazanego szablonu (ID) tworzy nowy trening (TrainingResponseDto) dla aktualnie zalogowanego użytkownika. "
                    + "Data wykonania treningu pobierana jest z pola `trainingDate` w ScheduleRecommendedDto. "
                    + "Jeśli szablon nie istnieje, zwraca 404.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID szablonu rekomendowanego treningu",
                            required = true,
                            schema = @Schema(type = "integer", example = "1")
                    )
            },
            requestBody = @RequestBody(
                    description = "Obiekt JSON zawierający pole `trainingDate` (ScheduleRecommendedDto)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleRecommendedDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Pomyślnie utworzono nowy trening na podstawie rekomendacji",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrainingResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono szablonu rekomendowanego treningu o podanym ID",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Błąd walidacji danych wejściowych (np. brak `trainingDate`)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany (brak tokena lub sesji)",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @PostMapping("/{id}/schedule")
    public ResponseEntity<TrainingResponseDto> schedule(
            @PathVariable Integer id,
            @Valid
            // TU musi być SPRINGOWE @RequestBody (z pakietu org.springframework.web.bind.annotation)
            @org.springframework.web.bind.annotation.RequestBody ScheduleRecommendedDto dto
    ) {
        User user = getCurrentUser();

        RecommendedTrainings template = recService.findEntityById(id)
                .orElseThrow(() -> new RuntimeException("Szablon nie istnieje"));

        TrainingCreateDto create = new TrainingCreateDto();
        create.setName(template.getName());
        create.setDescription(template.getDescription());
        create.setTrainingDate(dto.getTrainingDate());
        create.setAskTrainer(false);

        TrainingResponseDto saved = trainingService.createTraining(user.getId(), create);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
            summary = "Przypisz wszystkie rekomendowane treningi do użytkownika",
            description = "Dla zalogowanego użytkownika przypisuje wszystkie dostępne szablony rekomendowanych treningów.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pomyślnie przypisano wszystkie rekomendowane treningi do użytkownika",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono zalogowanego użytkownika w bazie",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany (brak tokena lub sesji)",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
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
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak zalogowanego użytkownika.");
        }
        String email = auth.getName();
        return userService.findByMail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika."
                ));
    }
}
