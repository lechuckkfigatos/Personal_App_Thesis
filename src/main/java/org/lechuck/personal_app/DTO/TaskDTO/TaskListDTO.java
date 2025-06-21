package org.lechuck.personal_app.DTO.TaskDTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskListDTO {
    private Integer id;
    private Integer user_id;
    private String title;
    private String description;
    private String CreateDate;

    public TaskListDTO(Integer id, Integer user_id, String title, String description, String createDate) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        CreateDate = createDate;
    }
}
