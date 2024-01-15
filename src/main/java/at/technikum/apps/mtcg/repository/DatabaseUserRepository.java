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

    private final String CREATE_SQL = "INSERT INTO u_user(u_id, u_username, u_pass_hash, u_pass_salt, u_coins, u_elo) VALUES(?::uuid, ?, ?, ?::bytea, ?, ?);";
    private final String FIND_USERNAME_SQL = "SELECT * FROM u_user WHERE u_username = ?;";
    private final String FIND_USERID_SQL = "SELECT * FROM u_user WHERE u_id = ?::uuid;";

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

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return Optional.of(new User(
                        result.getObject("u_id", java.util.UUID.class),
                        result.getString("u_username"),
                        result.getString("u_pass_hash"),
                        result.getBytes("u_pass_salt"),
                        result.getInt("u_coins"),
                        result.getInt("u_elo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while trying to find the supplied user!");
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> find(UUID userId) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(FIND_USERID_SQL)
        ) {
            pstmt.setObject(1, userId);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return Optional.of(new User(
                        result.getObject("u_id", java.util.UUID.class),
                        result.getString("u_username"),
                        result.getString("u_pass_hash"),
                        result.getBytes("u_pass_salt"),
                        result.getInt("u_coins"),
                        result.getInt("u_elo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while trying to find the supplied user!");
        }

        return Optional.empty();
    }
}
