package org.lechuck.personal_app.Repository;

import org.lechuck.personal_app.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Integer id(Integer id);

}
