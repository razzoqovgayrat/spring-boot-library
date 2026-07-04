package com.library.library.service;

import com.library.library.dto.request.AddStudentRequest;
import com.library.library.dto.request.StatusUpdateRequest;
import com.library.library.dto.response.StudentBookResponse;
import com.library.library.dto.response.UserResponse;
import com.library.library.entity.User;
import com.library.library.enums.Role;
import com.library.library.enums.UserStatus;
import com.library.library.exception.DuplicateResourceException;
import com.library.library.exception.ResourceNotFoundException;
import com.library.library.repository.StudentBookRepository;
import com.library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final UserRepository userRepository;
    private final StudentBookRepository studentBookRepository;

    public UserResponse addStudent(AddStudentRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        User student = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();

        return UserResponse.fromEntity(userRepository.save(student));
    }

    public List<UserResponse> searchStudents(String keyword) {
        String term = keyword == null ? "" : keyword;
        return userRepository.findByRoleAndFullNameContainingIgnoreCase(Role.STUDENT, term).stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse changeStudentStatus(Long id, StatusUpdateRequest request) {
        User student = getStudentOrThrow(id);
        student.setStatus(request.getStatus());
        return UserResponse.fromEntity(userRepository.save(student));
    }

    public List<StudentBookResponse> getStudentBooks(Long studentId) {
        getStudentOrThrow(studentId);
        return studentBookRepository.findByStudentId(studentId).stream()
                .map(StudentBookResponse::fromEntity)
                .toList();
    }

    private User getStudentOrThrow(Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }
}
