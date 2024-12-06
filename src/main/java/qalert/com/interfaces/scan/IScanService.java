package qalert.com.interfaces.scan;

import org.springframework.web.multipart.MultipartFile;

import qalert.com.models.generic.Response2;

public interface IScanService extends IScanDao{

    Response2<String> detectAdditives(MultipartFile file);

}
