package com.example.controller;

import com.example.entity.Task;
import com.example.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> getAllTasks(java.security.Principal principal) {
        String username = principal.getName();
        System.out.println("DEBUG: Fetching tasks for user: " + username);
        List<Task> tasks = taskRepository.findByUsername(username);
        System.out.println("DEBUG: Found " + tasks.size() + " tasks for user: " + username);
        return tasks;
    }

    @PostMapping
    public Task createTask(@RequestBody Task task, java.security.Principal principal) {
        String username = principal.getName();
        System.out.println("DEBUG: Creating task for user: " + username);
        task.setUsername(username);
        if (task.getStatus() == null) {
            task.setStatus("todo");
        }
        Task savedTask = taskRepository.save(task);
        System.out.println("DEBUG: Task created with ID: " + savedTask.getId());
        return savedTask;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(taskDetails.getTitle());
                    task.setDescription(taskDetails.getDescription());
                    task.setStatus(taskDetails.getStatus());
                    task.setPriority(taskDetails.getPriority());
                    task.setDueDate(taskDetails.getDueDate());
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
