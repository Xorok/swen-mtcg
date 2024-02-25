package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.dto.StatOutDto;
import at.technikum.apps.mtcg.entity.Stat;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.StatNotFoundException;
import at.technikum.apps.mtcg.exception.UserNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatabaseUserRepository implements UserRepository {

    private static final String CREATE_USER_SQL = "INSERT INTO u_user(u_id, u_username, u_pass_hash, u_pass_salt, u_coins) VALUES(?::uuid, ?, ?, ?::bytea, ?);";
    private static final String CREATE_STAT_SQL = "INSERT INTO s_stat(s_u_id, s_elo, s_wins, s_losses) VALUES(?::uuid, ?, ?, ?);";
    private static final String FIND_USER_BY_USERNAME_SQL = "SELECT * FROM u_user WHERE u_username = ?;";
    private static final String FIND_USER_BY_USERID_SQL = "SELECT * FROM u_user WHERE u_id = ?::uuid;";
    private static final String UPDATE_USER_SQL = "UPDATE u_user SET u_name = ?, u_bio = ?, u_image = ? WHERE u_id = ?::uuid;";
    private static final String FIND_STAT_SQL = "SELECT * FROM s_stat WHERE s_u_id = ?::uuid;";
    private static final String UPDATE_STAT_SQL = "UPDATE s_stat SET s_elo = ?, s_wins = ?, s_losses = ? WHERE s_u_id = ?::uuid;";
    private static final String GET_SCOREBOARD_SQL = "SELECT s_elo, s_wins, s_losses, u_username, u_name " +
            "FROM s_stat " +
            "INNER JOIN u_user ON s_u_id = u_id " +
            "ORDER BY s_elo DESC, u_username ASC " +
            "LIMIT 10;";
    private final Database database;

    public DatabaseUserRepository(Database database) {
        this.database = database;
    }

    @Override
    public User create(User user, Stat stat) throws InternalServerException {
        try (java.sql.Connection con = database.getConnection()) {
            con.setAutoCommit(false);

            try (
                    PreparedStatement userPstmt = con.prepareStatement(CREATE_USER_SQL);
                    PreparedStatement statPstmt = con.prepareStatement(CREATE_STAT_SQL);
            ) {
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
            } catch (SQLException e) {
                con.rollback();
                con.setAutoCommit(true);
                throw e;
            }

            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred during user creation!");
        }

        return user;
    }

    @Override
    public User update(User user) throws UserNotFoundException, InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(UPDATE_USER_SQL)
        ) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getBio());
            pstmt.setString(3, user.getImage());
            pstmt.setObject(4, user.getUserId());

            int updatedRows = pstmt.executeUpdate();

            if (updatedRows == 1) return user;
            else throw new UserNotFoundException("The user could not be found!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred during user update!");
        }
    }

    @Override
    public Optional<User> find(String username) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(FIND_USER_BY_USERNAME_SQL)
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
                PreparedStatement pstmt = con.prepareStatement(FIND_USER_BY_USERID_SQL)
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

    @Override
    public Stat setStat(UUID userId, Stat stat) throws StatNotFoundException, InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(UPDATE_STAT_SQL)
        ) {
            pstmt.setObject(1, stat.getElo());
            pstmt.setObject(2, stat.getWins());
            pstmt.setObject(3, stat.getLosses());
            pstmt.setObject(4, stat.getUserId());

            int updatedRows = pstmt.executeUpdate();
            if (updatedRows == 1) return stat;
            else throw new StatNotFoundException("The statistics for the user could not be found!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while trying to update the statistics!");
        }
    }

    @Override
    public List<StatOutDto> getScoreboard() throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(GET_SCOREBOARD_SQL)
        ) {
            ResultSet resultSet = pstmt.executeQuery();
            return getScoreboardFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while trying to get the statistics!");
        }
    }

    private Optional<User> getUserFromResultSet(ResultSet resultSet) throws SQLException {
        Optional<User> user = Optional.empty();
        if (resultSet.next()) {
            user = Optional.of(new User(
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
        resultSet.close();
        return user;
    }

    private Optional<Stat> getStatFromResultSet(ResultSet resultSet) throws SQLException {
        Optional<Stat> stat = Optional.empty();
        if (resultSet.next()) {
            stat = Optional.of(new Stat(
                    resultSet.getObject("s_u_id", java.util.UUID.class),
                    resultSet.getInt("s_elo"),
                    resultSet.getInt("s_wins"),
                    resultSet.getInt("s_losses")
            ));
        }
        resultSet.close();
        return stat;
    }

    private List<StatOutDto> getScoreboardFromResultSet(ResultSet resultSet) throws SQLException {
        List<StatOutDto> stats = new ArrayList<>();
        while (resultSet.next()) {
            stats.add(new StatOutDto(
                    resultSet.getString("u_username"),
                    resultSet.getString("u_name"),
                    resultSet.getInt("s_elo"),
                    resultSet.getInt("s_wins"),
                    resultSet.getInt("s_losses")
            ));
        }
        resultSet.close();
        return stats;
    }
}
