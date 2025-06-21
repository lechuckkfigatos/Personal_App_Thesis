package org.lechuck.personal_app.DTO.UserDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private int id;
    private String userName;
    private String email;
    private String password;
    private String location;
    private String CreatedDate;
    private String Language;

}
