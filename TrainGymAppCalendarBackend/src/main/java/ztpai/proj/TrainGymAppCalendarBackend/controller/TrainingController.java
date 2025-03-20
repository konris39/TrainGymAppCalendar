package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.models.Training;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.TrainingRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/training")
@CrossOrigin
public class TrainingController {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    public TrainingController(TrainingRepository trainingRepository, UserRepository userRepository) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public List<Training> findAllTraining(){
        return trainingRepository.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{userId}")
    public List<Training> findTrainingByUserId(@PathVariable Integer userId){
        userRepository.findUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return trainingRepository.findAllByUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/add")
    public ResponseEntity<Training> addTrainingToUser(@Valid @RequestBody Training training, @PathVariable Integer userId){
        Optional<User> user = userRepository.findUserById(userId);
        if(!user.isPresent()){
            return ResponseEntity.notFound().build();
        }
        training.setUser(user.get());
        Training savedTraining = trainingRepository.save(training);
        return new ResponseEntity<>(savedTraining, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainingById(@PathVariable Integer id){
        if(trainingRepository.existsById(id)){
            trainingRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
