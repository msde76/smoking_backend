package smoking.core.domain.route.exception;

import smoking.core.global.error.code.status.BaseErrorCode;
import smoking.core.global.exception.GeneralException;

public class routeException extends GeneralException {

    public routeException(BaseErrorCode code) { super(code); }
}
