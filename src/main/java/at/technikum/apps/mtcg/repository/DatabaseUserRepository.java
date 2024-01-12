package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.UserCreationFailedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseUserRepository implements UserRepository {

    private final String CREATE_SQL = "INSERT INTO u_user(u_id, u_username, u_pass_hash, u_coins, u_elo) VALUES(?::uuid, ?, ?, ?, ?);";
    private final String FIND_SQL = "SELECT * FROM u_user WHERE u_username = ?;";

    private final Database database = new Database();

    @Override
    public User create(User user) throws UserCreationFailedException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(CREATE_SQL)
        ) {
            pstmt.setObject(1, user.getUuid());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setInt(4, user.getCoins());
            pstmt.setInt(5, user.getElo());

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UserCreationFailedException(e.getMessage());
        }

        return user;
    }

    @Override
    public Optional<User> find(String username) {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(FIND_SQL)
        ) {
            pstmt.setString(1, username);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return Optional.of(new User(
                        result.getObject("u_id", java.util.UUID.class),
                        result.getString("u_username"),
                        result.getString("u_pass_hash"),
                        result.getInt("u_coins"),
                        result.getInt("u_elo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Handle error
        }

        return Optional.empty();
    }
}
