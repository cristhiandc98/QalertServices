package qalert.com.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IEmailService;
import qalert.com.models.email.SendEmailRequest;
import qalert.com.models.generic.Response_;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.Consts;
import qalert.com.utils.utils.DateUtil;

@Qualifier(Consts.QALIFIER_SERVICE)
@Service
public class SecurityServiceImpl implements ISeguridad{

    @Qualifier(Consts.QALIFIER_DAO)
    @Autowired
    private ISeguridad dao;

    @Autowired
    private IEmailService emailService;
    
	private static final Logger logger = LogManager.getLogger(SecurityServiceImpl.class);
	
    @Override
    public Response_<String> saveVerificationCode(UserRequest request) {
        Response_<String> out = null;

        try {
            //Generate verification code
            int codMin = 101, codMax = 999, randomCode = 0;
            randomCode = (int)Math.floor(Math.random() * (codMax - codMin + 1) + codMin);
            request.getLogin().setVerificationCode(String.valueOf(randomCode));

            Response_<String> rptBD = dao.saveVerificationCode(request);

            if(rptBD.isStatus()){
                SendEmailRequest sendEmail = new SendEmailRequest();
                sendEmail.setTo(request.getEmail());
                sendEmail.setSubject("Verificación de correo");
                sendEmail.setBody("Su código de verificación es: " + randomCode);

                String msjOk = "Se envió un código de verificación al correo: " + request.getEmail();

                return emailService.sendSimpleEmail(sendEmail, msjOk);
            }
            else return rptBD;

        } catch (Exception e) {
            logger.error((out = new Response_<>(e, request, "Error al validar el código de verificación")).getErrorMssg());
        }

        return out;
    }

	@Override
	public Response_<String> resetIdDevice(UserRequest request) {
		request.getLogin().setDeviceId(Integer.parseInt(request.getLogin().getVerificationCode() + DateUtil.generateId()));
		return dao.resetIdDevice(request);
	}
}
