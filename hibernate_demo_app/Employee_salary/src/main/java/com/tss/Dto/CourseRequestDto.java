package com.tss.Dto;

import com.tss.Entity.Instructor;

import lombok.Data;

@Data
public class CourseRequestDto {
    private String courseName;
    private int duration;
    private double fees;
	private Long instructorid;
}
