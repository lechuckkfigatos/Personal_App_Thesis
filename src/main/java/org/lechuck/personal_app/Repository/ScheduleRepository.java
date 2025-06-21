package org.lechuck.personal_app.Repository;

import org.lechuck.personal_app.Entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {
    List<ScheduleEntity> findAllByUserId(int userId);

    @Query("SELECT s FROM ScheduleEntity s WHERE s.user.id = :userId AND " +
            "((s.startDate >= :startDateStart AND s.startDate <= :startDateEnd) OR " +
            "(s.endDate >= :startDateStart AND s.endDate <= :startDateEnd) OR " +
            "(s.startDate <= :startDateStart AND s.endDate >= :startDateEnd))")
    List<ScheduleEntity> findByUserIdAndDateRangeOverlap(
            int userId,
            String startDateStart,
            String startDateEnd
    );
}
