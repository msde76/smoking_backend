package smoking.core.domain.smoking.exception;

import smoking.core.global.error.code.status.BaseErrorCode;
import smoking.core.global.exception.GeneralException;

public class smokingException extends GeneralException {

    public smokingException(BaseErrorCode code) { super(code); }
}
