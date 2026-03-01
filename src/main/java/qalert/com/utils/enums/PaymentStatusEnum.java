package qalert.com.utils.enums;

import java.util.Arrays;

public enum PaymentStatusEnum {

    PENDING(8),
    PAID(9),
    FAILED(10),
    CANCELLED(11),
    EXPIRED(12),
    REFUNDED(13),
    ERROR_GENERATE_URL(14);

    private final int statusId;

    PaymentStatusEnum(int statusId) {
        this.statusId = statusId;
    }

    public int getStatusId() {
        return statusId;
    }

    public static PaymentStatusEnum fromId(int id) {
        return Arrays.stream(values())
                .filter(status -> status.statusId == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid PaymentStatus id: " + id));
    }

    public static PaymentStatusEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid PaymentStatus code: " + code));
    }
}