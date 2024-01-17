package at.technikum.apps.mtcg.converter;

import at.technikum.apps.mtcg.dto.CardInDto;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.exception.InvalidCardException;
import at.technikum.apps.mtcg.util.InputValidator;

import java.util.UUID;

public class CardInDtoToCardConverter implements InConverter<CardInDto, Card> {

    private final InputValidator inputValidator;

    public CardInDtoToCardConverter(InputValidator inputValidator) {
        this.inputValidator = inputValidator;
    }

    @Override
    public Card convert(CardInDto newCard) throws InvalidCardException {
        if (!inputValidator.cardId(newCard.getId())) {
            throw new InvalidCardException("The card id \"" + newCard.getId() + "\" is invalid!");
        }

        String name = newCard.getName();
        if (!inputValidator.cardName(name)) {
            throw new InvalidCardException("The card with the ID \"" + newCard.getId() + "\" has the invalid card name \"" + name + "\"!");
        }

        Double damage = newCard.getDamage();
        if (damage <= 0) {
            throw new InvalidCardException("The card with the ID \"" + newCard.getId() + "\" has the invalid damage " + damage + "!");
        }

        UUID id = UUID.fromString(newCard.getId());
        Card.Element element = Card.Element.NORMAL;
        if (name.contains("Fire")) {
            element = Card.Element.FIRE;
        } else if (name.contains("Water")) {
            element = Card.Element.WATER;
        }
        Card.Type type = newCard.getName().contains("Spell") ? Card.Type.SPELL : Card.Type.MONSTER;

        return new Card(id, name, damage, type, element, null);
    }
}