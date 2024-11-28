package app.toysocialnetwork.repository.database;

import app.toysocialnetwork.domain.Friendship;
import app.toysocialnetwork.domain.validators.ValidationException;
import app.toysocialnetwork.domain.validators.Validator;
import app.toysocialnetwork.repository.AbstractRepository;
import app.toysocialnetwork.utils.NetworkDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDBRepository implements AbstractRepository<Long, Friendship> {

    private final Validator<Friendship> validator;

    public FriendshipDBRepository(Validator<Friendship> validator) {
        this.validator = validator;
    }

    private Connection connect() throws SQLException {
        return NetworkDB.getInstance().getConnection();
    }

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

    public Set<Friendship> findAllByUserId(Long userId) {
        Set<Friendship> friendships = new HashSet<>();
        String query = "SELECT * FROM friendships WHERE user_id1 = ? OR user_id2 = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
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
}