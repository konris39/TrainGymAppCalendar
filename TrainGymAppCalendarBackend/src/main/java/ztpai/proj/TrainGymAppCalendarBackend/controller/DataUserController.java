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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/data")
public class DataUserController {

    private final DataUserService dataUserService;
    private final UserRepository userRepository;

    public DataUserController(DataUserService dataUserService, UserRepository userRepository) {
        this.dataUserService = dataUserService;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Pobierz dane zalogowanego użytkownika",
            description = "Zwraca informacje o użytkowniku (DataUserResponseDto) na podstawie aktualnie zalogowanego konta.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Znaleziono dane użytkownika",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataUserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono danych użytkownika lub samego użytkownika",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany lub brak tokena",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("/my")
    public ResponseEntity<DataUserResponseDto> getMyData() {
        User currentUser = getCurrentUser();
        return dataUserService.getByUserId(currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Aktualizuj dane zalogowanego użytkownika",
            description = "Aktualizuje pola DataUser na podstawie przekazanego DataUserUpdateDto dla zalogowanego użytkownika.",
            requestBody = @RequestBody(
                    description = "Obiekt JSON z polami do aktualizacji (DataUserUpdateDto)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataUserUpdateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pomyślnie zaktualizowano dane użytkownika",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DataUserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Nieprawidłowe dane wejściowe (walidacja nie przeszła)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono profilu użytkownika do aktualizacji",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany lub brak tokena",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
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
