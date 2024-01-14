package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.dto.CardCreation;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.exception.CardCreationFailedException;
import at.technikum.apps.mtcg.exception.InvalidPackageSizeException;
import at.technikum.apps.mtcg.repository.CardRepository;

import java.util.Arrays;

public class PackageService {

    private final CardRepository cardRepository;

    public PackageService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void createPackage(CardCreation[] newCards) throws InvalidPackageSizeException, CardCreationFailedException {
        if (newCards.length == 0 || newCards.length % 5 != 0) {
            throw new InvalidPackageSizeException("Package size must be >0 and dividable by 5!");
        }

        Card[] cards = Arrays.stream(newCards)
                .map(Card::new)
                .toArray(Card[]::new);

        cardRepository.createAll(cards);
    }
}
