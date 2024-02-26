package at.technikum.apps.mtcg.converter;

import at.technikum.apps.mtcg.dto.TradeInDto;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.Trade;
import at.technikum.apps.mtcg.exception.InvalidElementException;
import at.technikum.apps.mtcg.exception.InvalidTradeFormatException;
import at.technikum.apps.mtcg.exception.InvalidTypeException;
import at.technikum.apps.mtcg.util.InputValidator;

import java.util.UUID;

public class TradeInDtoToTradeConverter implements InConverter<TradeInDto, Trade> {

    private final InputValidator inputValidator;

    public TradeInDtoToTradeConverter(InputValidator inputValidator) {
        this.inputValidator = inputValidator;
    }

    @Override
    public Trade convert(TradeInDto newTrade) throws InvalidTradeFormatException {
        if (!inputValidator.uuid(newTrade.getOfferedCardId())) {
            throw new InvalidTradeFormatException("The card id \"" + newTrade.getOfferedCardId() + "\" is invalid!");
        }

        Card.Type cardType;
        try {
            cardType = Card.Type.mapFrom(newTrade.getRequiredType());
        } catch (InvalidTypeException e) {
            throw new InvalidTradeFormatException("The card type \"" + newTrade.getRequiredType() + "\" is invalid!");
        }

        boolean invalidElement = false;
        Card.Element cardElement;
        try {
            cardElement = Card.Element.mapFrom(newTrade.getRequiredElement());
        } catch (InvalidElementException e) {
            invalidElement = true;
            cardElement = null;
        }

        boolean invalidMinDamage = false;
        Double damage = newTrade.getRequiredMinDamage();
        if (damage == null || damage <= 0) {
            invalidMinDamage = true;
            damage = null;
        }

        if (invalidElement && invalidMinDamage) {
            throw new InvalidTradeFormatException("A trade must request either a valid element or a valid minimum damage!");
        }

        UUID id = UUID.fromString(newTrade.getOfferedCardId());
        return new Trade(id, cardType, cardElement, damage);
    }
}