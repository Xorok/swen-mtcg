package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.converter.CardDtoToCardConverter;
import at.technikum.apps.mtcg.dto.CardInDto;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.exception.DuplicateCardException;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.InvalidCardException;
import at.technikum.apps.mtcg.exception.WrongNumberOfCardsException;
import at.technikum.apps.mtcg.repository.CardRepository;

public class PackageService {

    private final CardRepository cardRepository;
    private final CardDtoToCardConverter cardConverter;

    public PackageService(CardRepository cardRepository, CardDtoToCardConverter cardConverter) {
        this.cardRepository = cardRepository;
        this.cardConverter = cardConverter;
    }

    public void createPackage(CardInDto[] newCards) throws WrongNumberOfCardsException, InvalidCardException, DuplicateCardException, InternalServerException {
        if (newCards.length == 0 || newCards.length % 5 != 0) {
            throw new WrongNumberOfCardsException("Invalid number of cards! Packages contain five cards each!");
        }

        Card[] cards = new Card[newCards.length];
        for (int i = 0; i < newCards.length; i++) {
            Card card = cardConverter.convert(newCards[i]);
            cards[i] = card;
        }

        cardRepository.createAll(cards);
    }
}
