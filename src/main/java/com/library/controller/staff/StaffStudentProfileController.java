package com.library.controller.staff;

import com.library.dto.request.AddStudentRequest;
import com.library.dto.request.StatusUpdateRequest;
import com.library.dto.response.StudentBookResponse;
import com.library.dto.response.UserResponse;
import com.library.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/library/staff/student-profile-controller")
@RestController
@RequiredArgsConstructor
public class StaffStudentProfileController {
    private final StudentProfileService studentProfileService;

    @PostMapping("/add-student")
    public ResponseEntity<UserResponse> addStudent(@Valid @RequestBody AddStudentRequest request) {
        UserResponse response = studentProfileService.addStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all-student")
    public ResponseEntity<List<UserResponse>> getAllProfiles() {
        List<UserResponse> profiles = studentProfileService.getAllStudents();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchStudents(@RequestParam(required = false) String keyword) {
        List<UserResponse> students = studentProfileService.searchStudents(keyword);
        return ResponseEntity.ok(students);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<UserResponse> changeStudentStatus(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        UserResponse response = studentProfileService.changeStudentStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student-books/{studentId}")
    public ResponseEntity<List<StudentBookResponse>> getStudentBooksByStudentId(@PathVariable Long studentId) {
        List<StudentBookResponse> books = studentProfileService.getStudentBooks(studentId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/all-student-books")
    public ResponseEntity<List<StudentBookResponse>> getAllStudentBooks() {
        List<StudentBookResponse> books = studentProfileService.getAllStudentBooks();
        return ResponseEntity.ok(books);
    }
}
