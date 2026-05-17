package com.Backend.SalonBooking.Services.Salon.Hiring;


import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.HiringEmps.CreateHirePostRequest;
import com.Backend.SalonBooking.Dtos.HiringEmps.HirePostResponse;

import java.util.List;

public interface SalonHiringService {
    ApiResponse<HirePostResponse> createHirePost(CreateHirePostRequest request);
    ApiResponse<String> joinHiringSalon(Long postId);
    ApiResponse<List<HirePostResponse>> getOpenHiringPosts();
    ApiResponse<String> closeHiringPost(Long postId);

}
