package org.lechuck.personal_app.DTO.Schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ScheduleDTO {
    private int schedule_id;
    private int user_id;
    private String title;
    private String description;
    private String location;
    private String start_date;
    private String end_date;
    private String recurring_type;


}
