package org.lechuck.personal_app.DTO.TaskDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDTO {
    private Integer task_id;
    private Integer task_list_id;
    private String title;
    private String description;
    private String due_date;
    private String status;
    private String priority;
    private String recurring_type;

    public TaskDTO(Integer task_id, Integer task_list_id, String title, String description, String due_date, String status, String priority, String recurring_type) {
        this.task_id = task_id;
        this.task_list_id = task_list_id;
        this.title = title;
        this.description = description;
        this.due_date = due_date;
        this.status = status;
        this.priority = priority;
        this.recurring_type = recurring_type;
    }
}
