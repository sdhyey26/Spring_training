package com.tss.Dto;

import lombok.Data;

@Data
public class InstructorResponseDto {

    private Long instructorId;
    private String name;
    private String qualification;
    private int experience;
}
