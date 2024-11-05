package qalert.com.interfaces;

import qalert.com.models.generic.Response_;
import qalert.com.models.service_log.ServiceLogRequest;

public interface  IServiceLog {

    Response_<Boolean> insert(ServiceLogRequest request);

}
