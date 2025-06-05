package ztpai.proj.TrainGymAppCalendarBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ztpai.proj.TrainGymAppCalendarBackend.dto.RecommendedTrainingDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.RecommendedTrainings;
import ztpai.proj.TrainGymAppCalendarBackend.models.TrainingType;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.RecommendedTrainingsRepository;
import ztpai.proj.TrainGymAppCalendarBackend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RecommendedTrainingServiceTest {

    private RecommendedTrainingsRepository recommendedTrainingsRepository;
    private UserRepository userRepository;
    private RecommendedTrainingService recommendedTrainingService;

    @BeforeEach
    void setUp() {
        recommendedTrainingsRepository = mock(RecommendedTrainingsRepository.class);
        userRepository = mock(UserRepository.class);
        recommendedTrainingService = new RecommendedTrainingService(recommendedTrainingsRepository, userRepository);
    }

    @Test
    void shouldFindAllAndMapToDto() {
        RecommendedTrainings rec = new RecommendedTrainings();
        rec.setId(1);
        rec.setName("Push Day");
        rec.setDescription("Trening push");
        rec.setType(TrainingType.PUSH);

        when(recommendedTrainingsRepository.findAll()).thenReturn(List.of(rec));

        List<RecommendedTrainingDto> result = recommendedTrainingService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Push Day");
        assertThat(result.get(0).getType()).isEqualTo(TrainingType.PUSH);
        verify(recommendedTrainingsRepository).findAll();
    }

    @Test
    void shouldFindByIdDto() {
        RecommendedTrainings rec = new RecommendedTrainings();
        rec.setId(2);
        rec.setName("Pull Day");
        when(recommendedTrainingsRepository.findById(2)).thenReturn(Optional.of(rec));

        Optional<RecommendedTrainingDto> result = recommendedTrainingService.findByIdDto(2);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(2);
        assertThat(result.get().getName()).isEqualTo("Pull Day");
    }

    @Test
    void shouldReturnEmptyIfNotFoundByIdDto() {
        when(recommendedTrainingsRepository.findById(99)).thenReturn(Optional.empty());

        Optional<RecommendedTrainingDto> result = recommendedTrainingService.findByIdDto(99);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindEntityById() {
        RecommendedTrainings rec = new RecommendedTrainings();
        rec.setId(5);
        when(recommendedTrainingsRepository.findById(5)).thenReturn(Optional.of(rec));

        Optional<RecommendedTrainings> result = recommendedTrainingService.findEntityById(5);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(5);
    }

    @Test
    void shouldAssignAllRecommendedToUser() {
        User user = new User();
        user.setId(10);
        user.setName("Jan");
        user.setRecommendedTrainings(new java.util.HashSet<>());

        RecommendedTrainings rec1 = new RecommendedTrainings();
        rec1.setId(1);
        rec1.setName("Push Day");

        RecommendedTrainings rec2 = new RecommendedTrainings();
        rec2.setId(2);
        rec2.setName("Legs Day");

        List<RecommendedTrainings> allRecs = List.of(rec1, rec2);

        when(userRepository.findById(10)).thenReturn(Optional.of(user));
        when(recommendedTrainingsRepository.findAll()).thenReturn(allRecs);

        recommendedTrainingService.assignAllRecommendedToUser(10);

        assertThat(user.getRecommendedTrainings()).containsExactlyInAnyOrder(rec1, rec2);
        verify(userRepository).save(user);
    }

    @Test
    void shouldNotAddDuplicatesWhenAssignAllRecommendedToUser() {
        User user = new User();
        user.setId(11);
        user.setRecommendedTrainings(new java.util.HashSet<>());

        RecommendedTrainings rec1 = new RecommendedTrainings();
        rec1.setId(1);
        rec1.setName("Push Day");

        RecommendedTrainings rec2 = new RecommendedTrainings();
        rec2.setId(2);
        rec2.setName("Legs Day");

        user.getRecommendedTrainings().add(rec1);

        List<RecommendedTrainings> allRecs = List.of(rec1, rec2);

        when(userRepository.findById(11)).thenReturn(Optional.of(user));
        when(recommendedTrainingsRepository.findAll()).thenReturn(allRecs);

        recommendedTrainingService.assignAllRecommendedToUser(11);

        assertThat(user.getRecommendedTrainings()).containsExactlyInAnyOrder(rec1, rec2);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowIfUserNotFoundWhenAssignAll() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                recommendedTrainingService.assignAllRecommendedToUser(999)
        );
    }
}