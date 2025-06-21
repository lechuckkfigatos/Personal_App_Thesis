package org.lechuck.personal_app.DTO.UserDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String error;

    // Private constructor to enforce factory methods
    private LoginResponseDTO(String token, String error) {
        this.token = token;
        this.error = error;
    }

    // Factory method for success
    public static LoginResponseDTO success(String token) {
        return new LoginResponseDTO(token, null);
    }

    // Factory method for failure
    public static LoginResponseDTO failure(String error) {
        return new LoginResponseDTO(null, error);
    }

    // Helper method to check success
    public boolean isSuccess() {
        return token != null && error == null;
    }
}