package com.library.library.controller.admin;

import com.library.library.dto.request.AddStudentRequest;
import com.library.library.dto.request.StatusUpdateRequest;
import com.library.library.dto.response.ApiResponse;
import com.library.library.dto.response.StudentBookResponse;
import com.library.library.dto.response.UserResponse;
import com.library.library.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/library/admin/student-profile-controller")
@RestController
@RequiredArgsConstructor
public class AdminStudentProfileController {

    private final StudentProfileService studentProfileService;

    @PostMapping("/add-student")
    public ResponseEntity<ApiResponse<UserResponse>> addStudent(@Valid @RequestBody AddStudentRequest request) {
        UserResponse response = studentProfileService.addStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student added successfully", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchStudents(
            @RequestParam(required = false) String keyword) {
        List<UserResponse> students = studentProfileService.searchStudents(keyword);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> changeStudentStatus(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        UserResponse response = studentProfileService.changeStudentStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student status updated successfully", response));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<ApiResponse<List<StudentBookResponse>>> getStudentBooks(@PathVariable Long id) {
        List<StudentBookResponse> books = studentProfileService.getStudentBooks(id);
        return ResponseEntity.ok(ApiResponse.success("Student books retrieved successfully", books));
    }
}
