package org.lechuck.personal_app.Controller;

import org.lechuck.personal_app.Service.AuthService;
import org.lechuck.personal_app.Service.ChatBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    // Request record for incoming chat messages
    public record ChatRequest(String message) {}

    public record TaskCreationRequest(String message, int taskListId) {}

    public record ScheduleCreationRequest(String message) {}

    // Response record mirroring ChatBotService.ChatBotResponse
    public record ChatResponse(boolean success, String message, Object data) {}

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> handleMessage(@RequestBody ChatRequest request) {
        try {
            if (request.message() == null || request.message().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse(false, "Message cannot be empty", null));
            }

            int userId = AuthService.getUserId();

            ChatBotService.ChatBotResponse serviceResponse = chatBotService.getChatResponse(request.message(), userId);

            ChatResponse response = new ChatResponse(
                    serviceResponse.isSuccess(),
                    serviceResponse.getMessage(),
                    serviceResponse.getData()
            );

            return serviceResponse.isSuccess()
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.badRequest().body(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(401)
                    .body(new ChatResponse(false, "User not authenticated: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ChatResponse(false, "Sorry, an error occurred: " + e.getMessage(), null));
        }
    }
    @PostMapping("/createTask")
    public ResponseEntity<ChatResponse> createTaskWithChat(@RequestBody TaskCreationRequest request) {
        try {
            // Validate request
            if (request.message() == null || request.message().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse(false, "Message cannot be empty", null));
            }
            int userId = AuthService.getUserId();

            ChatBotService.ChatBotResponse serviceResponse = chatBotService.createTaskFromMessage(
                    request.message(), userId, request.taskListId);

            ChatResponse response = new ChatResponse(
                    serviceResponse.isSuccess(),
                    serviceResponse.getMessage(),
                    serviceResponse.getData()
            );
            return serviceResponse.isSuccess()
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401)
                    .body(new ChatResponse(false, "User not authenticated: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ChatResponse(false, "Sorry, an error occurred: " + e.getMessage(), null));
        }
    }

    @PostMapping("/createSchedule")
    public ResponseEntity<ChatResponse> createScheduleWithChat(@RequestBody ScheduleCreationRequest request) {
        try {
            // Validate request
            if (request.message() == null || request.message().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse(false, "Message cannot be empty", null));
            }
            int userId = AuthService.getUserId();

            ChatBotService.ChatBotResponse serviceResponse = chatBotService.createScheduleFromMessage(
                    request.message(), userId);

            ChatResponse response = new ChatResponse(
                    serviceResponse.isSuccess(),
                    serviceResponse.getMessage(),
                    serviceResponse.getData()
            );
            return serviceResponse.isSuccess()
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.badRequest().body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401)
                    .body(new ChatResponse(false, "User not authenticated: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ChatResponse(false, "Sorry, an error occurred: " + e.getMessage(), null));
        }
    }
}