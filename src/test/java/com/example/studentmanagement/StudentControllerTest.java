package com.example.studentmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createStudent(Student student) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @DisplayName("GET /api/students returns the seeded students")
    void listsStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("POST then GET round-trips a student")
    void createsAndFetchesStudent() throws Exception {
        Long id = createStudent(new Student(null, "Grace Hopper", "grace@example.com", "Computer Science", 4));

        mockMvc.perform(get("/api/students/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Grace Hopper"))
                .andExpect(jsonPath("$.course").value("Computer Science"))
                .andExpect(jsonPath("$.year").value(4));
    }

    @Test
    @DisplayName("PUT updates an existing student")
    void updatesStudent() throws Exception {
        Long id = createStudent(new Student(null, "Katherine Johnson", "kj@example.com", "Mathematics", 1));

        Student updated = new Student(null, "Katherine Johnson", "kj@example.com", "Physics", 2);

        mockMvc.perform(put("/api/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course").value("Physics"))
                .andExpect(jsonPath("$.year").value(2));
    }

    @Test
    @DisplayName("DELETE removes a student and a second delete 404s")
    void deletesStudent() throws Exception {
        Long id = createStudent(new Student(null, "Barbara Liskov", "bl@example.com", "Computer Science", 5));

        mockMvc.perform(delete("/api/students/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/students/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET with an unknown id returns 404")
    void unknownIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/students/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST with invalid payload returns 400 with field errors")
    void rejectsInvalidPayload() throws Exception {
        Student invalid = new Student(null, "", "not-an-email", "", 9);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.year").exists());
    }

    @Test
    @DisplayName("Filtering by course narrows the result set")
    void filtersByCourse() throws Exception {
        mockMvc.perform(get("/api/students").param("course", "Mathematics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].course").value("Mathematics"));
    }
}
