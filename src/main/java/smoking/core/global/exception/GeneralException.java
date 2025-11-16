package smoking.core.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smoking.core.global.error.code.status.BaseErrorCode;
import smoking.core.global.error.code.status.ErrorReasonDTO;


@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
