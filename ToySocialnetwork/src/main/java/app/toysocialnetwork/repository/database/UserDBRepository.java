package app.toysocialnetwork.repository.database;

import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.domain.validators.ValidationException;
import app.toysocialnetwork.domain.validators.Validator;
import app.toysocialnetwork.repository.AbstractRepository;
import app.toysocialnetwork.utils.NetworkDB;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements AbstractRepository<Long, User> {
    private final Validator<User> validator;

    /**
     * Constructor that creates a new UserDBRepository
     * @param validator
     * validator must not be null
     */
    public UserDBRepository(Validator<User> validator) {
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
    public Optional<User> findOne(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                User user = new User(firstName, lastName, username, password);
                user.setId(id);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Find all entities
     * @return an {@code Iterable} encapsulating all entities
     */
    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                User user = new User(firstName, lastName, username, password);
                user.setId(id);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Save the entity to the database
     * @param user
     * entity must be not null
     * @return an {@code Optional} - null if the entity was saved,
     * - the entity (id already exists)
     * @throws ValidationException
     * if the entity is not valid
     */
    @Override
    public Optional<User> save(User user) throws ValidationException {
        validator.validate(user);
        String query = "INSERT INTO users (first_name, last_name, username, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getPassword());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    return Optional.of(user);
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
    public Optional<User> delete(Long id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(new User("firstname", "lastname", "username", "password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Update the entity in the database
     * @param user
     * entity must not be null
     * @return an {@code Optional}
     * - null if the entity was updated
     * - otherwise (e.g. id does not exist) returns the entity.
     * @throws ValidationException
     * if the entity is not valid
     */
    @Override
    public Optional<User> update(User user) throws ValidationException {
        validator.validate(user);
        String query = "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getPassword());
            stmt.setLong(5, user.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
