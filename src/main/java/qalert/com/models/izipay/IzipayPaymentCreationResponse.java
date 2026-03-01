package qalert.com.models.izipay;

public class IzipayPaymentCreationResponse {

    private String status;
    private Answer answer;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}

