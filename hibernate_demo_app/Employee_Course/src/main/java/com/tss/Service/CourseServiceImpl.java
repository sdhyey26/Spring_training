package com.tss.Service;

import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.Dto.CourseRequestDto;
import com.tss.Dto.CourseResponseDto;
import com.tss.Entity.Course;
import com.tss.Entity.Instructor;
import com.tss.Repository.CourseRepository;
import com.tss.Repository.InstructorRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CourseResponseDto addCourse(CourseRequestDto courseRequestDto) {

        log.info("Adding course, instructorId={}", courseRequestDto.getInstructorid());
        Course course = modelMapper.map(courseRequestDto, Course.class);

        if (courseRequestDto.getInstructorid() != null) {
            log.debug("Fetching instructor id={}", courseRequestDto.getInstructorid());
            Instructor instructor = instructorRepository.findById(courseRequestDto.getInstructorid())
                    .orElseThrow(() -> new RuntimeException(
                            "Instructor not found with ID: " + courseRequestDto.getInstructorid()));

            course.setInstructor(instructor);
            log.debug("Associated instructorId={} to course", instructor.getInstructorId());

            if (instructor.getCourses() == null) {
                instructor.setCourses(new ArrayList<>());
            }
            instructor.getCourses().add(course);

            instructorRepository.save(instructor);
            log.info("Saved instructor {} with new course association", instructor.getInstructorId());
        }

        Course savedCourse = courseRepository.save(course);
        log.info("Course saved: id={}", savedCourse.getCourseId());

        CourseResponseDto responseDto = modelMapper.map(savedCourse, CourseResponseDto.class);
        if (savedCourse.getInstructor() != null) {
            responseDto.setInstructorName(savedCourse.getInstructor().getName());
        }
        log.debug("Returning CourseResponseDto for courseId={}", savedCourse.getCourseId());

        return responseDto;
    }

}
