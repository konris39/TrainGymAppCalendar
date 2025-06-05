package ztpai.proj.TrainGymAppCalendarBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ztpai.proj.TrainGymAppCalendarBackend.models.DataUser;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.DataUserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.security.JwtUtil;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final DataUserRepository dataUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final long ACCESS_TOKEN_MAX_AGE = 2 * 60 * 60;
    private static final long REFRESH_TOKEN_MAX_AGE = 1 * 60 * 60;

    @Autowired
    public AuthController(UserRepository userRepository,
                          DataUserRepository dataUserRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.dataUserRepository = dataUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Operation(
            summary = "Rejestracja nowego użytkownika",
            description = "Dodaje nowego użytkownika do bazy. Sprawdza, czy adres e-mail nie jest już zajęty, "
                    + "haszuje hasło i tworzy podstawowe dane profilu.",
            requestBody = @RequestBody(
                    description = "Obiekt JSON zawierający pola: name, mail i password",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegisterDto.class),
                            examples = @ExampleObject(
                                    name = "Przykład rejestracji",
                                    value = """
                        {
                          "name": "Jan Kowalski",
                          "mail": "jan.kowalski@example.com",
                          "password": "mojeTajneHaslo123"
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Użytkownik został pomyślnie utworzony"),
                    @ApiResponse(responseCode = "409", description = "Użytkownik o podanym mailu już istnieje",
                            content = @Content(schema = @Schema(type = "string"))),
                    @ApiResponse(responseCode = "400", description = "Błąd walidacji danych wejściowych",
                            content = @Content(schema = @Schema(type = "string")))
            }
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> register(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody UserRegisterDto dto
    ) {
        if (userRepository.findByMail(dto.getMail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Użytkownik o podanym mailu już istnieje!");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setMail(dto.getMail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setTrainer(false);
        user.setAdmin(false);

        User savedUser = userRepository.save(user);

        DataUser dataUser = new DataUser();
        dataUser.setUser(savedUser);
        dataUserRepository.save(dataUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("Użytkownik utworzony!");
    }

    @Operation(
            summary = "Logowanie użytkownika",
            description = "Sprawdza, czy podany mail i hasło są poprawne. "
                    + "W przypadku sukcesu generuje tokeny JWT (access + refresh) "
                    + "i zapisuje je w ciasteczkach (`HttpOnly`).",
            requestBody = @RequestBody(
                    description = "Obiekt JSON z polami: mail i password",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Przykład logowania",
                                    value = """
                        {
                          "mail": "jan.kowalski@example.com",
                          "password": "mojeTajneHaslo123"
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zalogowano pomyślnie (zestaw ciasteczek w nagłówku)"),
                    @ApiResponse(responseCode = "401", description = "Nieprawidłowe dane logowania",
                            content = @Content(schema = @Schema(type = "void"))),
                    @ApiResponse(responseCode = "400", description = "Błąd walidacji payloadu",
                            content = @Content(schema = @Schema(type = "string")))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        Optional<User> userOpt = userRepository.findByMail(request.mail());
        if (userOpt.isEmpty() ||
                !passwordEncoder.matches(request.password(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userOpt.get();

        String accessToken  = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(ACCESS_TOKEN_MAX_AGE)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Odświeżanie tokenów (access + refresh)",
            description = "Sprawdza ciasteczko `refreshToken`. Jeśli jest ważne, generuje nowe "
                    + "tokeny JWT i zwraca je w ciasteczkach. Jeśli token odświeżający jest niepoprawny lub wygasł, zwraca 401.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Wygenerowano nowe tokeny i zapisano w ciasteczkach"),
                    @ApiResponse(responseCode = "401", description = "Brak lub nieważny token odświeżający",
                            content = @Content(schema = @Schema(type = "void"))),
                    @ApiResponse(responseCode = "500", description = "Błąd wewnętrzny podczas odczytu ciasteczka lub generowania tokena",
                            content = @Content(schema = @Schema(type = "string")))
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request,
                                             HttpServletResponse response) {
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Brak refreshToken"));

        String username = jwtUtil.extractUsernameFromRefresh(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtUtil.validateRefreshToken(refreshToken, userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByMail(username)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        String newAccess  = jwtUtil.generateToken(user);
        String newRefresh = jwtUtil.generateRefreshToken(user);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccess)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(ACCESS_TOKEN_MAX_AGE)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefresh)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Wylogowanie użytkownika",
            description = "Czyści ciasteczka `accessToken` oraz `refreshToken` (ustawia maksymalny wiek na 0), "
                    + "w efekcie usuwa sesję JWT z przeglądarki.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Pomyślnie wylogowano (ciasteczka usunięte)"),
                    @ApiResponse(responseCode = "500", description = "Błąd serwera podczas czyszczenia ciasteczek",
                            content = @Content(schema = @Schema(type = "string")))
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie clearAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

        return ResponseEntity.noContent().build();
    }

    public static class UserRegisterDto {
        @Schema(description = "Imię i nazwisko użytkownika", example = "Jan Kowalski")
        @jakarta.validation.constraints.NotBlank(message = "Pole 'name' nie może być puste")
        private String name;

        @Schema(description = "Adres e-mail użytkownika", example = "jan.kowalski@example.com")
        @jakarta.validation.constraints.NotBlank(message = "Pole 'mail' nie może być puste")
        @jakarta.validation.constraints.Email(message = "Pole 'mail' musi być poprawnym adresem e-mail")
        private String mail;

        @Schema(description = "Hasło użytkownika", example = "mojeTajneHaslo123")
        @jakarta.validation.constraints.NotBlank(message = "Pole 'password' nie może być puste")
        private String password;

        public UserRegisterDto() {}

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getMail() {
            return mail;
        }
        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    @Schema(description = "Dane logowania")
    public record LoginRequest(
            @Schema(description = "Adres e-mail użytkownika", example = "jan.kowalski@example.com")
            String mail,

            @Schema(description = "Hasło użytkownika", example = "mojeTajneHaslo123")
            String password
    ) {}
}
