package app.toysocialnetwork.repository.database;

import app.toysocialnetwork.domain.Request;
import app.toysocialnetwork.domain.validators.ValidationException;
import app.toysocialnetwork.domain.validators.Validator;
import app.toysocialnetwork.repository.AbstractRepository;
import app.toysocialnetwork.utils.NetworkDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestDBRepository implements AbstractRepository<Long, Request> {
    private final Validator<Request> validator;

    public RequestDBRepository(Validator<Request> validator) {
        this.validator = validator;
    }

    private Connection connect() throws SQLException {
        return NetworkDB.getInstance().getConnection();
    }

    @Override
    public Optional<Request> findOne(Long id) {
        String query = "SELECT * FROM requests WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long senderId = rs.getLong("sender_id");
                Long receiverId = rs.getLong("receiver_id");
                Request request = new Request(senderId, receiverId);
                request.setId(id);
                return Optional.of(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Request> findAll() {
        List<Request> requests = new ArrayList<>();
        String query = "SELECT * FROM requests";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                Long senderId = rs.getLong("sender_id");
                Long receiverId = rs.getLong("receiver_id");
                Request request = new Request(senderId, receiverId);
                request.setId(id);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public Optional<Request> save(Request request) throws ValidationException {
        validator.validate(request);
        String query = "INSERT INTO requests (sender_id, receiver_id) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, request.getSenderId());
            stmt.setLong(2, request.getReceiverId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    request.setId(generatedKeys.getLong(1));
                    return Optional.of(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Request> delete(Long id) {
        String query = "DELETE FROM requests WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(new Request(0L, 0L)); // Dummy request object
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Request> update(Request request) throws ValidationException {
        validator.validate(request);
        String query = "UPDATE requests SET sender_id = ?, receiver_id = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, request.getSenderId());
            stmt.setLong(2, request.getReceiverId());
            stmt.setLong(3, request.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}