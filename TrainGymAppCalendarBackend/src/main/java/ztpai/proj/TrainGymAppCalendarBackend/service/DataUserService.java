package ztpai.proj.TrainGymAppCalendarBackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ztpai.proj.TrainGymAppCalendarBackend.dto.*;
import ztpai.proj.TrainGymAppCalendarBackend.models.DataUser;
import ztpai.proj.TrainGymAppCalendarBackend.repository.DataUserRepository;

import java.util.Optional;

@Service
public class DataUserService {

    private final DataUserRepository dataUserRepository;

    public DataUserService(DataUserRepository dataUserRepository) {
        this.dataUserRepository = dataUserRepository;
    }

    public Optional<DataUserResponseDto> getByUserId(Integer userId) {
        return dataUserRepository.findByUserId(userId)
                .map(this::toDataUserResponseDto);
    }

    @Transactional
    public Optional<DataUserResponseDto> updateByUserId(Integer userId, DataUserUpdateDto dto) {
        Optional<DataUser> dataUserOpt = dataUserRepository.findByUserId(userId);
        if (dataUserOpt.isEmpty()) return Optional.empty();

        DataUser dataUser = dataUserOpt.get();

        if (dto.getWeight() != null) dataUser.setWeight(dto.getWeight());
        if (dto.getHeight() != null) dataUser.setHeight(dto.getHeight());
        if (dto.getAge() != null) dataUser.setAge(dto.getAge());
        if (dto.getBP() != null) dataUser.setBP(dto.getBP());
        if (dto.getSQ() != null) dataUser.setSQ(dto.getSQ());
        if (dto.getDL() != null) dataUser.setDL(dto.getDL());

        DataUser updated = dataUserRepository.save(dataUser);
        return Optional.of(toDataUserResponseDto(updated));
    }

    public DataUserResponseDto toDataUserResponseDto(DataUser d) {
        DataUserResponseDto dto = new DataUserResponseDto();
        dto.setId(d.getId());
        dto.setWeight(d.getWeight());
        dto.setHeight(d.getHeight());
        dto.setAge(d.getAge());
        dto.setBmi(d.getBMI());
        dto.setBp(d.getBP());
        dto.setSq(d.getSQ());
        dto.setDl(d.getDL());
        dto.setSum(d.getSum());

        UserDataDto userDto = new UserDataDto();
        userDto.setId(d.getUser().getId());
        userDto.setName(d.getUser().getName());
        userDto.setMail(d.getUser().getMail());
        dto.setUser(userDto);

        return dto;
    }
}
