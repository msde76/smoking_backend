package smoking.core.global.error.code.status;

public interface BaseCode {

    String getCode();

    String getMessage();

    ReasonDTO getReasonHttpStatus();
}
