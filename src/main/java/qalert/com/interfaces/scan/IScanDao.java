package qalert.com.interfaces.scan;

import java.util.List;

import qalert.com.models.generic.Response2;
import qalert.com.models.scan.ScanHeaderResponse;
import qalert.com.models.scan.ScanRequest;
import qalert.com.models.scan.ScanResponse;

public interface  IScanDao {

    Response2<ScanResponse> insertAndGetAdditivesFromPlainText(Long profileId, String data);

    void insert(ScanRequest request);

    Response2<ScanResponse> getAdditivesReport(ScanRequest request);

    Response2<List<ScanHeaderResponse>> getScanList(Long profileId);

    void sp_update_scan_header(ScanRequest request,int operacion);

}
