package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskCreateRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/tasks returns empty list")
    void getAllTasks_Empty() throws Exception {
        Mockito.when(taskService.getAllTasks()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/tasks creates task successfully")
    void createTask_Success() throws Exception {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("Học Java");
        TaskResponse resp = new TaskResponse(1L, "Học Java", false, LocalDateTime.of(2026,10,13,10,30,0));
        Mockito.when(taskService.createTask(Mockito.any())).thenReturn(resp);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Học Java")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.createdAt", is("2026-10-13T10:30:00")));
    }

    @Test
    @DisplayName("POST /api/tasks with blank title returns 400")
    void createTask_BlankTitle() throws Exception {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("   ");
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE existing task returns 204")
    void deleteTask_Success() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(1L);
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/health returns OK")
    void healthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
