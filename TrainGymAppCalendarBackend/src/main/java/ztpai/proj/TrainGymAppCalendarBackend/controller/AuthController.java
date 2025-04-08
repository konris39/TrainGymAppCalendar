package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user){
        if (userRepository.findByMail(user.getMail()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with that email already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created!");
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
        Optional<User> userOptional = userRepository.findByMail(request.getMail());
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email or password");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
        return ResponseEntity.ok("User authenticated successfully");
    }
}
