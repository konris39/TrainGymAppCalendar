package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public List<User> findAll(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findByUserId(@PathVariable Integer id){
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
        if(repository.existsById(id)){
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/updateName/{id}")
    public ResponseEntity<Void> updateUserName(@PathVariable Integer id, @RequestBody User user){
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setName(user.getName());
            repository.save(existingUser);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/updateMail/{id}")
    public ResponseEntity<Void> updateUserMail(@PathVariable Integer id, @RequestBody User user){
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setMail(user.getMail());
            repository.save(existingUser);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
