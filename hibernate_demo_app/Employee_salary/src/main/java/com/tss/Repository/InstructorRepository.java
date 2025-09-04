package com.tss.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.Entity.Instructor;

public interface InstructorRepository extends JpaRepository<Instructor, Long>{

}
