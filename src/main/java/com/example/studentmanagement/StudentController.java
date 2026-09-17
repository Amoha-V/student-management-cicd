package com.example.studentmanagement;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CRUD endpoints for students, backed by an in-memory store.
 * Swap the map for a JpaRepository when a database is added.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final Map<Long, Student> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @PostConstruct
    void seed() {
        create(new Student(null, "Ada Lovelace", "ada@example.com", "Computer Science", 2));
        create(new Student(null, "Alan Turing", "alan@example.com", "Mathematics", 3));
    }

    @GetMapping
    public List<Student> findAll(@RequestParam(required = false) String course) {
        List<Student> results = new ArrayList<>(store.values());
        if (course != null && !course.isBlank()) {
            results.removeIf(s -> !s.getCourse().equalsIgnoreCase(course));
        }
        results.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return results;
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable Long id) {
        Student student = store.get(id);
        if (student == null) {
            throw notFound(id);
        }
        return student;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@Valid @RequestBody Student student) {
        long id = sequence.incrementAndGet();
        student.setId(id);
        store.put(id, student);
        return student;
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student student) {
        if (!store.containsKey(id)) {
            throw notFound(id);
        }
        student.setId(id);
        store.put(id, student);
        return student;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (store.remove(id) == null) {
            throw notFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Student " + id + " not found");
    }
}
