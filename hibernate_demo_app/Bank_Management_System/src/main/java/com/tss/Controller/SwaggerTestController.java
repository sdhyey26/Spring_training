package com.tss.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swagger-test")
public class SwaggerTestController {

    @GetMapping
    public String helloSwagger() {
        return "Swagger is working!";
    }
}
