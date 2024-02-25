package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidCardException;
import at.technikum.apps.mtcg.exception.WrongNumberOfCardsException;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.util.InputValidator;

import java.util.List;
import java.util.UUID;

public class DeckService {

    private final CardRepository cardRepository;
    private final InputValidator inputValidator;

    public DeckService(CardRepository cardRepository, InputValidator inputValidator) {
        this.cardRepository = cardRepository;
        this.inputValidator = inputValidator;
    }

    public List<Card> getDeck(User user) throws InternalServerException {
        return cardRepository.getDeck(user);
    }

    public void setDeck(User user, String[] cardIds) throws WrongNumberOfCardsException, InvalidCardException, InternalServerException {
        if (cardIds.length != 4) {
            throw new WrongNumberOfCardsException("The provided deck did not include the required amount of cards!");
        }

        UUID[] cardUuids = new UUID[cardIds.length];
        for (int i = 0; i < cardIds.length; i++) {
            if (!inputValidator.uuid(cardIds[i])) {
                throw new InvalidCardException("The card \"" + cardIds[i] + "\" is invalid!");
            }
            cardUuids[i] = UUID.fromString(cardIds[i]);
        }

        if (cardRepository.userOwnsCards(user, cardUuids)) {
            cardRepository.setDeck(user, cardUuids);
        } else {
            throw new InvalidCardException("At least one of the provided cards does not belong to the user or is not available!");
        }
    }
}
