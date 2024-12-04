package app.toysocialnetwork.domain.validators;

import app.toysocialnetwork.domain.Message;

public class MessageValidation implements Validator<Message> {
    @Override
    public void validate(Message message) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        // from && to && text && date cant be null
        if (message.getFrom() == null) {
            errors.append("From cannot be null\n");
        }
        if (message.getTo() == null) {
            errors.append("To cannot be null\n");
        }
        if (message.getText() == null || message.getText().isEmpty()) {
            errors.append("Text cannot be empty\n");
        }
        if (message.getDate() == null) {
            errors.append("Date cannot be null\n");
        }

        // text cant be longer than 255 characters
        if (message.getText().length() > 255) {
            errors.append("Text cannot be longer than 255 characters\n");
        }

        // throw exception if there are errors
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
