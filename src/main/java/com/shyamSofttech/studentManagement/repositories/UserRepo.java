package com.shyamSofttech.studentManagement.repositories;

import com.shyamSofttech.studentManagement.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByMobileNo(Long mobile);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserName(String userName);

}