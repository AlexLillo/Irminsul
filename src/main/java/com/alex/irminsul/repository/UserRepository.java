package com.alex.irminsul.repository;

import com.alex.irminsul.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}