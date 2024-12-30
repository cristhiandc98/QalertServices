package qalert.com.interfaces.scan;

import qalert.com.models.generic.Response2;
import qalert.com.models.scan.ScanRequest;
import qalert.com.models.scan.ScanResponse;

public interface  IScanDao {

    Response2<ScanResponse> insertAndGetAdditivesFromPlainText(int profileId, String data);

    Response2<Boolean> insert(ScanRequest request);

}
