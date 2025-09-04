package com.tss.service;

import org.springframework.web.multipart.MultipartFile;

import com.tss.dto.ImageResponseDto;

public interface ImageService {

    String uploadImage(MultipartFile file);
    ImageResponseDto getImage(Long id);
}
