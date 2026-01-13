package com.device.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import com.device.management.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>, QueryByExampleExecutor<User> {
    User findByUserId(String userId);
}
