package org.lechuck.personal_app.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lechuck.personal_app.DTO.Schedule.ScheduleDTO;
import org.lechuck.personal_app.DTO.TaskDTO.TaskDTO;
import org.lechuck.personal_app.DTO.TaskDTO.TaskListDTO;
import org.lechuck.personal_app.Repository.TaskListRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lechuck.personal_app.Config.PromptConfig.ScheduleQueryPrompt;
import static org.lechuck.personal_app.Config.PromptConfig.createTaskPrompt;
import static org.lechuck.personal_app.Config.PromptConfig.CreateSchedulePrompt;

@Service
public class ChatBotService {

    private final ChatClient chatClient;
    private final ScheduleService scheduleService;
    private final TaskService taskService;
    private final TaskListService taskListService;
    private final ObjectMapper objectMapper;
    private final TaskListRepository taskListRepository;

    public ChatBotService(ChatClient.Builder builder, ScheduleService scheduleService, TaskService taskService, TaskListService taskListService, TaskListRepository taskListRepository) {
        this.chatClient = builder.build();
        this.scheduleService = scheduleService;
        this.taskService = taskService;
        this.taskListService = taskListService;
        this.objectMapper = new ObjectMapper();
        this.taskListRepository = taskListRepository;
    }

    // Wrapper class for JSON responses
    public static class ChatBotResponse {
        private boolean success;
        private String message;
        private Object data;

        public ChatBotResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }


    public ChatBotResponse getChatResponse(String userMessage, int userId) {
        if (userMessage.toLowerCase().contains("remind") && userMessage.toLowerCase().contains("schedule")) {
            return queryScheduleFromMessage(userMessage, userId);
        } else if (userMessage.toLowerCase().contains("schedule") || userMessage.toLowerCase().contains("meeting")) {
            return createScheduleFromMessage(userMessage, userId);
        }

        String response = chatClient.prompt()
                .system("""
                        You are a proactive personal assistant designed to assist users with tasks, schedules, reminders, and general queries in a conversational and helpful manner.
                        Engage the user by analyzing their input, identifying opportunities to assist with task management, scheduling, or reminders, and driving the conversation to clarify needs or suggest actions.
                        Respond in a single line without line breaks, keeping the tone friendly, concise, and action-oriented.
                        For general queries, provide a brief answer and pivot to potential task/schedule needs (e.g., "Got it! Should I set a reminder for this?").
                        Avoid assuming details not provided, and do not include error messages unless clarification is needed.
                        Always aim to anticipate the user’s needs and offer relevant suggestions to keep the conversation productive.
                        """)
                .user(userMessage)
                .call()
                .content();
        return new ChatBotResponse(true, response, "General response" );
    }

    public ChatBotResponse createScheduleFromMessage(String userMessage, int userId) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();
        PromptTemplate promptTemplate = new PromptTemplate("""
            User message: {userMessage}
            Extract the schedule details and return them in the specified format.
            """);
        Prompt prompt = promptTemplate.create(new HashMap<String, Object>() {{
            put("userMessage", userMessage);
        }});

        String response = chatClient.prompt()
                .system(CreateSchedulePrompt)
                .user(prompt.getContents())
                .call()
                .content();

        ChatBotResponse jsonResponse = extractJsonAsResponse(response);
        if (!jsonResponse.isSuccess()) {
            return jsonResponse;
        }

        try {
            JsonNode scheduleJson = (JsonNode) jsonResponse.getData();
            if (response.contains("I'm sorry")) {
                return new ChatBotResponse(false, response, null);
            }

            String title = scheduleJson.get("title").asText();
            String description = scheduleJson.has("description") && !scheduleJson.get("description").isNull()
                    ? scheduleJson.get("description").asText() : null;
            String location = scheduleJson.has("location") && !scheduleJson.get("location").isNull()
                    ? scheduleJson.get("location").asText() : null;
            String startDateStr = scheduleJson.get("start_date").asText();
            String endDateStr = scheduleJson.has("end_date") && !scheduleJson.get("end_date").isNull()
                    ? scheduleJson.get("end_date").asText() : null;
            String recurringType = scheduleJson.has("recurring_type") && !scheduleJson.get("recurring_type").isNull()
                    ? scheduleJson.get("recurring_type").asText() : "NONE";
            String friendlyMessage = scheduleJson.has("message") && !scheduleJson.get("message").isNull()
                    ? scheduleJson.get("message").asText() : "All set! You're gonna rock this!";
            String error_message = scheduleJson.has("error_message") && !scheduleJson.get("error_message").isNull()
                    ? scheduleJson.get("error_message").asText() : null;

            if (startDateStr == null || endDateStr == null) {
                return new ChatBotResponse(false, scheduleJson.has("error_message") && !scheduleJson.get("error_message").isNull()
                        ? scheduleJson.get("error_message").asText()
                        : friendlyMessage, null);
            }

            ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                    .user_id(userId)
                    .title(title)
                    .description(description)
                    .location(location)
                    .start_date(startDateStr)
                    .end_date(endDateStr)
                    .recurring_type(recurringType)
                    .build();

            ScheduleDTO createdSchedule = scheduleService.createSchedule(scheduleDTO, userId);
            return new ChatBotResponse(true, friendlyMessage , createdSchedule);

        } catch (IllegalArgumentException e) {
            return new ChatBotResponse(false, "Sorry, I couldn't create the schedule: " + e.getMessage(), null);
        }
    }

    public ChatBotResponse createTaskFromMessage(String userMessage, int userId, int taskListId) {
        PromptTemplate promptTemplate = new PromptTemplate("""
        User message: {userMessage}
        Extract the task details and return them in the specified format.
        """);
        Prompt prompt = promptTemplate.create(new HashMap<String, Object>() {{
            put("userMessage", userMessage);
        }});

        String response = chatClient.prompt()
                .system(createTaskPrompt)
                .user(prompt.getContents())
                .call()
                .content();

        ChatBotResponse jsonResponse = extractJsonAsResponse(response);
        if (!jsonResponse.isSuccess()) {
            return jsonResponse; // Failed to extract JSON
        }

        try {
            JsonNode taskJson = (JsonNode) jsonResponse.getData();
            // Check for error_message indicating failure
            if (taskJson.has("error_message") && !taskJson.get("error_message").isNull()) {
                return new ChatBotResponse(
                        false,
                        taskJson.get("error_message").asText(),
                        null
                );
            }

            // Validate required fields
            if (!taskJson.has("title") || taskJson.get("title").isNull()) {
                return new ChatBotResponse(
                        false,
                        "Sorry, a valid task title is required.",
                        null
                );
            }

            String title = taskJson.get("title").asText();
            String description = taskJson.has("description") && !taskJson.get("description").isNull()
                    ? taskJson.get("description").asText() : null;
            String dueDateStr = taskJson.has("due_date") && !taskJson.get("due_date").isNull()
                    ? taskJson.get("due_date").asText() : null;
            String priority = taskJson.has("priority") && !taskJson.get("priority").isNull()
                    ? taskJson.get("priority").asText() : "MEDIUM";
            String recurringType = taskJson.has("recurring_type") && !taskJson.get("recurring_type").isNull()
                    ? taskJson.get("recurring_type").asText() : "NONE";
            String friendlyMessage = taskJson.has("message") && !taskJson.get("message").isNull()
                    ? taskJson.get("message").asText() : "All set! You're gonna love this!";


            // Validate due_date format if provided
            if (dueDateStr != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime.parse(dueDateStr, formatter);
                } catch (DateTimeParseException e) {
                    return new ChatBotResponse(
                            false,
                            "Invalid due date format. Please use 'yyyy-MM-dd HH:mm'.",
                            null
                    );
                }
            }

            TaskDTO taskDTO = TaskDTO.builder()
                    .task_list_id(taskListId)
                    .title(title)
                    .description(description)
                    .due_date(dueDateStr)
                    .status("PENDING")
                    .priority(priority)
                    .recurring_type(recurringType)
                    .build();

            TaskDTO createdTask = taskService.createTask(taskDTO, userId);
            String taskListName = taskListRepository.findNameById(taskListId);
            return new ChatBotResponse(
                    true,
                    friendlyMessage,
                    createdTask
            );

        } catch (IllegalArgumentException e) {
            return new ChatBotResponse(
                    false,
                    "Sorry, I couldn't create the task: " + e.getMessage(),
                    null
            );
        } catch (Exception e) {
            return new ChatBotResponse(
                    false,
                    "An unexpected error occurred while creating the task: " + e.getMessage(),
                    null
            );
        }
    }

    public ChatBotResponse queryScheduleFromMessage(String userMessage, int userId) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();

        PromptTemplate promptTemplate = new PromptTemplate("""
            User message: {userMessage}
            Analyze the message and return the time frame in the specified format.
            """);
        Prompt prompt = promptTemplate.create(new HashMap<String, Object>() {{
            put("userMessage", userMessage);
        }});

        String response = chatClient.prompt()
                .system(ScheduleQueryPrompt)
                .user(prompt.getContents())
                .call()
                .content();

        ChatBotResponse jsonResponse = extractJsonAsResponse(response);
        if (!jsonResponse.isSuccess()) {
            return jsonResponse;
        }

        try {
            JsonNode queryJson = (JsonNode) jsonResponse.getData();
            if (response.contains("I'm sorry")) {
                return new ChatBotResponse(false, response, null);
            }

            String startDateStr = queryJson.has("start_date") && !queryJson.get("start_date").isNull()
                    ? queryJson.get("start_date").asText() : null;
            String endDateStr = queryJson.has("end_date") && !queryJson.get("end_date").isNull()
                    ? queryJson.get("end_date").asText() : null;

            if (startDateStr == null || endDateStr == null) {
                return new ChatBotResponse(false, "I'm sorry, both start date and end date are required. Please specify them again for me.", null);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            try {
                LocalDateTime.parse(startDateStr, formatter);
                LocalDateTime.parse(endDateStr, formatter);
            } catch (DateTimeParseException e) {
                return new ChatBotResponse(false, "I'm sorry, the date you provided is invalid.", null);
            }

            LocalDateTime startDate = LocalDateTime.parse(startDateStr, formatter);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, formatter);
            if (startDate.isAfter(endDate)) {
                return new ChatBotResponse(false, "I'm sorry, the start date must be before the end date.", null);
            }

            List<ScheduleDTO> schedules = scheduleService.getSchedulesByDateRange(userId, startDateStr, endDateStr);
            String message = schedules.isEmpty()
                    ? "No schedules found for the period from " + startDateStr + " to " + endDateStr + "."
                    : "Schedules retrieved successfully for the period from " + startDateStr + " to " + endDateStr + ".";
            return new ChatBotResponse(true, message, schedules);

        } catch (IllegalArgumentException e) {
            return new ChatBotResponse(false, "Sorry, I couldn't process your schedule query: " + e.getMessage(), null);
        }
    }

    private ChatBotResponse getTaskListsAsJson(int userId) {
        List<TaskListDTO> taskLists = taskListService.getTaskLists(userId);
        if (taskLists == null || taskLists.isEmpty()) {
            return new ChatBotResponse(
                    false,
                    "No task lists found. You can create one using 'create task list'.",
                    null
            );
        }

        return new ChatBotResponse(
                true,
                "Available task lists: " + taskLists.stream()
                        .map(TaskListDTO::getTitle)
                        .collect(java.util.stream.Collectors.joining(", ")),
                taskLists
        );
    }

    private ChatBotResponse extractJsonAsResponse(String response) {
        Pattern pattern = Pattern.compile("```json\\n(.*?)\\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            try {
                String jsonContent = matcher.group(1).trim();
                JsonNode jsonNode = objectMapper.readTree(jsonContent);
                return new ChatBotResponse(
                        true,
                        "JSON extracted successfully",
                        jsonNode
                );
            } catch (JsonProcessingException e) {
                return new ChatBotResponse(
                        false,
                        "Failed to parse JSON: " + e.getMessage(),
                        null
                );
            }
        }
        return new ChatBotResponse(
                false,
                "Failed to extract JSON from response",
                null
        );
    }
}