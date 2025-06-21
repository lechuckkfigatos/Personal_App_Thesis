package org.lechuck.personal_app.Repository;

import org.lechuck.personal_app.Entity.TaskEntity;
import org.lechuck.personal_app.Entity.TaskListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity,Integer> {
    Integer id(Integer id);

    List<TaskEntity> findAllByTaskList_Id(int taskList_id);

    @Query("SELECT t FROM TaskEntity t WHERE t.taskList.user.id = :userId")
    List<TaskEntity> findAllByUserId(@Param("userId") int userId);
}
