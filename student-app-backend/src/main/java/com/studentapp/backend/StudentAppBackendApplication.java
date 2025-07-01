package com.studentapp.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.studentapp.common.model"})
public class StudentAppBackendApplication {
  public static void main(String[] args) {
    SpringApplication.run(StudentAppBackendApplication.class, args);
  }
}