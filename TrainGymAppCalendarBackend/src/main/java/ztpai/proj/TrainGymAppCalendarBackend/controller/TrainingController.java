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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/api/training")
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

    @Operation(
            summary = "Pobierz wszystkie treningi zalogowanego użytkownika",
            description = "Zwraca listę wszystkich treningów (TrainingResponseDto) przypisanych do aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista treningów użytkownika",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = TrainingResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany (brak tokena/sesji)",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("/my")
    public List<TrainingResponseDto> getMyTrainings() {
        User currentUser = getCurrentUser();
        return trainingService.getAllUserTrainings(currentUser.getId());
    }

    @Operation(
            summary = "Pobierz szczegóły pojedynczego treningu",
            description = "Na podstawie podanego ID zwraca szczegóły treningu (TrainingResponseDto) tylko jeżeli należy on do aktualnie zalogowanego użytkownika.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID treningu, który ma zostać pobrany",
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Znaleziono i zwrócono szczegóły treningu",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrainingResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono treningu o podanym ID lub nie należy on do użytkownika",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany (brak tokena/sesji)",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TrainingResponseDto> getMyTrainingById(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        return trainingService.getUserTrainingById(currentUser.getId(), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Dodaj nowy trening do konta użytkownika",
            description = "Tworzy nowy trening (TrainingResponseDto) dla zalogowanego użytkownika na podstawie przesłanego obiektu TrainingCreateDto.",
            requestBody = @RequestBody(
                    description = "Obiekt JSON zawierający `name`, opcjonalnie `description`, `trainingDate` oraz `askTrainer` (TrainingCreateDto)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainingCreateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Pomyślnie utworzono trening",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrainingResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Błąd walidacji danych wejściowych",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany (brak tokena/sesji)",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @PostMapping("/add")
    public ResponseEntity<TrainingResponseDto> addTrainingToUser(@Valid @RequestBody TrainingCreateDto dto) {
        User currentUser = getCurrentUser();
        TrainingResponseDto response = trainingService.createTraining(currentUser.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Usuń trening użytkownika",
            description = "Usuwa trening o podanym ID, jeśli należy on do aktualnie zalogowanego użytkownika.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID treningu do usunięcia",
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Trening został pomyślnie usunięty (brak treści)",
                            content = @Content(schema = @Schema(type = "void"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono treningu o podanym ID lub nie należy on do użytkownika",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany (brak tokena/sesji)",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserTrainingById(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        boolean deleted = trainingService.deleteTraining(currentUser.getId(), id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Aktualizuj istniejący trening użytkownika",
            description = "Modyfikuje pola treningu (TrainingResponseDto) dla zalogowanego użytkownika na podstawie przesłanego obiektu TrainingUpdateDto.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID treningu do zaktualizowania",
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            requestBody = @RequestBody(
                    description = "Obiekt JSON z polami do aktualizacji: `name`, `description`, `trainingDate` (TrainingUpdateDto)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainingUpdateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pomyślnie zaktualizowano trening",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrainingResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Błąd walidacji danych wejściowych lub nieprawidłowy format",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono treningu o podanym ID lub nie należy on do użytkownika",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany (brak tokena/sesji)",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @PatchMapping("/update/{id}")
    public ResponseEntity<TrainingResponseDto> updateUserTraining(@Valid @RequestBody TrainingUpdateDto dto, @PathVariable Integer id) {
        User currentUser = getCurrentUser();
        return trainingService.updateTraining(currentUser.getId(), id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Oznacz trening jako ukończony",
            description = "Ustawia pole `completed = true` dla treningu o podanym ID, który należy do zalogowanego użytkownika.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID treningu, który ma zostać oznaczony jako ukończony",
                            required = true,
                            schema = @Schema(type = "integer", example = "5")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pomyślnie oznaczono trening jako ukończony i zwrócono zaktualizowane dane",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrainingResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono treningu o podanym ID lub nie należy on do użytkownika",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany (brak tokena/sesji)",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @PatchMapping("/complete/{id}")
    public ResponseEntity<TrainingResponseDto> completeUserTraining(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        return trainingService.completeTraining(currentUser.getId(), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Pobierz listę treningów oczekujących na akceptację (dla trenera)",
            description = "Zwraca listę treningów (TrainingResponseDto) od użytkowników przypisanych do trenera, które jeszcze nie zostały zaakceptowane.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista treningów oczekujących na akceptację",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = TrainingResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Zalogowany użytkownik nie jest trenerem",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono zalogowanego użytkownika w bazie",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
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

    @Operation(
            summary = "Zaakceptuj konkretny trening (dla trenera)",
            description = "Ustawia `accepted = true` dla treningu o podanym ID, jeżeli trening należy do użytkownika podopiecznego trenera.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID treningu do zaakceptowania",
                            required = true,
                            schema = @Schema(type = "integer", example = "7")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Trening został zaakceptowany (brak treści)",
                            content = @Content(schema = @Schema(type = "void"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Zalogowany użytkownik nie jest trenerem lub nie ma uprawnień do tego treningu",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono treningu o podanym ID",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
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

    @Operation(
            summary = "Odrzuć konkretny trening (dla trenera)",
            description = "Usuwa trening o podanym ID, jeśli należy on do użytkownika podopiecznego trenera.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID treningu do odrzucenia",
                            required = true,
                            schema = @Schema(type = "integer", example = "7")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Trening został usunięty (brak treści)",
                            content = @Content(schema = @Schema(type = "void"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Zalogowany użytkownik nie jest trenerem lub nie ma uprawnień do tego treningu",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono treningu o podanym ID",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
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
