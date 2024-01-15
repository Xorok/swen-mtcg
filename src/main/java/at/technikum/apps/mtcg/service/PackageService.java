package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.converter.CardDtoToCardConverter;
import at.technikum.apps.mtcg.dto.CardDto;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.*;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.UserRepository;

public class PackageService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final CardDtoToCardConverter cardConverter;

    public PackageService(CardRepository cardRepository, UserRepository userRepository, SessionService sessionService, CardDtoToCardConverter cardConverter) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.cardConverter = cardConverter;
    }

    public void createPackage(CardDto[] newCards) throws InvalidPackageSizeException, InvalidCardException, InternalServerException {
        if (newCards.length == 0 || newCards.length % 5 != 0) {
            throw new InvalidPackageSizeException("Invalid number of cards! Packages contain five cards each!");
        }

        Card[] cards = new Card[newCards.length];
        for (int i = 0; i < newCards.length; i++) {
            Card card = cardConverter.convert(newCards[i]);
            cards[i] = card;
        }

        cardRepository.createAll(cards);
    }

    public synchronized void buyPackage(User user) throws InvalidUserException, NotEnoughCoinsException, NoPackageAvailableException, InternalServerException {
        if (user.getCoins() < 5) {
            throw new NotEnoughCoinsException("User does not have enough money for buying a card package!");
        }

        User updatedUser = cardRepository.buyPackage(user);
        sessionService.updateSessionUser(updatedUser);
    }
}
