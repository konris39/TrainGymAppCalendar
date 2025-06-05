package ztpai.proj.TrainGymAppCalendarBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ztpai.proj.TrainGymAppCalendarBackend.dto.DataUserResponseDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.DataUserUpdateDto;
import ztpai.proj.TrainGymAppCalendarBackend.dto.UserDataDto;
import ztpai.proj.TrainGymAppCalendarBackend.models.DataUser;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;
import ztpai.proj.TrainGymAppCalendarBackend.repository.DataUserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DataUserServiceTest {

    private DataUserRepository dataUserRepository;
    private DataUserService dataUserService;

    private User mockUser;
    private DataUser mockDataUser;

    @BeforeEach
    void setUp() {
        dataUserRepository = mock(DataUserRepository.class);
        dataUserService = new DataUserService(dataUserRepository);

        mockUser = new User();
        mockUser.setId(5);
        mockUser.setName("Adam");
        mockUser.setMail("adam@test.pl");

        mockDataUser = new DataUser();
        mockDataUser.setId(15);
        mockDataUser.setUser(mockUser);
        mockDataUser.setWeight(80.0);
        mockDataUser.setHeight(180.0);
        mockDataUser.setAge(30);
        mockDataUser.setBP(100.0);
        mockDataUser.setSQ(120.0);
        mockDataUser.setDL(160.0);
    }

    @Test
    void shouldReturnDataUserByUserId() {
        when(dataUserRepository.findByUserId(5)).thenReturn(Optional.of(mockDataUser));

        Optional<DataUserResponseDto> result = dataUserService.getByUserId(5);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(15);
        assertThat(result.get().getUser().getMail()).isEqualTo("adam@test.pl");
        verify(dataUserRepository).findByUserId(5);
    }

    @Test
    void shouldReturnEmptyIfDataUserNotFound() {
        when(dataUserRepository.findByUserId(99)).thenReturn(Optional.empty());

        Optional<DataUserResponseDto> result = dataUserService.getByUserId(99);

        assertThat(result).isEmpty();
        verify(dataUserRepository).findByUserId(99);
    }

    @Test
    void shouldUpdateFieldsWhenUpdateByUserId() {
        DataUserUpdateDto updateDto = new DataUserUpdateDto();
        updateDto.setWeight(90.0);
        updateDto.setHeight(185.0);
        updateDto.setAge(31);
        updateDto.setBP(110.0);
        updateDto.setSQ(130.0);
        updateDto.setDL(170.0);

        when(dataUserRepository.findByUserId(5)).thenReturn(Optional.of(mockDataUser));
        when(dataUserRepository.save(any(DataUser.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<DataUserResponseDto> result = dataUserService.updateByUserId(5, updateDto);

        assertThat(result).isPresent();
        DataUserResponseDto dto = result.get();
        assertThat(dto.getWeight()).isEqualTo(90.0);
        assertThat(dto.getHeight()).isEqualTo(185.0);
        assertThat(dto.getAge()).isEqualTo(31);
        assertThat(dto.getBp()).isEqualTo(110.0);
        assertThat(dto.getSq()).isEqualTo(130.0);
        assertThat(dto.getDl()).isEqualTo(170.0);

        ArgumentCaptor<DataUser> captor = ArgumentCaptor.forClass(DataUser.class);
        verify(dataUserRepository).save(captor.capture());
        assertThat(captor.getValue().getWeight()).isEqualTo(90.0);
    }

    @Test
    void shouldReturnEmptyIfUpdateOnMissingUser() {
        DataUserUpdateDto updateDto = new DataUserUpdateDto();
        updateDto.setWeight(95.0);

        when(dataUserRepository.findByUserId(99)).thenReturn(Optional.empty());

        Optional<DataUserResponseDto> result = dataUserService.updateByUserId(99, updateDto);

        assertThat(result).isEmpty();
        verify(dataUserRepository).findByUserId(99);
    }
}
