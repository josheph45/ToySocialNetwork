package app.toysocialnetwork.repository.database;

import app.toysocialnetwork.domain.Message;
import app.toysocialnetwork.domain.validators.ValidationException;
import app.toysocialnetwork.domain.validators.Validator;
import app.toysocialnetwork.repository.AbstractRepository;
import app.toysocialnetwork.utils.NetworkDB;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class MessageDBRepository implements AbstractRepository<Long, Message> {
    private final Validator<Message> validator;

    /**
     * Constructor that creates a new MessageDBRepository
     * @param validator
     * validator must not be null
     */
    public MessageDBRepository(Validator<Message> validator) {
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
    public Optional<Message> findOne(Long id) {
        String query = "SELECT * FROM messages WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long from = rs.getLong("from");
                Long to = rs.getLong("to");
                String text = rs.getString("text");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                Message message = new Message(from, to, text, date);
                message.setId(id);
                return Optional.of(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Find all messages
     *  @return an {@code Iterable} containing all entities
     */
    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                Long from = rs.getLong("from");
                Long to = rs.getLong("to");
                String text = rs.getString("text");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                Message message = new Message(from, to, text, date);
                message.setId(id);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Save the entity to the database
     * @param message
     * entity must not be null
     * @return an {@code Optional}
     * - null if the entity was saved
     * - the entity (id already exists)
     * @throws ValidationException
     * if the entity is not valid
     * @throws IllegalArgumentException
     * if the given entity is null
     */
    @Override
    public Optional<Message> save(Message message) throws ValidationException {
        validator.validate(message);
        String query = "INSERT INTO messages (\"from\", \"to\", text, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, message.getFrom());
            stmt.setLong(2, message.getTo());
            stmt.setString(3, message.getText());
            stmt.setTimestamp(4, Timestamp.valueOf(message.getDate()));
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getLong(1));
                    return Optional.of(message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Delete the entity with the given id
     * @param id
     * id must not be null
     * @return an {@code Optional} encapsulating the deleted entity
     */
    @Override
    public Optional<Message> delete(Long id) {
        Optional<Message> optional = findOne(id);
        String query = "DELETE FROM messages WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return optional;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }
}
