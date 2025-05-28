package ztpai.proj.TrainGymAppCalendarBackend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ztpai.proj.TrainGymAppCalendarBackend.dto.RecommendedTrainingDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.RecommendedTrainings;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.RecommendedTrainingsRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendedTrainingService {

    private final RecommendedTrainingsRepository recommendedTrainingRepository;
    private final UserRepository userRepository;

    public RecommendedTrainingService(RecommendedTrainingsRepository recommendedTrainingRepository, UserRepository userRepository) {
        this.recommendedTrainingRepository = recommendedTrainingRepository;
        this.userRepository = userRepository;
    }

    public List<RecommendedTrainingDto> findAll() {
        return recommendedTrainingRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<RecommendedTrainingDto> findByIdDto(Integer id) {
        return recommendedTrainingRepository.findById(id).map(this::toDto);
    }

    public Optional<RecommendedTrainings> findEntityById(Integer id) {
        return recommendedTrainingRepository.findById(id);
    }

    @Transactional
    public void assignAllRecommendedToUser(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

    List<RecommendedTrainings> allRecommended = recommendedTrainingRepository.findAll();
    for (RecommendedTrainings rec : allRecommended) {
        if (!user.getRecommendedTrainings().contains(rec)) {
            user.getRecommendedTrainings().add(rec);
        }
    }
    userRepository.save(user);
    }


    private RecommendedTrainingDto toDto(RecommendedTrainings r) {
        RecommendedTrainingDto dto = new RecommendedTrainingDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setDescription(r.getDescription());
        dto.setType(r.getType());
        return dto;
    }
}
