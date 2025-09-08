package com.SpringSecurity.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SpringSecurity.Entity.Role;

public interface RoleRepo extends JpaRepository<Role, Integer>{
	Optional<Role> findByRolename(String role);
}
