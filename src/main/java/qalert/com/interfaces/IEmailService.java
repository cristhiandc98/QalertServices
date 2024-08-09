package qalert.com.interfaces;

import qalert.com.models.email.SendEmailRequest;
import qalert.com.models.generic.Response_;

public interface IEmailService {

    Response_<String> sendSimpleEmail(SendEmailRequest request, String mssgOk);

}
