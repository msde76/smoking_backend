package smoking.core.domain.device.exception;

import smoking.core.global.error.code.status.BaseErrorCode;
import smoking.core.global.exception.GeneralException;

public class deviceException extends GeneralException {

    public deviceException(BaseErrorCode code) { super(code); }
}
