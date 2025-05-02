package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.DataUser;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.DataUserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.security.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;
    private final DataUserRepository dataUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserRepository userRepository, DataUserRepository dataUserRepository,PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.dataUserRepository = dataUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user){
        if (userRepository.findByMail(user.getMail()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Użytkownik o podanym mailu już istnieje!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        DataUser dataUser = new DataUser();
        dataUser.setUser(savedUser);
        dataUserRepository.save(dataUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("Użytkownik utworzony!");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        Optional<User> userOptional = userRepository.findByMail(request.getMail());
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nieprawidłowy email lub hasło");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nieprawidłowy email lub hasło");
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
