package mopl.api.domain.content.exception;

import lombok.Getter;
import mopl.api.global.exception.DomainException;

@Getter
public class ContentException extends DomainException {
    public ContentException(ContentErrorCode errorCode) {
        super(errorCode);
    }
}
