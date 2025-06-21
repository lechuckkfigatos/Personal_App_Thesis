package org.lechuck.personal_app.Controller;

import org.lechuck.personal_app.DTO.TaskDTO.TaskDTO;
import org.lechuck.personal_app.DTO.TaskDTO.TaskListDTO;
import org.lechuck.personal_app.Service.AuthService;
import org.lechuck.personal_app.Service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/get-task/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable int id){
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/get-all-tasks/{id}")
    public ResponseEntity<?> getAllTasks(@PathVariable int id){
        List<TaskDTO> taskList = taskService.getAllTasks(id);
        if (taskList.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "No tasks found"));
        }
        return ResponseEntity.ok(taskList);
    }

    @GetMapping("/tasks-by-user")
    public ResponseEntity<?> getTasksByUser( ) {
        try {
            List<TaskDTO> tasks = taskService.getAllTasksByUser(AuthService.getUserId());
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(Map.of(
                    "message", "No tasks found"));
        }
    }


    @PostMapping("/create-task")
    public ResponseEntity<?> createTasks(@RequestBody TaskDTO taskDTO){
        TaskDTO task = taskService.createTask(taskDTO, AuthService.getUserId());
        if (taskDTO.getTitle() == null || taskDTO.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Task title cannot be null or empty");
        }
        return ResponseEntity.ok(Map.of(
                "message", "Task successfully created",
                "task", task
        ));
    };

    @PutMapping("/update-task/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Integer id, @RequestBody TaskDTO updatedTask) {
        try{
            TaskDTO taskDTO = taskService.updateTask(id, updatedTask);
            return ResponseEntity.ok(Map.of(
                    "message", "Task successfully updated",
                    "task", taskDTO
            ));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Failed to update task: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-task/{id}")
    public ResponseEntity<?> deleteTasks(@PathVariable int id){
        taskService.deleteTask(id);
        return ResponseEntity.ok(Map.of(
                "message", "Task successfully updated"));

    };
}
