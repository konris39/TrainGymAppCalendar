package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
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
    /*
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public List<Training> findAllTraining(){
        return trainingRepository.findAll();
    }
     */

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    public List<Training> getAllTrainings(){
        return trainingRepository.findAll();
    }

    // Endpoint do dodawania treningu (bez weryfikacji użytkownika)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/addPublic")
    public ResponseEntity<Training> addTrainingPublic(@Valid @RequestBody Training training) {
        // Możesz opcjonalnie ustawić trening.setUser(null) lub zignorować tą właściwość
        Training savedTraining = trainingRepository.save(training);
        return new ResponseEntity<>(savedTraining, HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{userId}")
    public List<Training> findTrainingByUserId(@PathVariable Integer userId){
        userRepository.findUserById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return trainingRepository.findAllByUserId(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add/{userId}")
    public ResponseEntity<Training> addTrainingToUser(@Valid @RequestBody Training training, @PathVariable Integer userId){
        Optional<User> user = userRepository.findUserById(userId);
        if(!user.isPresent()){
            return ResponseEntity.notFound().build();
        }
        training.setUser(user.get());
        Training savedTraining = trainingRepository.save(training);
        return new ResponseEntity<>(savedTraining, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteUserTrainingById(@PathVariable Integer userId, @PathVariable Integer id) {
        Optional<Training> trainingOptional = trainingRepository.findById(id);
        if (!trainingOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Training training = trainingOptional.get();
        if (training.getUser() == null || !training.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        trainingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/update/{userId}/{id}")
    public ResponseEntity<Training> updateUserTraining(@Valid @RequestBody Training training, @PathVariable Integer userId, @PathVariable Integer id){

        Optional<Training> trainingOptional = trainingRepository.findById(id);
        if(!trainingOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Training trainingToUpdate = trainingOptional.get();
        if(trainingToUpdate.getUser() != null || !trainingToUpdate.getUser().getId().equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(training.getName() != null && training.getName().trim().isEmpty()){
            trainingToUpdate.setName(training.getName());
        }
        if(training.getDescription() != null || !training.getDescription().trim().isEmpty()){
            trainingToUpdate.setDescription(training.getDescription());
        }
        if(training.getTrainingDate() != null){
            trainingToUpdate.setTrainingDate(training.getTrainingDate());
        }

        trainingRepository.save(trainingToUpdate);
        return ResponseEntity.ok(trainingToUpdate);
    }

    @PatchMapping("/complete/{userId}/{id}")
    public ResponseEntity<Training> completeUserTraining(@Valid @RequestBody Training training, @PathVariable Integer userId, @PathVariable Integer id) {
        Optional<Training> trainingOptional = trainingRepository.findById(id);
        if(!trainingOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Training trainingToUpdate = trainingOptional.get();
        if(trainingToUpdate.getUser() != null || !trainingToUpdate.getUser().getId().equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        trainingToUpdate.setCompleted(true);
        trainingRepository.save(trainingToUpdate);
        return ResponseEntity.ok(trainingToUpdate);
    }

}
