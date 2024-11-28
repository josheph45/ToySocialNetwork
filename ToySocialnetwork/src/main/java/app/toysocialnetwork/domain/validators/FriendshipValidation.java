package app.toysocialnetwork.domain.validators;

import app.toysocialnetwork.domain.Friendship;

import java.util.Objects;

public class FriendshipValidation implements Validator<Friendship> {
    @Override
    public void validate(Friendship friendship) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        // user1Id && user2Id cant be null
        if (friendship.getUser1Id() == null) {
            errors.append("User1Id cannot be null\n");
        }
        if (friendship.getUser2Id() == null) {
            errors.append("User2Id cannot be null\n");
        }

        // user1Id && user2Id cant be the same
        if (Objects.equals(friendship.getUser1Id(), friendship.getUser2Id())) {
            errors.append("User1Id and User2Id cannot be the same\n");
        }

        // throw exception if there are errors
        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
