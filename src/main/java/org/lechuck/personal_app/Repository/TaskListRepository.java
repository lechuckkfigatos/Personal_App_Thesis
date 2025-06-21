package org.lechuck.personal_app.Repository;

import org.lechuck.personal_app.Entity.TaskListEntity;
import org.lechuck.personal_app.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskListEntity, Integer> {


    TaskListEntity findByTitle(String title);

    List<TaskListEntity> findAllByUserId(int userId);

    boolean existsByUser_IdAndTitle(Integer userId, String title);

    @Query("SELECT t.title FROM TaskListEntity t WHERE t.id = :id")
    String findNameById(@Param("id") Integer id);

}
