package com.tss.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tss.Dto.CourseResponseDto;
import com.tss.Dto.InstructorRequestDto;
import com.tss.Dto.InstructorResponseDto;
import com.tss.Service.InstructorService;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorResponseDto> addInstructor(@RequestBody InstructorRequestDto requestDto) {
        InstructorResponseDto responseDto = instructorService.addInstructor(requestDto);
        return ResponseEntity.ok(responseDto);
    }
    
    @PutMapping("/{instructorId}/assign-course/{courseId}")
    public ResponseEntity<CourseResponseDto> assignCourse(
            @PathVariable Long instructorId,
            @PathVariable Long courseId) {

        CourseResponseDto responseDto = instructorService.assignCourseToInstructor(instructorId, courseId);
        return ResponseEntity.ok(responseDto);
    }
}
