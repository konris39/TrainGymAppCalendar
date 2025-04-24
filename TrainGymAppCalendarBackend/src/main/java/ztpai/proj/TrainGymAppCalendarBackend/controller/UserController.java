package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserRepository repository;


    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    // Use for admin view if needed
    /*==============================
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public List<User> findAll(){
        return repository.findAll();
    }
    ==============================*/

    @GetMapping("/{id}")
    public ResponseEntity<User> findByUserId(@PathVariable Integer id){
        User currentUser = getCurrentUser();
        if(!currentUser.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public void create(@Valid @RequestBody User user){
        repository.save(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id){
        User currentUser = getCurrentUser();
        if(!currentUser.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(repository.existsById(id)){
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/updateName/{id}")
    public ResponseEntity<Void> updateUserName(@PathVariable Integer id, @RequestBody User user){
        User currentUser = getCurrentUser();
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if(!existingUser.getId().equals(currentUser.getId())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            existingUser.setName(user.getName());
            repository.save(existingUser);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/updateMail/{id}")
    public ResponseEntity<Void> updateUserMail(@PathVariable Integer id, @RequestBody User user){
        User currentUser = getCurrentUser();
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if(!existingUser.getId().equals(currentUser.getId())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            existingUser.setMail(user.getMail());
            repository.save(existingUser);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak zalogowanego użytkownika.");
        }
        String email = auth.getName();
        return repository.findByMail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika."));
    }
}
