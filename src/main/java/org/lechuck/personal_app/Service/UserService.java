package org.lechuck.personal_app.Service;

import org.apache.catalina.User;
import org.lechuck.personal_app.DTO.UserDTO.LoginResponseDTO;
import org.lechuck.personal_app.DTO.UserDTO.RegisterResponseDTO;
import org.lechuck.personal_app.DTO.UserDTO.UserDTO;
import org.lechuck.personal_app.Entity.UserEntity;
import org.lechuck.personal_app.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private final UserRepository repository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public RegisterResponseDTO register(UserEntity user){
        if (user.getUserName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
            return RegisterResponseDTO.failure("Please fill in all fields");
        }
        if (repository.existsByEmail(user.getEmail()) || repository.existsByUserName(user.getUserName())) {
            return RegisterResponseDTO.failure("Email or username already in use");
        }
        if (!isValidEmail(user.getEmail())) {
            return RegisterResponseDTO.failure("Invalid email format");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setLanguage("English");
        user.setUserLocation("Unknown");
        user.setCreatedDate(String.valueOf(LocalDateTime.now()));
        repository.save(user);
        return RegisterResponseDTO.success("Your account has been successfully created");
    }

    public LoginResponseDTO verifyUser(UserEntity user) {
        // Validate input fields first
        if (user == null || user.getUserName() == null || user.getPassword() == null ||
                user.getUserName().isEmpty() || user.getPassword().isEmpty()) {
            return LoginResponseDTO.failure("Please fill in all fields");
        }

        try {
            // Attempt authentication
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
            );

            // If authenticated, generate and return token
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getUserName());
                return LoginResponseDTO.success(token);
            }

            // This line should not be reached due to exceptions, but included for safety
            return LoginResponseDTO.failure("Username or password is incorrect");
        } catch (BadCredentialsException e) {
            // Incorrect username or password
            return LoginResponseDTO.failure("Username or password is incorrect");
        } catch (Exception e) {
            // Other unexpected errors
            return LoginResponseDTO.failure("An error occurred during authentication");
        }
    }

    public UserDTO getUserById(int id){
        UserEntity user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        return UserDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .Language(user.getLanguage())
                .location(user.getUserLocation())
                .CreatedDate(user.getCreatedDate())
                .build();
    }

    public List<UserDTO> getAllUsers() {
        List<UserEntity> user = repository.findAll();
        if (user.isEmpty()) {
            throw new IllegalArgumentException("No users found");
        }
        return user.stream()
                .map(userEntity -> UserDTO.builder()
                        .id(userEntity.getId())
                        .userName(userEntity.getUserName())
                        .email(userEntity.getEmail())
                        .Language(userEntity.getLanguage())
                        .location(userEntity.getUserLocation())
                        .CreatedDate(userEntity.getCreatedDate())
                        .build())
                .toList();
    }

    @Transactional
    public UserDTO updateUser(Integer id, UserDTO updatedUser) {
        if (id == null || updatedUser == null) {
            throw new IllegalArgumentException("Invalid user ID or data");
        }

        UserEntity existingUser = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));

        if (updatedUser.getUserName() != null && !updatedUser.getUserName().isEmpty()) {
            existingUser.setUserName(updatedUser.getUserName());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty() &&
                !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (!isValidEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (repository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(encoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getLanguage() != null) {
            existingUser.setLanguage(updatedUser.getLanguage());
        }
        if (updatedUser.getLocation() != null) {
            existingUser.setUserLocation(updatedUser.getLocation());
        }
        repository.save(existingUser);
        return UserDTO.builder()
                .id(existingUser.getId())
                .userName(existingUser.getUserName())
                .email(existingUser.getEmail())
                .Language(existingUser.getLanguage())
                .location(existingUser.getUserLocation())
                .build();
    }

    private boolean isValidEmail(String email) {
        // Simple email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    @Transactional
    public void deleteUser(int user_id){
        UserEntity userEntity = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("No user with id " + user_id + " found"));
        repository.delete(userEntity);
    }

}
