package app.toysocialnetwork.repository.database;

import app.toysocialnetwork.domain.Request;
import app.toysocialnetwork.domain.validators.ValidationException;
import app.toysocialnetwork.domain.validators.Validator;
import app.toysocialnetwork.repository.AbstractRepository;
import app.toysocialnetwork.utils.NetworkDB;

import java.sql.*;
import java.util.*;

public class RequestDBRepository implements AbstractRepository<Long, Request> {
    private final Validator<Request> validator;

    /**
     * Constructor that creates a new RequestDBRepository
     * @param validator
     * validator must not be null
     */
    public RequestDBRepository(Validator<Request> validator) {
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

    /**
     * Find all entities
     * @return an {@code Iterable} containing all entities
     */
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

    /**
     * Save the entity to the database
     * @param request
     * entity must be not null
     * @return an {@code Optional}
     * - null if the entity was saved,
     * - the entity (id already exists)
     * @throws ValidationException
     * if the entity is not valid
     * @throws IllegalArgumentException
     * if the given entity is null.
     */
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

    /**
     * Remove the entity with the specified id
     * @param id
     * id must be not null
     * @return an {@code Optional}
     * - null if there is no entity with the given id,
     * - the removed entity, otherwise
     */
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

    /**
     * Update the entity in the database
     * @param request
     * entity must not be null
     * @return an {@code Optional}
     * - null if the entity was updated
     * - otherwise (e.g. id does not exist) returns the entity.
     * @throws IllegalArgumentException
     * if the given entity is null.
     * @throws ValidationException
     * if the entity is not valid.
     */
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