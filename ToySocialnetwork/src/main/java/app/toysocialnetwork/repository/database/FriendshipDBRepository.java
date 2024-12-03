package app.toysocialnetwork.repository.database;

import app.toysocialnetwork.domain.Friendship;
import app.toysocialnetwork.domain.validators.ValidationException;
import app.toysocialnetwork.domain.validators.Validator;
import app.toysocialnetwork.repository.AbstractRepository;
import app.toysocialnetwork.utils.NetworkDB;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class FriendshipDBRepository implements AbstractRepository<Long, Friendship> {
    private final Validator<Friendship> validator;

    /**
     * Constructor that creates a new FriendshipDBRepository
     * @param validator
     * validator must not be null
     */
    public FriendshipDBRepository(Validator<Friendship> validator) {
        this.validator = validator;
    }

    /**
     * Establish a connection to the database
     * @return a connection to the database
     * @throws SQLException
     * if the connection cannot be established
     */
    private Connection connect() throws SQLException {
        return NetworkDB.getInstance().getConnection();
    }

    /**
     * Find the entity with the given id
     * @param id -the id of the entity to be returned
     * id must not be null
     * @return an {@code Optional} encapsulating the entity with the given id
     */
    @Override
    public Optional<Friendship> findOne(Long id) {
        String query = "SELECT * FROM friendships WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long userId1 = rs.getLong("user_id1");
                Long userId2 = rs.getLong("user_id2");
                LocalDateTime friendsFrom = rs.getTimestamp("friends_from").toLocalDateTime();
                Friendship friendship = new Friendship(userId1, userId2, friendsFrom);
                friendship.setId(id);
                return Optional.of(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Find all entities
     * @return an {@code Iterable} containing all entities
     */
    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                Long userId1 = rs.getLong("user_id1");
                Long userId2 = rs.getLong("user_id2");
                LocalDateTime friendsFrom = rs.getTimestamp("friends_from").toLocalDateTime();
                Friendship friendship = new Friendship(userId1, userId2, friendsFrom);
                friendship.setId(id);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    /**
     * Save the entity to the database
     * @param friendship
     * entity must be not null
     * @return an {@code Optional}
     * - null if the entity was saved
     * - the entity (id already exists)
     * @throws ValidationException
     * if the entity is not valid
     * @throws IllegalArgumentException
     * if the given entity is null
     */
    @Override
    public Optional<Friendship> save(Friendship friendship) throws ValidationException {
        validator.validate(friendship);
        String query = "INSERT INTO friendships (user_id1, user_id2, friends_from) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, friendship.getUser1Id());
            stmt.setLong(2, friendship.getUser2Id());
            stmt.setTimestamp(3, Timestamp.valueOf(friendship.getFriendsFrom()));
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    friendship.setId(generatedKeys.getLong(1));
                    return Optional.of(friendship);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Remove the entity with the specified id
     * @param id
     * id must be not null
     * @return an {@code Optional}
     * - null if there is no entity with the given id
     * - the removed entity, otherwise
     * @throws IllegalArgumentException
     * if the given id is null
     */
    @Override
    public Optional<Friendship> delete(Long id) {
        String query = "DELETE FROM friendships WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(new Friendship(0L, 0L, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Update the entity in the database
     * @param friendship
     * entity must not be null
     * @return an {@code Optional}
     * - null if the entity was updated
     * - otherwise (e.g. id does not exist) returns the entity
     * @throws IllegalArgumentException
     * if the given entity is null
     * @throws ValidationException
     * if the entity is not valid
     */
    @Override
    public Optional<Friendship> update(Friendship friendship) throws ValidationException {
        validator.validate(friendship);
        String query = "UPDATE friendships SET user_id1 = ?, user_id2 = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, friendship.getUser1Id());
            stmt.setLong(2, friendship.getUser2Id());
            stmt.setLong(3, friendship.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}