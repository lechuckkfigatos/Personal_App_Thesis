package org.lechuck.personal_app.Service;

import org.lechuck.personal_app.DTO.Schedule.ScheduleDTO;
import org.lechuck.personal_app.DTO.TaskDTO.TaskDTO;
import org.lechuck.personal_app.Entity.ScheduleEntity;
import org.lechuck.personal_app.Entity.UserEntity;
import org.lechuck.personal_app.Repository.ScheduleRepository;
import org.lechuck.personal_app.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final UserRepository userRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,  UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public ScheduleDTO getScheduleById(int id){
        ScheduleEntity scheduleEntity = scheduleRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Schedule with ID "+ id + "not found"));
        return ScheduleDTO.builder()
                .schedule_id(scheduleEntity.getId())
                .user_id(scheduleEntity.getUser().getId())
                .title(scheduleEntity.getTitle())
                .description(scheduleEntity.getDescription())
                .location(scheduleEntity.getLocation())
                .start_date(scheduleEntity.getStartDate())
                .end_date(scheduleEntity.getEndDate())
                .recurring_type(scheduleEntity.getRecurringType())
                .build();
    }

    @Transactional
    public List<ScheduleDTO> getAllSchedules(int userId){
        if (scheduleRepository.findAllByUserId(userId).isEmpty()){
            throw new IllegalArgumentException("No Schedule found");
        }
        return scheduleRepository.findAllByUserId(userId).stream()
                .map(scheduleEntity -> ScheduleDTO.builder()
                        .schedule_id(scheduleEntity.getId())
                        .user_id(scheduleEntity.getUser().getId())
                        .title(scheduleEntity.getTitle())
                        .description(scheduleEntity.getDescription())
                        .location(scheduleEntity.getLocation())
                        .start_date(scheduleEntity.getStartDate())
                        .end_date(scheduleEntity.getEndDate())
                        .recurring_type(scheduleEntity.getRecurringType())
                        .build())
                .toList();
    }


    @Transactional
    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO, int userId){
        ScheduleEntity scheduleEntity = new ScheduleEntity();
        UserEntity userEntity = userRepository.findById(userId).
                orElseThrow(() -> new IllegalArgumentException("User " + userId + " not found"));
        scheduleEntity.setUser(userEntity);
        scheduleEntity.setTitle(scheduleDTO.getTitle());
        scheduleEntity.setDescription(scheduleDTO.getDescription());
        scheduleEntity.setLocation(scheduleDTO.getLocation());
        scheduleEntity.setStartDate(String.valueOf(scheduleDTO.getStart_date()));
        scheduleEntity.setEndDate(String.valueOf(scheduleDTO.getEnd_date()));
        scheduleEntity.setRecurringType(scheduleDTO.getRecurring_type());
        ScheduleEntity savedSchedule = scheduleRepository.save(scheduleEntity);

        return mapToDTO(savedSchedule);
    }

    @Transactional
    public ScheduleDTO updateSchedule(Integer id, ScheduleDTO updatedSchedule){
        if(id == null || updatedSchedule ==null){
            throw new IllegalArgumentException("Invalid ID or Data");
        }
        ScheduleEntity existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("No existing schedules found"));
        if (updatedSchedule.getTitle() != null && !updatedSchedule.getTitle().isEmpty()){
            existingSchedule.setTitle(updatedSchedule.getTitle());
        }
        if (updatedSchedule.getDescription() != null && !updatedSchedule.getDescription().isEmpty()){
            existingSchedule.setDescription(updatedSchedule.getDescription());
        }
        if (updatedSchedule.getLocation() != null && !updatedSchedule.getLocation().isEmpty()){
            existingSchedule.setLocation(updatedSchedule.getLocation());
        }
        if (updatedSchedule.getStart_date() != null ){
            existingSchedule.setStartDate(String.valueOf(updatedSchedule.getStart_date()));
        }
        if (updatedSchedule.getEnd_date() != null ){
            existingSchedule.setEndDate(String.valueOf(updatedSchedule.getEnd_date()));
        }
        if (updatedSchedule.getRecurring_type() != null && !updatedSchedule.getRecurring_type().isEmpty()){
            existingSchedule.setRecurringType(updatedSchedule.getRecurring_type());
        }
        scheduleRepository.save(existingSchedule);

        return mapToDTO(existingSchedule);
    }

    @Transactional
    public void deleteSchedule(int id){
        ScheduleEntity scheduleEntity = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No schedule found !"));
        scheduleRepository.delete(scheduleEntity);
    }

    public List<ScheduleDTO> getSchedulesByDateRange(int userId, String startDate, String endDate) {
        List<ScheduleEntity> schedules = scheduleRepository.findByUserIdAndDateRangeOverlap(userId, startDate, endDate);
        return schedules.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ScheduleDTO mapToDTO(ScheduleEntity entity) {
        return ScheduleDTO.builder()
                .schedule_id(entity.getId())
                .user_id(entity.getUser().getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .start_date(entity.getStartDate())
                .end_date(entity.getEndDate())
                .recurring_type(entity.getRecurringType())
                .build();
    }


}
