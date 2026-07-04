package com.library.library.controller.admin;

import com.library.library.dto.request.AddProfileRequest;
import com.library.library.dto.request.StatusUpdateRequest;
import com.library.library.dto.response.ApiResponse;
import com.library.library.dto.response.UserResponse;
import com.library.library.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/library/admin/profile-controller")
@RestController
@RequiredArgsConstructor
public class AdminProfileController {

    private final ProfileService profileService;

    @GetMapping("/all-profile")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllProfiles() {
        List<UserResponse> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(ApiResponse.success("Profiles retrieved successfully", profiles));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchProfiles(
            @RequestParam(required = false) String keyword) {
        List<UserResponse> profiles = profileService.searchProfiles(keyword);
        return ResponseEntity.ok(ApiResponse.success("Profiles retrieved successfully", profiles));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<UserResponse>> addProfile(@Valid @RequestBody AddProfileRequest request) {
        UserResponse response = profileService.addProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Profile created successfully", response));
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> changeProfileStatus(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        UserResponse response = profileService.changeProfileStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Profile status updated successfully", response));
    }
}
