package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.DuplicateCardException;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NoPackageAvailableException;
import at.technikum.apps.mtcg.util.SQLCloseable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseCardRepository implements CardRepository {

    private static final String INSERT_CARD_SQL = "INSERT INTO c_card" +
            "(c_id, c_name, c_damage, c_ct_type, c_ce_element, c_u_owner) " +
            "VALUES(?, ?, ?, ?, ?, ?)";
    private static final String INSERT_CARD_SQL_MULTIPLE_VALUES = ", (?, ?, ?, ?, ?, ?)";
    private static final String ASSIGN_PACKAGE_SQL = "UPDATE c_card SET c_u_owner=?::uuid " +
            "WHERE c_id IN (SELECT c_id FROM c_card" +
            "               WHERE c_u_owner IS NULL" +
            "               ORDER BY c_no" +
            "               LIMIT 5);";
    private static final String REDUCE_COINS_SQL = "UPDATE u_user SET u_coins = u_coins-5 WHERE u_id = ?::uuid;";
    private static final String SELECT_CARDS_SQL = "SELECT c_id, c_name, c_damage, c_ct_type, c_ce_element, c_u_owner " +
            "FROM c_card " +
            "WHERE c_u_owner = ?::uuid;";
    private static final String INSERT_DECK_SQL = "INSERT INTO d_deck(d_u_owner, d_c_card) VALUES(?::uuid, ?::uuid)";
    private static final String INSERT_DECK_SQL_MULTIPLE_VALUES = ", (?::uuid, ?::uuid)";
    private static final String SELECT_DECK_SQL = "SELECT c_id, c_name, c_damage, c_ct_type, c_ce_element, c_u_owner " +
            "FROM c_card " +
            "INNER JOIN d_deck ON c_id = d_c_card " +
            "WHERE d_u_owner = ?::uuid;";
    private static final String USER_OWNS_CARDS_SQL = "SELECT COUNT(*) FROM c_card " +
            "WHERE c_u_owner = ?::uuid " +
            "AND c_id in (?::uuid, ?::uuid, ?::uuid, ?::uuid);";

    private final Database database;

    public DatabaseCardRepository(Database database) {
        this.database = database;
    }

    @Override
    public void createAll(Card[] cards) throws DuplicateCardException, InternalServerException {
        String insertsSQL = INSERT_CARD_SQL +
                INSERT_CARD_SQL_MULTIPLE_VALUES.repeat(Math.max(0, cards.length - 1));
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(insertsSQL)
        ) {
            // TODO: Check insert limit and separate in batches accordingly
            for (int i = 0; i < cards.length; i++) {
                pstmt.setObject(i * 6 + 1, cards[i].getId());
                pstmt.setString(i * 6 + 2, cards[i].getName());
                pstmt.setDouble(i * 6 + 3, cards[i].getDamage());
                pstmt.setString(i * 6 + 4, cards[i].getType().getValue());
                pstmt.setString(i * 6 + 5, cards[i].getElement().getValue());
                pstmt.setObject(i * 6 + 6, cards[i].getOwner());
            }

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            if ((e.getSQLState().equals("23505"))) { // Postgres SQL: 23505 unique_violation
                throw new DuplicateCardException("At least one card in the packages already exists!");
            }
            throw new InternalServerException("A database error occurred during card creation!");
        }
    }

    @Override
    public synchronized User buyPackage(User user) throws NoPackageAvailableException, InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement packagePstmt = con.prepareStatement(ASSIGN_PACKAGE_SQL);
                PreparedStatement coinsPstmt = con.prepareStatement(REDUCE_COINS_SQL);
                SQLCloseable finish = con::rollback; // Always rollback at the end
        ) {
            con.setAutoCommit(false);
            packagePstmt.setObject(1, user.getUserId());
            coinsPstmt.setObject(1, user.getUserId());

            int updatedRows = packagePstmt.executeUpdate();
            if (updatedRows < 5) {
                con.rollback();
                throw new NoPackageAvailableException("No card package available for buying!");
            }
            coinsPstmt.execute();
            con.commit();
            // TODO: Do we have to turn off autocommit again for future DB calls elsewhere? --> Interferes with needed SQLCloseable finish = con::rollback
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred during package acquisition!");
        }

        user.setCoins(user.getCoins() - 5);
        return user;
    }

    @Override
    public List<Card> getCards(User user) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(SELECT_CARDS_SQL);
        ) {
            pstmt.setObject(1, user.getUserId());
            ResultSet resultSet = pstmt.executeQuery();

            return getCardsFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while fetching the cards!");
        }
    }

    @Override
    public List<Card> getDeck(User user) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(SELECT_DECK_SQL);
        ) {
            pstmt.setObject(1, user.getUserId());
            ResultSet resultSet = pstmt.executeQuery();

            return getCardsFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while fetching the deck!");
        }
    }

    @Override
    public void setDeck(User user, UUID[] cardIds) throws InternalServerException {
        String insertsSQL = INSERT_DECK_SQL +
                INSERT_DECK_SQL_MULTIPLE_VALUES.repeat(Math.max(0, cardIds.length - 1));
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(insertsSQL)
        ) {
            // TODO: Check insert limit and separate in batches accordingly
            for (int i = 0; i < cardIds.length; i++) {
                pstmt.setObject(i * 2 + 1, user.getUserId());
                pstmt.setObject(i * 2 + 2, cardIds[i]);
            }

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while setting the deck cards!");
        }
    }

    @Override
    public boolean userOwnsCards(User user, UUID[] cardIds) throws InternalServerException {
        try (
                Connection con = database.getConnection();
                PreparedStatement pstmt = con.prepareStatement(USER_OWNS_CARDS_SQL);
        ) {
            pstmt.setObject(1, user.getUserId());
            pstmt.setObject(2, cardIds[0]);
            pstmt.setObject(3, cardIds[1]);
            pstmt.setObject(4, cardIds[2]);
            pstmt.setObject(5, cardIds[3]);
            ResultSet resultSet = pstmt.executeQuery();

            resultSet.next();
            int count = resultSet.getInt(1);

            return count == cardIds.length;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred while checking the submitted cards!");
        }
    }

    private List<Card> getCardsFromResultSet(ResultSet resultSet) throws SQLException {
        List<Card> cards = new ArrayList<>();
        while (resultSet.next()) {
            cards.add(new Card(
                    resultSet.getObject("c_id", UUID.class),
                    resultSet.getString("c_name"),
                    resultSet.getDouble("c_damage"),
                    Card.Type.mapFrom(resultSet.getString("c_ct_type")),
                    Card.Element.mapFrom(resultSet.getString("c_ce_element")),
                    resultSet.getObject("c_u_owner", UUID.class)
            ));
        }
        return cards;
    }
}
