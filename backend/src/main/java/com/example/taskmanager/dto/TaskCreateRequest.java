package com.example.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for creating a new task. Only the title is provided by the client.
 */
public class TaskCreateRequest {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }
}
