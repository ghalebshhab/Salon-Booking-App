package com.Backend.SalonBooking.Services.Media;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.SalonMedia.CreateSalonMediaRequest;
import com.Backend.SalonBooking.Dtos.SalonMedia.SalonMediaResponse;

import java.util.List;

public interface SalonMediaService {

    ApiResponse<SalonMediaResponse> createMediaPost(CreateSalonMediaRequest request);

    ApiResponse<List<SalonMediaResponse>> getSalonMediaPosts(Long salonId);

    ApiResponse<List<SalonMediaResponse>> getSalonVideos(Long salonId);

    ApiResponse<String> deleteMediaPost(Long mediaPostId);
}