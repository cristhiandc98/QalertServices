package qalert.com.interfaces.scan;

import qalert.com.models.generic.Response2;

public interface  IScanDao {

    Response2<Boolean> insertAndGetAdditivesFromPlainText(int profileId, String data);

}
