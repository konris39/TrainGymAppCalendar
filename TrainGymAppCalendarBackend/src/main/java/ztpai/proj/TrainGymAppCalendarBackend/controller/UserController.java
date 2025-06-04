package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.dto.JoinGroupDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.UserAdminUpdateDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.UserResponseDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.UserUpdateDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Pobierz listę wszystkich użytkowników (tylko ADMIN)",
            description = "Zwraca listę wszystkich kont użytkowników w systemie. Wymagane uprawnienia: rola ADMIN.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista wszystkich użytkowników",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Brak uprawnień (użytkownik nie jest ADMIN)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("")
    public ResponseEntity<List<UserResponseDto>> findAll(){
        User currentUser = getCurrentUser();
        if (!currentUser.getAdmin()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<UserResponseDto> users = userService.findAll()
                .stream()
                .map(userService::toUserResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Pobierz dane aktualnie zalogowanego użytkownika",
            description = "Zwraca informacje o aktualnie zalogowanym użytkowniku (UserResponseDto).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Zwrócono dane zalogowanego użytkownika",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany lub brak sesji",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return userService.findByMail(auth.getName())
                .map(userService::toUserResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(
            summary = "Pobierz dane użytkownika po ID (tylko dla siebie)",
            description = "Zwraca informacje (UserResponseDto) dla użytkownika o podanym ID, jeśli ID odpowiada aktualnemu użytkownikowi.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID użytkownika, którego dane mają zostać pobrane",
                            required = true,
                            schema = @Schema(type = "integer", example = "10")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Zwrócono dane użytkownika",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Brak uprawnień (próba oglądania danych innego użytkownika)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Użytkownik nie istnieje",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findByUserId(@PathVariable Integer id){
        User currentUser = getCurrentUser();
        if(!currentUser.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return userService.findById(id)
                .map(userService::toUserResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Aktualizuj własne dane użytkownika",
            description = "Umożliwia zalogowanemu użytkownikowi aktualizację swojego imienia i adresu e-mail.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID użytkownika do zaktualizowania (musi równać się ID zalogowanego)",
                            required = true,
                            schema = @Schema(type = "integer", example = "10")
                    )
            },
            requestBody = @RequestBody(
                    description = "Obiekt JSON z polami do aktualizacji: `name` oraz `mail` (UserUpdateDto)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pomyślnie zaktualizowano dane użytkownika",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Błąd walidacji danych wejściowych (np. brak wymaganych pól)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Brak uprawnień (ID nie odpowiada zalogowanemu użytkownikowi)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Użytkownik o podanym ID nie istnieje",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer id, @Valid @RequestBody UserUpdateDto dto) {
        User currentUser = getCurrentUser();
        if(!currentUser.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserResponseDto updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Usuń własne konto użytkownika",
            description = "Usuwa konto o podanym ID, jeśli ID odpowiada zalogowanemu użytkownikowi.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID konta do usunięcia (musi równać się ID zalogowanego)",
                            required = true,
                            schema = @Schema(type = "integer", example = "10")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Konto użytkownika zostało pomyślnie usunięte (brak treści)",
                            content = @Content(schema = @Schema(type = "void"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Brak uprawnień (próba usunięcia konta innego użytkownika)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono użytkownika o podanym ID",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id){
        User currentUser = getCurrentUser();
        if(!currentUser.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(userService.deleteUserById(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Aktualizuj rolę użytkownika (ADMIN)",
            description = "Pozwala administratorowi zmienić flagi `trainer` i `admin` dla wybranego użytkownika.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID użytkownika, którego role mają zostać zaktualizowane",
                            required = true,
                            schema = @Schema(type = "integer", example = "15")
                    )
            },
            requestBody = @RequestBody(
                    description = "Obiekt JSON zawierający pola `name` (wymagane), opcjonalnie `trainer`, `admin` (UserUpdateDto)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pomyślnie zaktualizowano rolę użytkownika",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Brak uprawnień (użytkownik nie jest ADMIN)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Użytkownik o podanym ID nie istnieje",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @PatchMapping("/updateRoles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserRoles(@PathVariable Integer id, @RequestBody UserUpdateDto dto) {
        UserResponseDto updated = userService.updateUserRoles(id, dto);
        if(updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Aktualizuj imię użytkownika (ADMIN)",
            description = "Pozwala administratorowi zmienić tylko imię (pole `name`) wybranego użytkownika.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID użytkownika, którego imię ma zostać zaktualizowane",
                            required = true,
                            schema = @Schema(type = "integer", example = "20")
                    )
            },
            requestBody = @RequestBody(
                    description = "Obiekt JSON zawierający nowe imię (UserAdminUpdateDto)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserAdminUpdateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pomyślnie zaktualizowano imię użytkownika",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Brak uprawnień (użytkownik nie jest ADMIN)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Użytkownik o podanym ID nie istnieje",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @PatchMapping("/updateAdm/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserAdm(@PathVariable Integer id, @Valid @RequestBody UserAdminUpdateDto dto) {
        UserResponseDto updated = userService.updateUserNameOnly(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Usuń użytkownika (ADMIN)",
            description = "Pozwala administratorowi usunąć dowolne konto użytkownika.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID konta do usunięcia",
                            required = true,
                            schema = @Schema(type = "integer", example = "25")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Konto zostało usunięte",
                            content = @Content(schema = @Schema(type = "void"))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Brak uprawnień (użytkownik nie jest ADMIN)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Nie znaleziono użytkownika o podanym ID",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @DeleteMapping("/deleteAdm/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByIdAdm(@PathVariable Integer id) {
        if(userService.deleteUserById(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Przypisz się do grupy trenera",
            description = "Zalogowany użytkownik wysyła prośbę o dołączenie do grupy drugiego użytkownika (trenera) na podstawie adresu e-mail trenera.",
            requestBody = @RequestBody(
                    description = "Obiekt JSON zawierający pole `trainerEmail` (JoinGroupDto)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JoinGroupDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Zapisano pomyślnie prośbę o dołączenie do grupy",
                            content = @Content(schema = @Schema(type = "void"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Błąd (np. nie znaleziono trenera lub nieprawidłowy e-mail)",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Użytkownik niezalogowany",
                            content = @Content(schema = @Schema(type = "string"))
                    )
            }
    )
    @PostMapping("/joinGroup")
    public ResponseEntity<Void> joinGroup(@RequestBody JoinGroupDto dto) {
        User currentUser = getCurrentUser();
        try {
            userService.joinGroup(dto.getTrainerEmail(), currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak zalogowanego użytkownika.");
        }
        return userService.findByMail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika."));
    }
}
