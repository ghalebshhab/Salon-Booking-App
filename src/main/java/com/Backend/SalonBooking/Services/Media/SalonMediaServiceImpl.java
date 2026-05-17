package com.Backend.SalonBooking.Services.Media;
import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.SalonMedia.CreateSalonMediaRequest;
import com.Backend.SalonBooking.Dtos.SalonMedia.SalonMediaResponse;
import com.Backend.SalonBooking.Entities.SalonProfileMedia.MediaType;
import com.Backend.SalonBooking.Entities.SalonProfileMedia.SalonMediaPost;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.SalonMediaRepo;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonMediaServiceImpl implements SalonMediaService {

    private final SalonMediaRepo salonMediaPostRepo;
    private final SalonRepo salonRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public ApiResponse<SalonMediaResponse> createMediaPost(CreateSalonMediaRequest request) {

        User owner = getCurrentUser();

        Salon salon = salonRepo.findByOwnerId(owner.getId())
                .orElse(null);

        if (salon == null) {
            return ApiResponse.error("You do not have a salon");
        }

        if (request.getMediaUrl() == null || request.getMediaUrl().isBlank()) {
            return ApiResponse.error("Media URL is required");
        }

        if (request.getMediaType() == null) {
            return ApiResponse.error("Media type is required");
        }

        SalonMediaPost post = new SalonMediaPost();

        post.setCaption(request.getCaption());
        post.setMediaUrl(request.getMediaUrl().trim());
        post.setMediaType(request.getMediaType());
        post.setCreatedAt(LocalDateTime.now());
        post.setSalon(salon);

        SalonMediaPost savedPost = salonMediaPostRepo.save(post);

        return ApiResponse.success("Media post created successfully", mapToResponse(savedPost));
    }

    @Override
    public ApiResponse<List<SalonMediaResponse>> getSalonMediaPosts(Long salonId) {

        Salon salon = salonRepo.findById(salonId)
                .orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found");
        }

        List<SalonMediaResponse> response = salonMediaPostRepo
                .findBySalonIdOrderByCreatedAtDesc(salonId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Salon media posts returned successfully", response);
    }

    @Override
    public ApiResponse<List<SalonMediaResponse>> getSalonVideos(Long salonId) {

        Salon salon = salonRepo.findById(salonId)
                .orElse(null);

        if (salon == null) {
            return ApiResponse.error("Salon not found");
        }

        List<SalonMediaResponse> response = salonMediaPostRepo
                .findBySalonIdAndMediaTypeOrderByCreatedAtDesc(salonId, MediaType.VIDEO)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success("Salon videos returned successfully", response);
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteMediaPost(Long mediaPostId) {

        User owner = getCurrentUser();

        SalonMediaPost post = salonMediaPostRepo.findById(mediaPostId)
                .orElse(null);

        if (post == null) {
            return ApiResponse.error("Media post not found");
        }

        if (!post.getSalon().getOwner().getId().equals(owner.getId())) {
            return ApiResponse.error("You are not allowed to delete this media post");
        }

        salonMediaPostRepo.delete(post);

        return ApiResponse.success("Media post deleted successfully", null);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private SalonMediaResponse mapToResponse(SalonMediaPost post) {

        SalonMediaResponse response = new SalonMediaResponse();

        response.setId(post.getId());
        response.setCaption(post.getCaption());
        response.setMediaUrl(post.getMediaUrl());
        response.setMediaType(post.getMediaType());
        response.setCreatedAt(post.getCreatedAt());

        if (post.getSalon() != null) {
            response.setSalonId(post.getSalon().getId());
            response.setSalonName(post.getSalon().getName());
        }

        return response;
    }
}