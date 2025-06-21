package org.lechuck.personal_app.Controller;

import lombok.Getter;
import org.lechuck.personal_app.DTO.TaskDTO.TaskDTO;
import org.lechuck.personal_app.DTO.TaskDTO.TaskListDTO;
import org.lechuck.personal_app.Entity.TaskListEntity;
import org.lechuck.personal_app.Service.AuthService;
import org.lechuck.personal_app.Service.TaskListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task-list")
public class TaskListController {

    private final TaskListService taskListService;

    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    @GetMapping("/get-tasklist/{id}")
    public ResponseEntity<?> getTaskList(@PathVariable int id){
        TaskListDTO taskList = taskListService.getTaskListById(id);
        return ResponseEntity.ok(taskList);
    }

    @GetMapping("/get-all-tasklist")
    public ResponseEntity<?> getTaskLists() {
        List<TaskListDTO> taskList = taskListService.getTaskLists(AuthService.getUserId());
        if (taskList.isEmpty()) {
            return ResponseEntity.ok("No task lists found");
        }
        return ResponseEntity.ok(taskList);
    }

    @PostMapping("/create-tasklist")
    public ResponseEntity<?> createTaskList(@RequestBody TaskListDTO taskListDTO){
        if (taskListDTO.getTitle() == null || taskListDTO.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Task list title cannot be null or empty");
        }
       TaskListDTO taskList = taskListService.createTaskList(taskListDTO, AuthService.getUserId());
        return ResponseEntity.ok(Map.of(
                "message", "Task list successfully created",
                "taskList", taskList
        ));

    }

    @PutMapping("/update-tasklist/{id}")
    public ResponseEntity<?> updateTaskList(@PathVariable int id, @RequestBody TaskListDTO taskList){
        try{
            TaskListDTO updatedTaskList = taskListService.updateTaskList(id, taskList);
            return ResponseEntity.ok(Map.of(
                    "message", "Task list successfully updated",
                    "taskList", updatedTaskList
            ));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch ( Exception e){
            return ResponseEntity.status(500).body("Failed to update task list: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-tasklist/{id}")
    public ResponseEntity<?> deleteTaskList(@PathVariable int id) {
        taskListService.deleteTaskList(id);
        return ResponseEntity.ok(Map.of(
                "message", "Task list successfully updated"));
    }
}
