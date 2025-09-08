package com.SpringSecurity.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SpringSecurity.Entity.User;

public interface UserRepo extends JpaRepository<User, Integer>{
	
	Optional<User> findByUsername(String username);
	
	boolean existsByUsername(String username);

}
