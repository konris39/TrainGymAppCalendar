package ztpai.proj.TrainGymAppCalendarBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingCreateDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingResponseDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.TrainingUpdateDto;
import ztpai.proj.TrainGymAppCalendarBackend.kafka.TrainerRequestProducer;
import ztpai.proj.TrainGymAppCalendarBackend.models.Training;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.models.UserGroup;
import ztpai.proj.TrainGymAppCalendarBackend.repository.GroupRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.TrainingRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    TrainingRepository trainingRepository;
    UserRepository userRepository;
    TrainerRequestProducer trainerRequestProducer;
    GroupRepository groupRepository;

    TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingRepository = mock(TrainingRepository.class);
        userRepository = mock(UserRepository.class);
        trainerRequestProducer = mock(TrainerRequestProducer.class);
        groupRepository = mock(GroupRepository.class);
        trainingService = new TrainingService(trainingRepository, userRepository, trainerRequestProducer, groupRepository);
    }

    @Test
    void shouldCreateTrainingWithoutAskTrainer() {
        User user = new User();
        user.setId(1);
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setName("Trening A");
        dto.setDescription("Opis");
        dto.setTrainingDate(LocalDate.of(2024, 7, 1));
        dto.setAskTrainer(false);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> {
            Training tr = inv.getArgument(0);
            tr.setId(10);
            return tr;
        });

        TrainingResponseDto resp = trainingService.createTraining(1, dto);

        assertThat(resp.getName()).isEqualTo("Trening A");
        assertThat(resp.isAccepted()).isTrue();
        verify(trainerRequestProducer, never()).sendRequest(any());
        verify(trainingRepository).save(any());
    }

    @Test
    void shouldCreateTrainingWithAskTrainerAndSendKafkaEvent() {
        User user = new User();
        user.setId(2);
        TrainingCreateDto dto = new TrainingCreateDto();
        dto.setName("Trening z trenerem");
        dto.setDescription("Opis2");
        dto.setTrainingDate(LocalDate.of(2024, 7, 2));
        dto.setAskTrainer(true);

        User trainer = new User();
        trainer.setId(123);
        UserGroup group = new UserGroup();
        group.setUser(user);
        group.setTrainer(trainer);
        List<UserGroup> userGroups = List.of(group);

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> {
            Training tr = inv.getArgument(0);
            tr.setId(22);
            return tr;
        });
        when(groupRepository.findAllByUserId(2)).thenReturn(userGroups);

        TrainingResponseDto resp = trainingService.createTraining(2, dto);

        assertThat(resp.getName()).isEqualTo("Trening z trenerem");
        assertThat(resp.isAccepted()).isFalse();
        verify(trainingRepository).save(any());
        verify(trainerRequestProducer).sendRequest(contains("\"trainingId\":22"));
        verify(trainerRequestProducer).sendRequest(contains("\"trainerId\":123"));
    }

    @Test
    void shouldGetAllUserTrainings() {
        Training t1 = new Training();
        t1.setId(1);
        t1.setName("Trening1");
        t1.setCompleted(false);

        Training t2 = new Training();
        t2.setId(2);
        t2.setName("Trening2");
        t2.setCompleted(true);

        when(trainingRepository.findAllByUserId(7)).thenReturn(List.of(t1, t2));
        List<TrainingResponseDto> resp = trainingService.getAllUserTrainings(7);

        assertThat(resp).hasSize(2);
        assertThat(resp.get(0).getName()).isEqualTo("Trening1");
        assertThat(resp.get(1).isCompleted()).isTrue();
    }

    @Test
    void shouldGetUserTrainingByIdIfOwner() {
        User user = new User();
        user.setId(5);

        Training tr = new Training();
        tr.setId(17);
        tr.setUser(user);

        when(trainingRepository.findById(17)).thenReturn(Optional.of(tr));
        Optional<TrainingResponseDto> resp = trainingService.getUserTrainingById(5, 17);

        assertThat(resp).isPresent();
        assertThat(resp.get().getId()).isEqualTo(17);
    }

    @Test
    void shouldReturnEmptyIfNotOwnerInGetUserTrainingById() {
        User user = new User();
        user.setId(5);

        Training tr = new Training();
        tr.setId(17);
        tr.setUser(new User());
        tr.getUser().setId(999);

        when(trainingRepository.findById(17)).thenReturn(Optional.of(tr));
        Optional<TrainingResponseDto> resp = trainingService.getUserTrainingById(5, 17);

        assertThat(resp).isEmpty();
    }

    @Test
    void shouldUpdateTrainingIfOwner() {
        User user = new User();
        user.setId(10);

        Training tr = new Training();
        tr.setId(7);
        tr.setUser(user);
        tr.setName("OldName");

        TrainingUpdateDto dto = new TrainingUpdateDto();
        dto.setName("Nowa nazwa");
        dto.setDescription("Desc");

        when(trainingRepository.findById(7)).thenReturn(Optional.of(tr));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<TrainingResponseDto> resp = trainingService.updateTraining(10, 7, dto);

        assertThat(resp).isPresent();
        assertThat(resp.get().getName()).isEqualTo("Nowa nazwa");
        assertThat(resp.get().getDescription()).isEqualTo("Desc");
    }

    @Test
    void shouldNotUpdateTrainingIfNotOwner() {
        Training tr = new Training();
        tr.setId(7);
        tr.setUser(new User());
        tr.getUser().setId(999);

        TrainingUpdateDto dto = new TrainingUpdateDto();
        dto.setName("Whatever");

        when(trainingRepository.findById(7)).thenReturn(Optional.of(tr));
        Optional<TrainingResponseDto> resp = trainingService.updateTraining(1, 7, dto);

        assertThat(resp).isEmpty();
    }

    @Test
    void shouldCompleteTrainingIfOwner() {
        User user = new User();
        user.setId(3);

        Training tr = new Training();
        tr.setId(33);
        tr.setUser(user);
        tr.setCompleted(false);

        when(trainingRepository.findById(33)).thenReturn(Optional.of(tr));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<TrainingResponseDto> resp = trainingService.completeTraining(3, 33);

        assertThat(resp).isPresent();
        assertThat(resp.get().isCompleted()).isTrue();
    }

    @Test
    void shouldNotCompleteTrainingIfNotOwner() {
        Training tr = new Training();
        tr.setId(77);
        tr.setUser(new User());
        tr.getUser().setId(99);

        when(trainingRepository.findById(77)).thenReturn(Optional.of(tr));
        Optional<TrainingResponseDto> resp = trainingService.completeTraining(1, 77);

        assertThat(resp).isEmpty();
    }

    @Test
    void shouldDeleteTrainingIfOwner() {
        User user = new User();
        user.setId(13);

        Training tr = new Training();
        tr.setId(44);
        tr.setUser(user);

        when(trainingRepository.findById(44)).thenReturn(Optional.of(tr));

        boolean result = trainingService.deleteTraining(13, 44);

        assertThat(result).isTrue();
        verify(trainingRepository).deleteById(44);
    }

    @Test
    void shouldNotDeleteTrainingIfNotOwner() {
        Training tr = new Training();
        tr.setId(99);
        tr.setUser(new User());
        tr.getUser().setId(999);

        when(trainingRepository.findById(99)).thenReturn(Optional.of(tr));
        boolean result = trainingService.deleteTraining(1, 99);

        assertThat(result).isFalse();
        verify(trainingRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldReturnCorrectTrainingResponseDto() {
        Training tr = new Training();
        tr.setId(1);
        tr.setName("abc");
        tr.setDescription("desc");
        tr.setTrainingDate(LocalDate.of(2025,1,1));
        tr.setCompleted(true);
        tr.setAccepted(false);

        TrainingResponseDto dto = trainingService.toTrainingResponseDto(tr);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("abc");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getTrainingDate()).isEqualTo(LocalDate.of(2025,1,1));
        assertThat(dto.isCompleted()).isTrue();
        assertThat(dto.isAccepted()).isFalse();
    }
}