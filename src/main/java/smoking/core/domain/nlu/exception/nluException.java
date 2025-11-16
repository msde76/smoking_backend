package smoking.core.domain.nlu.exception;

import smoking.core.global.error.code.status.BaseErrorCode;
import smoking.core.global.exception.GeneralException;

public class nluException extends GeneralException {

    public nluException(BaseErrorCode code) { super(code); }
}
