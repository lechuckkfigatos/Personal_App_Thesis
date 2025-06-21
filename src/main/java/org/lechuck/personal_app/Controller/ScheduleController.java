package org.lechuck.personal_app.Controller;

import feign.Param;
import org.lechuck.personal_app.Config.CustomUserDetails;
import org.lechuck.personal_app.DTO.Schedule.ScheduleDTO;
import org.lechuck.personal_app.Entity.ScheduleEntity;
import org.lechuck.personal_app.Service.AuthService;
import org.lechuck.personal_app.Service.ScheduleService;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/get-schedule/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable int id) {
        ScheduleDTO scheduleDTO = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(scheduleDTO);
    }

    @GetMapping("/get-all-schedules")
    public ResponseEntity<?> getAllSchedules() {
        List<ScheduleDTO> scheduleList = scheduleService.getAllSchedules(AuthService.getUserId());
        return ResponseEntity.ok(scheduleList);
    }

    @PostMapping("/create-schedule")
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDTO scheduleDTO){
        ScheduleDTO dto = scheduleService.createSchedule(scheduleDTO, AuthService.getUserId());
        if(scheduleDTO.getTitle() == null || scheduleDTO.getTitle().isEmpty()){
            return ResponseEntity.badRequest().body("Title is required");
        }
        return ResponseEntity.ok(Map.of(
                "message", "Schedule created successfully",
                "schedule", dto
        ));
    }

    @PutMapping("/update-schedule/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Integer id,@RequestBody ScheduleDTO scheduleDTO){
        try{
            ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, scheduleDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Schedule updated successfully",
                    "schedule", updatedSchedule
            ));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Failed to update schedule: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-schedule/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(Map.of(
                "message", "Schedule deleted successfully"));
    }

}
