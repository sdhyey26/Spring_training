package com.tss.Dto;

import lombok.Data;

@Data
public class CourseResponseDto {
    private Long courseId;
    private String courseName;
    private int duration;
    private double fees;
    private String instructorName;
}
