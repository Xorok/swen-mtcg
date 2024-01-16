package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class DatabaseUserRepository implements UserRepository {

    private static final String CREATE_SQL = "INSERT INTO u_user(u_id, u_username, u_pass_hash, u_pass_salt, u_coins, u_elo) VALUES(?::uuid, ?, ?, ?::bytea, ?, ?);";
    private static final String FIND_USERNAME_SQL = "SELECT * FROM u_user WHERE u_username = ?;";
    private static final String FIND_USERID_SQL = "SELECT * FROM u_user WHERE u_id = ?::uuid;";

    private final Database database;

    public DatabaseUserRepository(Database database) {
        this.database = database;
    }

    @Override
    public User create(User user) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(CREATE_SQL)
        ) {
            pstmt.setObject(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setBytes(4, user.getPasswordSalt());
            pstmt.setInt(5, user.getCoins());
            pstmt.setInt(6, user.getElo());

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred during user creation!");
        }

        return user;
    }

    @Override
    public Optional<User> find(String username) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(FIND_USERNAME_SQL)
        ) {
            pstmt.setString(1, username);

            ResultSet resultSet = pstmt.executeQuery();
            return getUserFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while trying to find the supplied user!");
        }
    }

    @Override
    public Optional<User> find(UUID userId) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(FIND_USERID_SQL)
        ) {
            pstmt.setObject(1, userId);

            ResultSet resultSet = pstmt.executeQuery();
            return getUserFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while trying to find the supplied user!");
        }
    }

    private Optional<User> getUserFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(new User(
                    resultSet.getObject("u_id", java.util.UUID.class),
                    resultSet.getString("u_username"),
                    resultSet.getString("u_pass_hash"),
                    resultSet.getBytes("u_pass_salt"),
                    resultSet.getInt("u_coins"),
                    resultSet.getInt("u_elo")
            ));
        }
        return Optional.empty();
    }
}
