package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidUserException;
import at.technikum.apps.mtcg.exception.NoPackageAvailableException;
import at.technikum.apps.mtcg.exception.NotEnoughCoinsException;
import at.technikum.apps.mtcg.util.SQLCloseable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseCardRepository implements CardRepository {

    private final String CREATE_SQL = "INSERT INTO c_card(c_id, c_name, c_damage, c_ct_type, c_ce_element, c_u_owner) VALUES(?, ?, ?, ?, ?, ?)";
    private final String CREATE_SQL_MULTIPLE_VALUES = ", (?, ?, ?, ?, ?, ?)";
    private final String ASSIGN_PACKAGE_SQL = "UPDATE c_card SET c_u_owner=?::uuid " +
            "WHERE c_id IN (SELECT c_id FROM c_card" +
            "               WHERE c_u_owner IS NULL" +
            "               ORDER BY c_id" +
            "               LIMIT 5);";
    private final String REDUCE_COINS_SQL = "UPDATE u_user SET u_coins = u_coins-5 WHERE u_id = ?::uuid;";

    private final Database database;
    private final UserRepository userRepository;

    public DatabaseCardRepository(Database database, UserRepository userRepository) {
        this.database = database;
        this.userRepository = userRepository;
    }

    @Override
    public void createAll(Card[] cards) throws InternalServerException {
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

            // TODO: Add duplicate card exception as in API description
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException("A database error occurred during card creation!");
        }
    }

    public synchronized User buyPackage(User user) throws NotEnoughCoinsException, NoPackageAvailableException, InvalidUserException, InternalServerException {
        Optional<User> userOptional = userRepository.find(user.getUserId());
        if (userOptional.isEmpty()) {
            throw new InvalidUserException("Could not find specified user!");
        }
        User dbUser = userOptional.get();

        if (dbUser.getCoins() < 5) {
            throw new NotEnoughCoinsException("User does not have enough money for buying a card package!");
        }

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

        dbUser.setCoins(dbUser.getCoins() - 5);
        return dbUser;
    }
}
