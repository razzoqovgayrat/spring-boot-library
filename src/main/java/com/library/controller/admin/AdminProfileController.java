package com.library.controller.admin;

import com.library.dto.request.AddProfileRequest;
import com.library.dto.request.StatusUpdateRequest;
import com.library.dto.response.UserResponse;
import com.library.service.ProfileService;
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
    public ResponseEntity<List<UserResponse>> getAllProfiles() {
        List<UserResponse> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchProfiles(
            @RequestParam(required = false) String keyword) {
        List<UserResponse> profiles = profileService.searchProfiles(keyword);
        return ResponseEntity.ok(profiles);
    }

    @PostMapping("/add")
    public ResponseEntity<UserResponse> addProfile(@Valid @RequestBody AddProfileRequest request) {
        UserResponse response = profileService.addProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<UserResponse> changeProfileStatus(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        UserResponse response = profileService.changeProfileStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
