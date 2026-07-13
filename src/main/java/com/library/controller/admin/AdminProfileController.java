package com.library.controller.admin;

import com.library.dto.request.AddProfileRequest;
import com.library.dto.request.StatusUpdateRequest;
import com.library.dto.response.UserResponse;
import com.library.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/profile")
@RestController
@RequiredArgsConstructor
public class AdminProfileController {

    private final ProfileService profileService;

    @PostMapping()
    @Operation(summary = "Add new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "created"),
            @ApiResponse(responseCode = "404", description = "bad request")
    })
    public ResponseEntity<UserResponse> addProfile(@Valid @RequestBody AddProfileRequest request) {
        UserResponse response = profileService.addProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
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

    @PutMapping("/{id}")
    @Operation(summary = "Change profile status")
    public ResponseEntity<UserResponse> changeProfileStatus(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        UserResponse response = profileService.changeProfileStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "delete profile")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User o'chirildi"),
            @ApiResponse(responseCode = "404", description = "Topilmadi")
    })
    public ResponseEntity<UserResponse> deleteProfile(@Parameter(description = "Foydalanuvchi ID", example = "1")
                                                      @PathVariable Long userId) {
        UserResponse response = profileService.removeProfile(userId);
        return ResponseEntity.ok(response);
    }
}
