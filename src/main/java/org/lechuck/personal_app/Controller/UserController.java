package org.lechuck.personal_app.Controller;

import org.apache.catalina.User;
import org.lechuck.personal_app.DTO.UserDTO.LoginResponseDTO;
import org.lechuck.personal_app.DTO.UserDTO.RegisterResponseDTO;
import org.lechuck.personal_app.DTO.UserDTO.UserDTO;
import org.lechuck.personal_app.Entity.UserEntity;
import org.lechuck.personal_app.Service.AuthService;
import org.lechuck.personal_app.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity user) {
            RegisterResponseDTO response = userService.register(user);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } return switch (response.getError()) {
                case "Please fill in all fields" -> ResponseEntity.status(400).body(response);
                case "Email or username already in use" -> ResponseEntity.status(409).body(response);
                case "Invalid email format" -> ResponseEntity.status(400).body(response);
                default -> ResponseEntity.status(500).body(response);
            };
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UserEntity user) {
        LoginResponseDTO response = userService.verifyUser(user);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return switch (response.getError()) {
            case "Please fill in all fields" -> ResponseEntity.status(400).body(response);
            case "Email or username already in use" -> ResponseEntity.status(409).body(response);
            case "Username or password is incorrect" -> ResponseEntity.status(401).body(response);
            default -> ResponseEntity.status(500).body(response);
        };
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUserById() {
        UserDTO user = userService.getUserById(AuthService.getUserId());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/get-users")
    public ResponseEntity<?> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO user) {
        try {
            UserDTO updatedUser = userService.updateUser(AuthService.getUserId(), user);
            return ResponseEntity.ok(Map.of(
                    "message", "User successfully updated",
                    "user", updatedUser
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update user: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-user/")
    public ResponseEntity<?> deleteUser() {
        userService.deleteUser(AuthService.getUserId());
        return ResponseEntity.ok(Map.of(
                "message", "User successfully deleted"
        ));
    };
}
