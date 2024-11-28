package app.toysocialnetwork.domain.validators;

import app.toysocialnetwork.domain.User;

public class UserValidation implements Validator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        // firstName && lastName && username && password cant be null
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            errors.append("First name cannot be empty\n");
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            errors.append("Last name cannot be empty\n");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getUsername().length() < 5) {
            errors.append("Username must be at least 5 characters long\n");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().length() < 5) {
            errors.append("Password must be at least 5 characters long\n");
        }

        // firstName && lastName can only contain letters
        if (user.getFirstName().matches(".*\\d.*")) {
            errors.append("First name can only contain letters\n");
        }
        if (user.getLastName().matches(".*\\d.*")) {
            errors.append("Last name can only contain letters\n");
        }

        // firstName && lastName cant be longer than 20 characters
        if (user.getFirstName().length() > 20) {
            errors.append("First name cannot be longer than 20 characters\n");
        }
        if (user.getLastName().length() > 20) {
            errors.append("Last name cannot be longer than 20 characters\n");
        }
        if (user.getUsername().length() > 20) {
            errors.append("Username cannot be longer than 20 characters\n");
        }
        if (user.getPassword().length() > 20) {
            errors.append("Password cannot be longer than 20 characters\n");
        }

        // throw exception if there are errors
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
