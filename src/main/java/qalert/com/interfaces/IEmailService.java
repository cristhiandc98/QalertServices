package qalert.com.interfaces;

import qalert.com.models.email.SendEmailRequest;
import qalert.com.models.generic.Response2;

public interface IEmailService {

    Response2<String> sendSimpleEmail(SendEmailRequest request, String mssgOk);

}
