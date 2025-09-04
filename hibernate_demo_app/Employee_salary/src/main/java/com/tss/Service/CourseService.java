package com.tss.Service;

import com.tss.Dto.CourseRequestDto;
import com.tss.Dto.CourseResponseDto;

public interface CourseService {
    CourseResponseDto addCourse(CourseRequestDto courseRequestDto);

}
