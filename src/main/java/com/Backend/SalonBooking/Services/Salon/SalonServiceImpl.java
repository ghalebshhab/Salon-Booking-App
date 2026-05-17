package com.Backend.SalonBooking.Services.Salon;

import com.Backend.SalonBooking.Dtos.ApiResponse;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonRequest;
import com.Backend.SalonBooking.Dtos.Salons.CreateSalonResponse;
import com.Backend.SalonBooking.Dtos.Salons.UpdateSalonInfoRequest;
import com.Backend.SalonBooking.Entities.Salons.Salon;
import com.Backend.SalonBooking.Entities.Salons.State;
import com.Backend.SalonBooking.Entities.Users.User;
import com.Backend.SalonBooking.Repositories.SalonRepo;
import com.Backend.SalonBooking.Repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SalonServiceImpl implements SalonService {

    private final UserRepo userRepo;
    private final SalonRepo salonRepo;

    @Override
    public ApiResponse<CreateSalonResponse> createSalon(CreateSalonRequest createSalonRequest,String emailFromToken) {

    if(createSalonRequest.getName()==null){
        return ApiResponse.error("Name is required");
    }
    if(createSalonRequest.getCity()==null){
        return ApiResponse.error("City is required");
    }
    if(createSalonRequest.getOpenTime()==null){
        return ApiResponse.error("Open time is required");
    }
    if (createSalonRequest.getCloseTime()==null){
        return ApiResponse.error("Close time is required");
    }
    if (createSalonRequest.getAddress()==null){
        return ApiResponse.error("Address is required");
    }
    if (createSalonRequest.getEmail()==null){
        return ApiResponse.error("Email is required");
    }
    if (createSalonRequest.getPhoneNumber() == null ||
                (!createSalonRequest.getPhoneNumber().matches("^\\+9627\\d{8}$")
                        && !createSalonRequest.getPhoneNumber().matches("^07\\d{8}$"))) {
        return ApiResponse.error("Phone number must be like +9627XXXXXXXX or 07XXXXXXXX");
    }

    User user=userRepo.findByEmail(emailFromToken).orElse(null);
        if(user==null){
            return ApiResponse.error("User With This Email not found");
        }

        Salon salon=new Salon();
        salon.setCity(createSalonRequest.getCity());
        salon.setAddress(createSalonRequest.getAddress());
        salon.setEmail(createSalonRequest.getEmail());
        salon.setPhoneNumber(createSalonRequest.getPhoneNumber());
        salon.setName(createSalonRequest.getName());
        salon.setOpenTime(createSalonRequest.getOpenTime());
        salon.setCloseTime(createSalonRequest.getCloseTime());
        salon.setOwner(user);
        salon.setImages(createSalonRequest.getImages());

        salonRepo.save(salon);

        CreateSalonResponse response=toResponse(salon);

      return ApiResponse.success("Salon Created Successfully", response);

    }

    @Override
    public ApiResponse<CreateSalonResponse> updateSalon(UpdateSalonInfoRequest updateSalonInfoRequest,
                        String emailFromToken,
                        Long salonId) {
    User user=userRepo.findByEmail(emailFromToken).orElse(null);
    if(user==null){
        return ApiResponse.error("User not found");
    }
    Salon salon=salonRepo.findById(salonId).orElse(null);
     if (salon==null){
         return ApiResponse.error("Salon not found");
     }
     if(!salon.getOwner().getId().equals(user.getId())){
         return ApiResponse.error("You Can Only Update Your Own Salon");
     }
        if(updateSalonInfoRequest.getName()==null){
            return ApiResponse.error("Name is required");
        }
        if(updateSalonInfoRequest.getCity()==null){
            return ApiResponse.error("City is required");
        }
        if(updateSalonInfoRequest.getOpenTime()==null){
            return ApiResponse.error("Open time is required");
        }
        if (updateSalonInfoRequest.getCloseTime()==null){
            return ApiResponse.error("Close time is required");
        }
        if (updateSalonInfoRequest.getAddress()==null){
            return ApiResponse.error("Address is required");
        }
        if (updateSalonInfoRequest.getEmail()==null){
            return ApiResponse.error("Email is required");
        }
        if (updateSalonInfoRequest.getPhoneNumber() == null ||
                (!updateSalonInfoRequest.getPhoneNumber().matches("^\\+9627\\d{8}$")
                        && !updateSalonInfoRequest.getPhoneNumber().matches("^07\\d{8}$"))) {
            return ApiResponse.error("Phone number must be like +9627XXXXXXXX or 07XXXXXXXX");
        }
        salon.setName(updateSalonInfoRequest.getName());
        salon.setOpenTime(updateSalonInfoRequest.getOpenTime());
        salon.setCloseTime(updateSalonInfoRequest.getCloseTime());
        salon.setAddress(updateSalonInfoRequest.getAddress());
        salon.setEmail(updateSalonInfoRequest.getEmail());
        salon.setPhoneNumber(updateSalonInfoRequest.getPhoneNumber());
        salon.setImages(updateSalonInfoRequest.getImages());
        salon.setCity(updateSalonInfoRequest.getCity());
        salonRepo.save(salon);
        CreateSalonResponse response=toResponse(salon);

        return ApiResponse.success("Salon Information Updated Successfully", response);
    }

    @Override
    public ApiResponse<String> deleteSalon(String emailFromToken) {
    User user=userRepo.findByEmail(emailFromToken).orElse(null);
    if(user==null){
        return ApiResponse.error("User not found");
    }
    Salon salon=salonRepo.findByowner(user).orElse(null);
    if(salon==null){
        return ApiResponse.error("Salon not found");
    }
    salon.setIsDeleted(true);
    salonRepo.save(salon);

        return ApiResponse.success("Salon Deleted Successfully",null);
    }

    @Override
    public ApiResponse<CreateSalonResponse> getSalonById(Long salonId) {
        Salon salon=salonRepo.findById(salonId).orElse(null);
        if(salon==null){
            return ApiResponse.error("Salon not found");
        }


        return ApiResponse.success("Salon Fetched Successfully", toResponse(salon));
    }


    private CreateSalonResponse toResponse(Salon salon){
        CreateSalonResponse response=new CreateSalonResponse();
        response.setAddress(salon.getAddress());
        response.setEmail(salon.getEmail());
        response.setCity(salon.getCity());
        response.setOpenTime(salon.getOpenTime());
        response.setCloseTime(salon.getCloseTime());
        response.setName(salon.getName());
        response.setPhoneNumber(salon.getPhoneNumber());
        response.setImages(salon.getImages());
        response.setState(salon.getState());
        response.setOwnerId(salon.getOwner().getId());
        response.setSalonId(salon.getId());
        response.setDeleted(salon.getIsDeleted());
        return response;
    }
    private State calculateState(LocalTime openTime, LocalTime closeTime) {

        LocalTime now = LocalTime.now();

        boolean isOpen =
                !now.isBefore(openTime) &&
                        now.isBefore(closeTime);

        return isOpen ? State.OPEN : State.CLOSED;
    }


}
