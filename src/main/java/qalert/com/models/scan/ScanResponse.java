package qalert.com.models.scan;

import java.util.ArrayList;
import java.util.List;

public class ScanResponse {

    private final List<ScanHeaderResponse> headerList;

    private final List<ScanDetailResponse> detailList;

    public ScanResponse() {
        headerList = new ArrayList<>();
        detailList = new ArrayList<>();
    }

    public List<ScanHeaderResponse> getHeaderList() {
        return headerList;
    }

    public void addScanHeader(ScanHeaderResponse scanHeader) {
        this.headerList.add(scanHeader);
    }

    public List<ScanDetailResponse> getDetailList() {
        return detailList;
    }

    public void addScanDetail(ScanDetailResponse scanDetail) {
        this.detailList.add(scanDetail);
    }



}
