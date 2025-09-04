package com.tss.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.Entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long>{

}
