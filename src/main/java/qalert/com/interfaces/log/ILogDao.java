package qalert.com.interfaces.log;

import qalert.com.models.service_log.LogServiceRequest;

public interface ILogDao {
    void insert(LogServiceRequest request);
}
