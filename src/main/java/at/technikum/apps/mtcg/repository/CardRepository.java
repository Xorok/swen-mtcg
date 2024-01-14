package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.exception.CardCreationFailedException;

public interface CardRepository {
    void createAll(Card[] cards) throws CardCreationFailedException; // TODO: Replace with own exception
}
