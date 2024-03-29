package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.DuplicateCardException;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.NoPackageAvailableException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository {
    void createAll(Card[] cards) throws DuplicateCardException, InternalServerException;

    User buyPackage(User user) throws NoPackageAvailableException, InternalServerException;

    Optional<Card> getCard(UUID cardId) throws InternalServerException;

    List<Card> getCards(User user) throws InternalServerException;

    List<Card> getDeck(User user) throws InternalServerException;

    void setDeck(User user, UUID[] cardIds) throws InternalServerException;

    boolean userOwnsCards(User user, UUID[] cardIds) throws InternalServerException;
}