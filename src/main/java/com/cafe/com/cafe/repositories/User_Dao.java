package com.cafe.com.cafe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cafe.com.cafe.Entites.User;
import com.cafe.com.cafe.wrapper.User_Wrapper;

import jakarta.transaction.Transactional;

@Repository
@EnableJpaRepositories
public interface User_Dao extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);

    List<User_Wrapper> getAllUser();

    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

    List<String> getAllAdmin();

    User findByEmail(String email);

}
