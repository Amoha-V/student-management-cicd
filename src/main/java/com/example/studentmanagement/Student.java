package com.example.studentmanagement;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class Student {

    private Long id;

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotBlank(message = "email must not be blank")
    @Email(message = "email must be a valid address")
    private String email;

    @NotBlank(message = "course must not be blank")
    private String course;

    @Min(value = 1, message = "year must be between 1 and 5")
    @Max(value = 5, message = "year must be between 1 and 5")
    private int year;

    public Student() {
    }

    public Student(Long id, String name, String email, String course, int year) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student other)) {
            return false;
        }
        return year == other.year
                && Objects.equals(id, other.id)
                && Objects.equals(name, other.name)
                && Objects.equals(email, other.email)
                && Objects.equals(course, other.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, course, year);
    }

    @Override
    public String toString() {
        return "Student{id=%d, name='%s', email='%s', course='%s', year=%d}"
                .formatted(id, name, email, course, year);
    }
}
