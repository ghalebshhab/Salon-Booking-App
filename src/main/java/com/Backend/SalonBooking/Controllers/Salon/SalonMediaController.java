package com.Backend.SalonBooking.Controllers.Salon;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.SalonMedia.CreateSalonMediaRequest;
import com.Backend.SalonBooking.Dtos.SalonMedia.SalonMediaResponse;
import com.Backend.SalonBooking.Services.Media.SalonMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
@RestController
@RequestMapping("/api/salon-media")
@RequiredArgsConstructor
public class SalonMediaController {

    private final SalonMediaService salonMediaService;

    @PostMapping
    public ResponseEntity<ApiResponse<SalonMediaResponse>> createMediaPost(
            @RequestBody CreateSalonMediaRequest request
    ) {
        return ResponseEntity.ok(salonMediaService.createMediaPost(request));
    }

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<ApiResponse<List<SalonMediaResponse>>> getSalonMediaPosts(
            @PathVariable Long salonId
    ) {
        return ResponseEntity.ok(salonMediaService.getSalonMediaPosts(salonId));
    }

    @GetMapping("/salon/{salonId}/videos")
    public ResponseEntity<ApiResponse<List<SalonMediaResponse>>> getSalonVideos(
            @PathVariable Long salonId
    ) {
        return ResponseEntity.ok(salonMediaService.getSalonVideos(salonId));
    }

    @DeleteMapping("/{mediaPostId}")
    public ResponseEntity<ApiResponse<String>> deleteMediaPost(
            @PathVariable Long mediaPostId
    ) {
        return ResponseEntity.ok(salonMediaService.deleteMediaPost(mediaPostId));
    }
}