package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskCreateRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.Task;
import java.util.List;

/**
 * Service interface for managing tasks.
 */
public interface TaskService {
    /**
     * Retrieve all tasks.
     * @return list of task responses
     */
    List<TaskResponse> getAllTasks();

    /**
     * Create a new task.
     * @param request request containing title
     * @return created task response
     */
    TaskResponse createTask(TaskCreateRequest request);

    /**
     * Delete a task by id.
     * @param id task id
     */
    void deleteTask(Long id);
}
