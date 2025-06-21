package org.lechuck.personal_app.Service;

import jakarta.persistence.Temporal;
import org.lechuck.personal_app.DTO.TaskDTO.TaskDTO;
import org.lechuck.personal_app.DTO.TaskDTO.TaskListDTO;
import org.lechuck.personal_app.Entity.TaskEntity;
import org.lechuck.personal_app.Entity.TaskListEntity;
import org.lechuck.personal_app.Repository.TaskListRepository;
import org.lechuck.personal_app.Repository.TaskRepository;
import org.lechuck.personal_app.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskListRepository taskListRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(int id){
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Task with ID " + id + " not found"));
        return TaskDTO.builder()
                .task_list_id(task.getTaskList().getId())
                .task_id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .recurring_type(task.getRecurringType())
                .due_date(task.getDueDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasksByUser(int userId) {
        List<TaskEntity> tasks = taskRepository.findAllByUserId(userId);
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("No tasks found for user ID " + userId);
        }
        return tasks.stream()
                .map(task -> TaskDTO.builder()
                        .task_id(task.getId())
                        .task_list_id(task.getTaskList().getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .due_date(task.getDueDate())
                        .status(task.getStatus())
                        .priority(task.getPriority())
                        .recurring_type(task.getRecurringType())
                        .build())
                .toList();
    }


    @Transactional
    public List<TaskDTO> getAllTasks(int taskListId){
        if(taskRepository.findAllByTaskList_Id(taskListId).isEmpty()){
            throw new IllegalArgumentException("No Tasks Found");
        }
        return taskRepository.findAllByTaskList_Id(taskListId).stream()
                .map(taskEntity -> TaskDTO.builder()
                        .task_list_id(taskEntity.getTaskList().getId())
                        .task_id(taskEntity.getId())
                        .title(taskEntity.getTitle())
                        .description(taskEntity.getDescription())
                        .due_date(taskEntity.getDueDate())
                        .status(taskEntity.getStatus())
                        .priority(taskEntity.getPriority())
                        .recurring_type(taskEntity.getRecurringType())
                        .build())
                .toList();
    }

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO, int userId) {
        TaskEntity taskEntity = new TaskEntity();
        TaskListEntity taskListEntity = taskListRepository.findById(taskDTO.getTask_list_id())
                .orElseThrow(() -> new IllegalArgumentException("Task List Not Found"));
        taskEntity.setTaskList(taskListEntity);
        taskEntity.setId(taskDTO.getTask_id());
        taskEntity.setTitle(taskDTO.getTitle());
        taskEntity.setDescription(taskDTO.getDescription());
        taskEntity.setDueDate(taskDTO.getDue_date());
        taskEntity.setStatus(taskDTO.getStatus());
        taskEntity.setPriority(taskDTO.getPriority());
        taskEntity.setRecurringType(taskDTO.getRecurring_type());
        taskRepository.save(taskEntity);

        return TaskDTO.builder()
                .task_list_id(taskDTO.getTask_list_id())
                .task_id(taskEntity.getId())
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .due_date(taskDTO.getDue_date())
                .status(taskDTO.getStatus())
                .priority(taskDTO.getPriority())
                .recurring_type(taskDTO.getRecurring_type())
                .build();

    }

    @Transactional
    public TaskDTO updateTask(Integer id, TaskDTO updatedTask) {
        if (id == null || updatedTask == null) {
            throw new IllegalArgumentException("Invalid ID or data");
        }
        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task ID not found"));
        if(updatedTask.getTask_list_id() != null && !updatedTask.getTask_list_id().describeConstable().isEmpty()){
            existingTask.setTaskList(taskListRepository.findById(updatedTask.getTask_list_id())
                    .orElseThrow(()-> new IllegalArgumentException("Task List not found")));
        }
        if (updatedTask.getTitle() != null && !updatedTask.getTitle().isEmpty()) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null && !updatedTask.getDescription().isEmpty()) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getDue_date() != null && !updatedTask.getDue_date().isEmpty()) {
            existingTask.setDueDate(updatedTask.getDue_date());
        }
        if (updatedTask.getStatus() != null && !updatedTask.getStatus().isEmpty()) {
            existingTask.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getPriority() != null && !updatedTask.getPriority().isEmpty()) {
            existingTask.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getRecurring_type() != null && !updatedTask.getRecurring_type().isEmpty()) {
            existingTask.setRecurringType(updatedTask.getRecurring_type());
        }
        taskRepository.save(existingTask);

        return TaskDTO.builder()
                .task_id(existingTask.getId())
                .task_list_id(existingTask.getTaskList().getId())
                .title(existingTask.getTitle())
                .description(existingTask.getDescription())
                .due_date(existingTask.getDueDate())
                .status(existingTask.getStatus())
                .priority(existingTask.getPriority())
                .recurring_type(existingTask.getRecurringType())
                .build();
    }

    @Transactional
    public void deleteTask(Integer task_id){
        TaskEntity taskEntity = taskRepository.findById(task_id)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + task_id + " not found"));
        taskRepository.delete(taskEntity);

    }

}
