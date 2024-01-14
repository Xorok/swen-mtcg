package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.exception.CardCreationFailedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseCardRepository implements CardRepository {

    private final String CREATE_SQL = "INSERT INTO c_card(c_id, c_name, c_damage, c_ct_type, c_ce_element, c_u_owner) VALUES(?, ?, ?, ?, ?, ?)";
    private final String CREATE_SQL_MULTIPLE_VALUES = ", (?, ?, ?, ?, ?, ?)";
    private final String FIND_SQL = "SELECT * FROM u_user WHERE u_username = ?;";

    private final Database database;

    public DatabaseCardRepository(Database database) {
        this.database = database;
    }

    @Override
    public void createAll(Card[] cards) throws CardCreationFailedException {
        String insertsSQL = CREATE_SQL +
                CREATE_SQL_MULTIPLE_VALUES.repeat(Math.max(0, cards.length - 1));
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
            // TODO: Add duplicate card exception as in API description
            throw new CardCreationFailedException(e.getMessage());
        }
    }
}
