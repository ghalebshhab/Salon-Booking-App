package com.Backend.SalonBooking.Controllers.Salon;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.HiringEmps.CreateHirePostRequest;
import com.Backend.SalonBooking.Dtos.HiringEmps.HirePostResponse;
import com.Backend.SalonBooking.Services.Salon.Hiring.SalonHiringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
@RestController
@RequestMapping("/api/hiring-posts")
@RequiredArgsConstructor
public class HiringController {

    private final SalonHiringService salonHiringService;

    @PostMapping
    public ResponseEntity<ApiResponse<HirePostResponse>> createHirePost(
            @RequestBody CreateHirePostRequest request
    ) {
        return ResponseEntity.ok(salonHiringService.createHirePost(request));
    }

    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<HirePostResponse>>> getOpenHiringPosts() {
        return ResponseEntity.ok(salonHiringService.getOpenHiringPosts());
    }

    @PostMapping("/{postId}/join")
    public ResponseEntity<ApiResponse<String>> joinHiringSalon(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(salonHiringService.joinHiringSalon(postId));
    }

    @PutMapping("/{postId}/close")
    public ResponseEntity<ApiResponse<String>> closeHiringPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(salonHiringService.closeHiringPost(postId));
    }
}