package com.Backend.SalonBooking.Services.Email;

import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;

public interface EmployeeInvitationEmailService {

    boolean sendEmployeeInvitation(Salonemps employee);
}
