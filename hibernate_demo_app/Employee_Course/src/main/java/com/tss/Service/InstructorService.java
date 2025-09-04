package com.tss.Service;

import com.tss.Dto.CourseResponseDto;
import com.tss.Dto.InstructorRequestDto;
import com.tss.Dto.InstructorResponseDto;

public interface InstructorService {
    InstructorResponseDto addInstructor(InstructorRequestDto instructorRequestDto);

    CourseResponseDto assignCourseToInstructor(Long instructorId, Long courseId);

}
