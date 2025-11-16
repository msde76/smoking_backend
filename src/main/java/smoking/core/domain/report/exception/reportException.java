package smoking.core.domain.report.exception;

import smoking.core.global.error.code.status.BaseErrorCode;
import smoking.core.global.exception.GeneralException;

public class reportException extends GeneralException {

    public reportException(BaseErrorCode code) { super(code); }
}
