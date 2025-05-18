package ztpai.proj.TrainGymAppCalendarBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ztpai.proj.TrainGymAppCalendarBackend.dto.UserResponseDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.UserUpdateDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.OK)
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

    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdateDto dto
    ) {
        User currentUser = getCurrentUser();
        if(!currentUser.getId().equals(id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserResponseDto updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

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

    @PatchMapping("/updateRoles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserRoles(
            @PathVariable Integer id,
            @RequestBody UserUpdateDto dto
    ) {
        UserResponseDto updated = userService.updateUserRoles(id, dto);
        if(updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
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
