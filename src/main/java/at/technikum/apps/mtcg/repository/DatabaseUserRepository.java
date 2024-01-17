package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.entity.Stat;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.util.SQLCloseable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class DatabaseUserRepository implements UserRepository {

    private static final String CREATE_USER_SQL = "INSERT INTO u_user(u_id, u_username, u_pass_hash, u_pass_salt, u_coins) VALUES(?::uuid, ?, ?, ?::bytea, ?);";
    private static final String CREATE_STAT_SQL = "INSERT INTO s_stat(s_u_id, s_elo, s_wins, s_losses) VALUES(?::uuid, ?, ?, ?);";
    private static final String FIND_USERNAME_SQL = "SELECT * FROM u_user WHERE u_username = ?;";
    private static final String FIND_USERID_SQL = "SELECT * FROM u_user WHERE u_id = ?::uuid;";
    private static final String UPDATE_USER_SQL = "UPDATE u_user SET u_name = ?, u_bio = ?, u_image = ? WHERE u_id = ?::uuid;";
    private static final String FIND_STAT_SQL = "SELECT * FROM s_stat WHERE s_u_id = ?::uuid;";

    private final Database database;

    public DatabaseUserRepository(Database database) {
        this.database = database;
    }

    @Override
    public User create(User user, Stat stat) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement userPstmt = con.prepareStatement(CREATE_USER_SQL);
                PreparedStatement statPstmt = con.prepareStatement(CREATE_STAT_SQL);
                SQLCloseable finish = con::rollback; // Always rollback at the end
        ) {
            con.setAutoCommit(false);

            userPstmt.setObject(1, user.getUserId());
            userPstmt.setString(2, user.getUsername());
            userPstmt.setString(3, user.getPasswordHash());
            userPstmt.setBytes(4, user.getPasswordSalt());
            userPstmt.setInt(5, user.getCoins());

            statPstmt.setObject(1, user.getUserId());
            statPstmt.setInt(2, stat.getElo());
            statPstmt.setInt(3, stat.getWins());
            statPstmt.setInt(4, stat.getLosses());

            userPstmt.execute();
            statPstmt.execute();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred during user creation!");
        }

        return user;
    }

    @Override
    public User update(User user) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(UPDATE_USER_SQL)
        ) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getBio());
            pstmt.setString(3, user.getImage());
            pstmt.setObject(4, user.getUserId());

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred during user update!");
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

    @Override
    public Optional<Stat> getStat(UUID userId) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(FIND_STAT_SQL)
        ) {
            pstmt.setObject(1, userId);

            ResultSet resultSet = pstmt.executeQuery();
            return getStatFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while trying to get the statistics!");
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
                    resultSet.getString("u_name"),
                    resultSet.getString("u_bio"),
                    resultSet.getString("u_image")
            ));
        }
        return Optional.empty();
    }

    private Optional<Stat> getStatFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(new Stat(
                    resultSet.getObject("s_u_id", java.util.UUID.class),
                    resultSet.getInt("s_elo"),
                    resultSet.getInt("s_wins"),
                    resultSet.getInt("s_losses")
            ));
        }
        return Optional.empty();
    }
}
