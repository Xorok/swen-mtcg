package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.Trade;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidElementException;
import at.technikum.apps.mtcg.exception.InvalidTypeException;
import at.technikum.apps.mtcg.exception.TradeNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatabaseTradeRepository implements TradeRepository {
    private static final String INSERT_TRADE_SQL = "INSERT INTO t_trade" +
            "(t_c_offered_card, t_ct_requested_type, t_ct_requested_element, t_requested_min_damage) " +
            "VALUES(?::uuid, ?, ?, ?);";

    private static final String DELETE_TRADE_SQL = "DELETE FROM t_trade " +
            "WHERE t_c_offered_card = ?::uuid;";

    private static final String FIND_TRADES_SQL = "SELECT " +
            "t_c_offered_card, t_ct_requested_type, t_ct_requested_element, t_requested_min_damage " +
            "FROM t_trade " +
            "INNER JOIN c_card ON t_c_offered_card = c_id " +
            "WHERE c_u_owner != ?::uuid;";

    private static final String TRADE_EXISTS_SQL = "SELECT 1 FROM t_trade " +
            "WHERE t_c_offered_card = ?::uuid;";

    private static final String GET_TRADE_SQL = "SELECT " +
            "t_c_offered_card, t_ct_requested_type, t_ct_requested_element, t_requested_min_damage " +
            "FROM t_trade " +
            "WHERE t_c_offered_card = ?::uuid;";

    private static final String UPDATE_CARD_OWNER_SQL = "UPDATE c_card SET c_u_owner=?::uuid " +
            "WHERE c_id = ?::uuid;";

    private final Database database;

    public DatabaseTradeRepository(Database database) {
        this.database = database;
    }

    @Override
    public void createTrade(Trade newTrade) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(INSERT_TRADE_SQL);
        ) {
            Card.Element element = newTrade.getRequestedElement();

            pstmt.setObject(1, newTrade.getOfferedCardId());
            pstmt.setObject(2, newTrade.getRequestedType().getValue());
            pstmt.setObject(3, element != null ? element.getValue() : null);
            pstmt.setObject(4, newTrade.getRequestedMinDamage());

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while creating the trade!");
        }
    }

    @Override
    public void deleteTrade(UUID cardId) throws TradeNotFoundException, InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(DELETE_TRADE_SQL);
        ) {
            pstmt.setObject(1, cardId);

            int deletedRows = pstmt.executeUpdate();
            if (deletedRows != 1) throw new TradeNotFoundException("The specified trade could not be deleted!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while deleting the trade!");
        }
    }

    @Override
    public List<Trade> getTradesFromOthers(UUID userId) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(FIND_TRADES_SQL);
        ) {
            pstmt.setObject(1, userId);

            ResultSet resultSet = pstmt.executeQuery();
            return getTradesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while fetching the trades!");
        } catch (InvalidTypeException | InvalidElementException e) {
            e.printStackTrace();
            throw new InternalServerException("A data mapping error occurred while fetching the trades!");
        }
    }

    @Override
    public boolean tradeExists(UUID cardId) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(TRADE_EXISTS_SQL);
        ) {
            pstmt.setObject(1, cardId);

            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while checking if the trade exists!");
        }
    }

    @Override
    public Optional<Trade> getTrade(UUID cardId) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(GET_TRADE_SQL);
        ) {
            pstmt.setObject(1, cardId);

            ResultSet resultSet = pstmt.executeQuery();
            List<Trade> trades = getTradesFromResultSet(resultSet);
            return trades.isEmpty() ? Optional.empty() : Optional.of(trades.get(0));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while fetching the trade!");
        } catch (InvalidTypeException | InvalidElementException e) {
            e.printStackTrace();
            throw new InternalServerException("A data mapping error occurred while fetching the trade!");
        }
    }

    @Override
    public synchronized void doTrade(UUID reqCardOwnerUserId, UUID offCardOwnerUserId, UUID requestedCardId, UUID offeredCardId) throws InternalServerException {
        try (Connection con = database.getConnection()) {
            con.setAutoCommit(false);

            try (
                    PreparedStatement updateCardOwner1Pstmt = con.prepareStatement(UPDATE_CARD_OWNER_SQL);
                    PreparedStatement updateCardOwner2Pstmt = con.prepareStatement(UPDATE_CARD_OWNER_SQL);
                    PreparedStatement deleteTradePstmt = con.prepareStatement(DELETE_TRADE_SQL);
            ) {
                updateCardOwner1Pstmt.setObject(1, offCardOwnerUserId);
                updateCardOwner1Pstmt.setObject(2, requestedCardId);
                updateCardOwner2Pstmt.setObject(1, reqCardOwnerUserId);
                updateCardOwner2Pstmt.setObject(2, offeredCardId);
                deleteTradePstmt.setObject(1, requestedCardId);

                int updatedRows = updateCardOwner1Pstmt.executeUpdate();
                if (updatedRows != 1) {
                    throw new InternalServerException("A database error occurred during trading!");
                }
                updatedRows = updateCardOwner2Pstmt.executeUpdate();
                if (updatedRows != 1) {
                    throw new InternalServerException("A database error occurred during trading!");
                }
                updatedRows = deleteTradePstmt.executeUpdate();
                if (updatedRows != 1) {
                    throw new InternalServerException("A database error occurred during trading!");
                }
            } catch (Exception e) {
                con.rollback();
                con.setAutoCommit(true);
                throw e;
            }

            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred during trading!");
        }
    }

    private List<Trade> getTradesFromResultSet(ResultSet resultSet) throws SQLException, InvalidTypeException, InvalidElementException {
        List<Trade> trades = new ArrayList<>();
        while (resultSet.next()) {
            String elementString = resultSet.getString("t_ct_requested_element");

            trades.add(new Trade(
                    resultSet.getObject("t_c_offered_card", UUID.class),
                    Card.Type.mapFrom(resultSet.getString("t_ct_requested_type")),
                    elementString != null ? Card.Element.mapFrom(elementString) : null,
                    resultSet.getDouble("t_requested_min_damage")
            ));
        }
        resultSet.close();
        return trades;
    }
}
