package app.toysocialnetwork.domain.validators;

import app.toysocialnetwork.domain.Friendship;
import app.toysocialnetwork.domain.Request;

import java.util.Objects;

public class RequestValidation implements Validator<Request> {
    @Override
    public void validate(Request request) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        // senderId && receiverId cant be null
        if (request.getSenderId() == null) {
            errors.append("SenderId cannot be null\n");
        }
        if (request.getReceiverId() == null) {
            errors.append("ReceiverId cannot be null\n");
        }

        // senderId && receiverId cant be the same
        if (Objects.equals(request.getSenderId(), request.getReceiverId())) {
            errors.append("SenderId and ReceiverId cannot be the same\n");
        }

        // throw exception if there are errors
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
