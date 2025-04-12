package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.dto.DataUserUpdateDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.DataUser;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.DataUserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/data")
@CrossOrigin
public class DataUserController {

    private final DataUserRepository dataUserRepository;
    private final UserRepository userRepository;

    public DataUserController(DataUserRepository dataUserRepository, UserRepository userRepository) {
        this.dataUserRepository = dataUserRepository;
        this.userRepository = userRepository;
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my")
    public List<DataUser> getMyData(){
        User currentUser = getCurrentUser();
        return dataUserRepository.findAllByUserId(currentUser.getId());
    }

    @PatchMapping("/update")
    public ResponseEntity<DataUser> updateUserDataByUserId(@Valid @RequestBody DataUserUpdateDto dto){
        User currentUser = getCurrentUser();

        Optional<DataUser> existingDataOpt = dataUserRepository.findByUserId(currentUser.getId());
        if (!existingDataOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        DataUser existingData = existingDataOpt.get();

        if (dto.getWeight() != null) {
            existingData.setWeight(dto.getWeight());
        }
        if (dto.getHeight() != null) {
            existingData.setHeight(dto.getHeight());
        }
        if (dto.getAge() != null) {
            existingData.setAge(dto.getAge());
        }

        if (dto.getBP() != null) {
            existingData.setBP(dto.getBP());
        }
        if (dto.getSQ() != null) {
            existingData.setSQ(dto.getSQ());
        }
        if (dto.getDL() != null) {
            existingData.setDL(dto.getDL());
        }

        DataUser updatedData = dataUserRepository.save(existingData);
        return ResponseEntity.ok(updatedData);
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
