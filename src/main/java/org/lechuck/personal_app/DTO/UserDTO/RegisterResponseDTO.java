package org.lechuck.personal_app.DTO.UserDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponseDTO {
    private String message;
    private String error;

    public RegisterResponseDTO(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public static RegisterResponseDTO success(String message){
        return new RegisterResponseDTO(message, null);
    }

    public static RegisterResponseDTO failure(String error){
        return  new RegisterResponseDTO(null, error);
    }

    public boolean isSuccess(){
        return message != null && error == null;
    }


}
