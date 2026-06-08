package com.Backend.SalonBooking.Services.Email;

import com.Backend.SalonBooking.Entities.SalonEmployees.Salonemps;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmployeeInvitationEmailServiceImpl implements EmployeeInvitationEmailService {

    private final JavaMailSender mailSender;

    public EmployeeInvitationEmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean sendEmployeeInvitation(Salonemps employee) {
        try {
            String salonName = employee.getSalon() != null ? employee.getSalon().getName() : "SalonHub Salon";

            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(employee.getEmail());
            helper.setSubject("You are invited to join " + salonName + " on SalonHub");

            helper.setText(buildHtml(employee), true);

            mailSender.send(message);
            return true;

        } catch (Exception e) {
            System.out.println("Failed to send employee invitation email: " + e.getMessage());
            return false;
        }
    }

    private String buildHtml(Salonemps employee) {

        String salonName = employee.getSalon() != null ? employee.getSalon().getName() : "SalonHub Salon";
        String employeeName = employee.getFullName() != null ? employee.getFullName() : "Employee";
        String specialty = employee.getSpecialty() != null ? employee.getSpecialty() : "Salon Employee";

        String startTime = employee.getStartTime() != null ? employee.getStartTime().toString() : "-";
        String endTime = employee.getEndTime() != null ? employee.getEndTime().toString() : "-";

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0;padding:0;background:#fff9f5;font-family:Arial,Helvetica,sans-serif;color:#2b2b2b;">
                    <div style="max-width:620px;margin:0 auto;padding:32px 18px;">
                        <div style="background:#ffffff;border:1px solid #f0d9d9;border-radius:26px;overflow:hidden;box-shadow:0 18px 45px rgba(43,43,43,0.08);">
                            <div style="padding:28px;background:linear-gradient(135deg,#c08497,#9f6478);color:#ffffff;">
                                <h1 style="margin:0;font-size:28px;">SalonHub Invitation</h1>
                                <p style="margin:8px 0 0;font-size:15px;opacity:.92;">You have been invited to join a salon team.</p>
                            </div>

                            <div style="padding:30px;">
                                <p style="font-size:17px;margin:0 0 16px;">Hello <strong>""" + employeeName + """
                                </strong>,</p>

                                <p style="line-height:1.7;margin:0 0 20px;color:#4b5563;">
                                    You have been invited to join <strong>""" + salonName + """
                                    </strong> on SalonHub.
                                </p>

                                <div style="background:#fff9f5;border:1px solid #f0d9d9;border-radius:20px;padding:20px;margin:22px 0;">
                                    <p style="margin:0 0 10px;"><strong>Salon:</strong> """ + salonName + """
                                    </p>
                                    <p style="margin:0 0 10px;"><strong>Specialty:</strong> """ + specialty + """
                                    </p>
                                    <p style="margin:0;"><strong>Working time:</strong> """ + startTime + " - " + endTime + """
                                    </p>
                                </div>

                                <p style="line-height:1.7;color:#4b5563;">
                                    Please create an account using this email:
                                    <strong>""" + employee.getEmail() + """
                                    </strong>
                                </p>

                                <p style="line-height:1.7;color:#4b5563;">
                                    After registration, your account will be automatically connected to the salon.
                                </p>

                                <div style="margin-top:28px;">
                                    <a href="http://localhost:5173/register"
                                       style="display:inline-block;background:#c08497;color:#ffffff;text-decoration:none;padding:14px 22px;border-radius:999px;font-weight:800;">
                                        Create Account
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;
    }
}