package ztpai.proj.TrainGymAppCalendarBackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ztpai.proj.TrainGymAppCalendarBackend.dto.*;
import ztpai.proj.TrainGymAppCalendarBackend.kafka.TrainerRequestProducer;
import ztpai.proj.TrainGymAppCalendarBackend.models.UserGroup;
import ztpai.proj.TrainGymAppCalendarBackend.models.Training;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.GroupRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.TrainingRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final TrainerRequestProducer trainerRequestProducer;
    private final GroupRepository groupRepository;

    public TrainingService(TrainingRepository trainingRepository, UserRepository userRepository, TrainerRequestProducer trainerRequestProducer, GroupRepository groupRepository) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.trainerRequestProducer = trainerRequestProducer;
        this.groupRepository = groupRepository;
    }

    @Transactional
public TrainingResponseDto createTraining(Integer userId, TrainingCreateDto dto) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

    Training training = new Training();
    training.setName(dto.getName());
    training.setDescription(dto.getDescription());
    training.setTrainingDate(dto.getTrainingDate());
    training.setAccepted(!dto.isAskTrainer());
    training.setCompleted(false);
    training.setUser(user);

    Training saved = trainingRepository.save(training);

    if (dto.isAskTrainer()) {
        List<UserGroup> groups = groupRepository.findAllByUserId(user.getId());
        if (!groups.isEmpty()) {
            Integer trainerId = groups.get(0).getTrainer().getId();
            String event = String.format(
                "{\"trainingId\":%d,\"userId\":%d,\"trainerId\":%d}",
                saved.getId(), user.getId(), trainerId
            );
            trainerRequestProducer.sendRequest(event);
        }
    }

    return toTrainingResponseDto(saved);
}


    public List<TrainingResponseDto> getAllUserTrainings(Integer userId) {
        List<Training> trainings = trainingRepository.findAllByUserId(userId);
        return trainings.stream().map(this::toTrainingResponseDto).collect(Collectors.toList());
    }

    public Optional<TrainingResponseDto> getUserTrainingById(Integer userId, Integer trainingId) {
        return trainingRepository.findById(trainingId)
                .filter(tr -> tr.getUser() != null && tr.getUser().getId().equals(userId))
                .map(this::toTrainingResponseDto);
    }

    @Transactional
    public Optional<TrainingResponseDto> updateTraining(Integer userId, Integer trainingId, TrainingUpdateDto dto) {
        Optional<Training> trainingOpt = trainingRepository.findById(trainingId);
        if(trainingOpt.isEmpty()) return Optional.empty();

        Training training = trainingOpt.get();
        if (training.getUser() == null || !training.getUser().getId().equals(userId)) {
            return Optional.empty();
        }

        if (dto.getName() != null) training.setName(dto.getName());
        if (dto.getDescription() != null) training.setDescription(dto.getDescription());
        if (dto.getTrainingDate() != null) training.setTrainingDate(dto.getTrainingDate());

        Training updated = trainingRepository.save(training);
        return Optional.of(toTrainingResponseDto(updated));
    }

    @Transactional
    public Optional<TrainingResponseDto> completeTraining(Integer userId, Integer trainingId) {
        Optional<Training> trainingOpt = trainingRepository.findById(trainingId);
        if(trainingOpt.isEmpty()) return Optional.empty();

        Training training = trainingOpt.get();
        if (training.getUser() == null || !training.getUser().getId().equals(userId)) {
            return Optional.empty();
        }
        training.setCompleted(true);
        Training updated = trainingRepository.save(training);
        return Optional.of(toTrainingResponseDto(updated));
    }

    @Transactional
    public boolean deleteTraining(Integer userId, Integer trainingId) {
        Optional<Training> trainingOpt = trainingRepository.findById(trainingId);
        if(trainingOpt.isEmpty()) return false;
        Training training = trainingOpt.get();
        if (training.getUser() == null || !training.getUser().getId().equals(userId)) {
            return false;
        }
        trainingRepository.deleteById(trainingId);
        return true;
    }

    public TrainingResponseDto toTrainingResponseDto(Training t) {
        TrainingResponseDto dto = new TrainingResponseDto();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());
        dto.setTrainingDate(t.getTrainingDate());
        dto.setCompleted(t.isCompleted());
        dto.setAccepted(t.isAccepted());
        return dto;
    }
}
