package qalert.com.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IEmailService;
import qalert.com.models.email.SendEmailRequest;
import qalert.com.models.generic.Response2;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.EnvironmentConst;
import qalert.com.utils.utils.DateUtil;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class SecurityServiceImpl implements ISecurity{
	
	@Autowired
	private Environment env;

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private ISecurity dao;

    @Autowired
    private IEmailService emailService;
    
	private static final Logger logger = LogManager.getLogger(SecurityServiceImpl.class);
	
    @Override
    public Response2<String> saveVerificationCode(UserRequest request, boolean isChangeDevice) {
        Response2<String> out;

        try {
            //Generate verification code
            int codMin = 101, codMax = 999, randomCode;
            randomCode = (int)Math.floor(Math.random() * (codMax - codMin + 1) + codMin);
            request.getLogin().setVerificationCode(String.valueOf(randomCode));

            Response2<String> rptBD = dao.saveVerificationCode(request, isChangeDevice);

            if(rptBD.isStatus()){
                SendEmailRequest sendEmail = new SendEmailRequest();
                sendEmail.setTo(request.getEmail());
                sendEmail.setSubject(env.getRequiredProperty(EnvironmentConst.APP_NAME) + " - VERIFICACIÓN DE CORREO");
                sendEmail.setBody("Su código de verificación es: " + randomCode);

                out = emailService.sendSimpleEmail(sendEmail, rptBD.getUserMssg());
                //if(out.isStatus()) out.setData(String.valueOf(randomCode));
            }
            else out = rptBD;

        } catch (IllegalStateException e) {
            logger.error((out = new Response2<>(e, "Error al generar el código de verificación")).getErrorMssg());
        }

        return out;
    }

	@Override
	public Response2<String> resetIdDevice(UserRequest request) {
		request.getLogin().setDeviceId(Integer.valueOf(DateUtil.generateId()));
		return dao.resetIdDevice(request);
	}
}
