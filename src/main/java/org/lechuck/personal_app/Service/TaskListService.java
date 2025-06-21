package org.lechuck.personal_app.Service;

import org.lechuck.personal_app.DTO.TaskDTO.TaskListDTO;
import org.lechuck.personal_app.Entity.TaskListEntity;
import org.lechuck.personal_app.Entity.UserEntity;
import org.lechuck.personal_app.Repository.TaskListRepository;
import org.lechuck.personal_app.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskListService {

    private final TaskListRepository taskListRepository;

    private final UserRepository userRepository;

    @Autowired
    public TaskListService(TaskListRepository taskListRepository, UserRepository userRepository) {
        this.taskListRepository = taskListRepository;
        this.userRepository = userRepository;
    }
    @Transactional(readOnly = true)
    public TaskListDTO getTaskListById(Integer id) {
        TaskListEntity taskList = taskListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task List Not Found"));
        return TaskListDTO.builder()
                .id(taskList.getId())
                .user_id(taskList.getUser().getId())
                .title(taskList.getTitle())
                .description(taskList.getDescription())
                .CreateDate(taskList.getCreatedDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TaskListDTO> getTaskLists(int userId) {
        if (taskListRepository.findAllByUserId(userId).isEmpty()) {
            throw new IllegalArgumentException("No Task Lists found");
        }
        List<TaskListEntity> taskLists = taskListRepository.findAllByUserId(userId);
        return taskLists.stream()
                .map(taskListEntity -> TaskListDTO.builder()
                        .id(taskListEntity.getId())
                        .user_id(userId)
                        .title(taskListEntity.getTitle())
                        .description(taskListEntity.getDescription())
                        .CreateDate(taskListEntity.getCreatedDate())
                        .build())
                .toList();
    }

    @Transactional
    public TaskListDTO createTaskList(TaskListDTO taskListDTO, int userId) {
        if (taskListRepository.existsByUser_IdAndTitle(userId, taskListDTO.getTitle())) {
            throw new IllegalArgumentException("A task list with this title already exists");
        }
        TaskListEntity taskListEntity = new TaskListEntity();
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        taskListEntity.setUser(userEntity);
        taskListEntity.setId(taskListDTO.getId());
        taskListEntity.setTitle(taskListDTO.getTitle());
        taskListEntity.setDescription(taskListDTO.getDescription());
        taskListEntity.setCreatedDate(String.valueOf(LocalDateTime.now()));
        taskListRepository.save(taskListEntity);

        return TaskListDTO.builder()
                .id(taskListEntity.getId())
                .user_id(taskListDTO.getUser_id())
                .title(taskListEntity.getTitle())
                .description(taskListEntity.getDescription())
                .CreateDate(taskListEntity.getCreatedDate())
                .build();
    }

    @Transactional
    public TaskListDTO updateTaskList(Integer id,TaskListDTO updatedList) {
            if (id == null || updatedList == null) {
                throw new IllegalArgumentException("Invalid Task List ID or data");
            }
            TaskListEntity existingList = taskListRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Task List not found"));
            if (updatedList.getTitle() != null && !updatedList.getTitle().isEmpty()) {
                existingList.setTitle(updatedList.getTitle());
            }
            if (updatedList.getDescription() != null && !updatedList.getDescription().isEmpty()) {
                existingList.setDescription(updatedList.getDescription());
            }
            TaskListEntity savedList = taskListRepository.save(existingList);

            return TaskListDTO.builder()
                    .id(savedList.getId())
                    .user_id(savedList.getUser().getId())
                    .title(savedList.getTitle())
                    .description(savedList.getDescription())
                    .CreateDate(savedList.getCreatedDate())
                    .build();

    }

    @Transactional
    public void deleteTaskList(Integer taskListId) {
        TaskListEntity taskList = taskListRepository.findById(taskListId)
                .orElseThrow(() -> new IllegalArgumentException("TaskList with ID " + taskListId + " not found"));
        taskListRepository.delete(taskList);
    }


    public Integer getTaskListIdByName(String taskListName) {
        TaskListEntity taskList = taskListRepository.findByTitle(taskListName);
        if (taskList != null) {
            return taskList.getId();
        } else {
            throw new IllegalArgumentException("Task List with name " + taskListName + " not found");
        }
    }
}


