package qalert.com.models.master;

public class MasterResponse {

    private Integer masterId;

    private Integer tableId;

    private Integer fieldId;

    private Integer sequence;

    private Integer valueInt;

    private String valueVarchar;

    public MasterResponse() {
    }

    public MasterResponse(Integer masterId, Integer tableId, Integer fieldId, Integer sequence, Integer valueInt,
    String valueVarchar) {
        this.masterId = masterId;
        this.tableId = tableId;
        this.fieldId = fieldId;
        this.sequence = sequence;
        this.valueInt = valueInt;
        this.valueVarchar = valueVarchar;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getValueInt() {
        return valueInt;
    }

    public void setValueInt(Integer valueInt) {
        this.valueInt = valueInt;
    }

    public String getValueVarchar() {
        return valueVarchar;
    }

    public void setValueVarchar(String valueVarchar) {
        this.valueVarchar = valueVarchar;
    }

    

}
