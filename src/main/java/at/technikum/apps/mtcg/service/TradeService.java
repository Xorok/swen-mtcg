package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.converter.TradeInDtoToTradeConverter;
import at.technikum.apps.mtcg.dto.TradeInDto;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.Trade;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.exception.*;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.TradeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TradeService {

    private final TradeRepository tradeRepository;
    private final CardRepository cardRepository;
    private final TradeInDtoToTradeConverter tradeConverter;

    public TradeService(TradeRepository tradeRepository, CardRepository cardRepository, TradeInDtoToTradeConverter tradeConverter) {
        this.tradeRepository = tradeRepository;
        this.cardRepository = cardRepository;
        this.tradeConverter = tradeConverter;
    }

    public List<Trade> getTradesForUser(User user) throws InternalServerException {
        return tradeRepository.getTradesFromOthers(user.getUserId());
    }

    public void createTrade(User user, TradeInDto newTradeDto) throws InvalidTradeFormatException, InvalidTradeRequestException, TradeAlreadyExistsException, InternalServerException {
        Trade newTrade;
        try {
            newTrade = tradeConverter.convert(newTradeDto);
        } catch (InvalidTradeFormatException e) {
            throw new InvalidTradeFormatException(e.getMessage());
        }

        if (!userOwnsCard(user, newTrade.getOfferedCardId())) {
            throw new InvalidTradeRequestException("The offered card is not owned by the user!");
        }

        if (cardIsInDeck(user, newTrade.getOfferedCardId())) {
            throw new InvalidTradeRequestException("The card cannot be offered for trading while it is in the deck!");
        }

        if (tradeRepository.tradeExists(newTrade.getOfferedCardId())) {
            throw new TradeAlreadyExistsException("The card is already being offered for trading!");
        }

        tradeRepository.createTrade(newTrade);
    }

    public void deleteTrade(User user, UUID cardId) throws TradeNotFoundException, InvalidTradeRequestException, InternalServerException {
        if (!tradeRepository.tradeExists(cardId)) {
            throw new TradeNotFoundException("The card is not being offered for trading!");
        }

        if (!userOwnsCard(user, cardId)) {
            throw new InvalidTradeRequestException("The card is not owned by the user!");
        }

        try {
            tradeRepository.deleteTrade(cardId);
        } catch (TradeNotFoundException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public synchronized void doTrade(User user, UUID requestedCardId, UUID offeredCardId) throws TradeNotFoundException, InvalidTradeRequestException, InternalServerException {
        Optional<Card> requestedCardOpt = cardRepository.getCard(requestedCardId);
        Optional<Card> offeredCardOpt = cardRepository.getCard(offeredCardId);
        Optional<Trade> tradeOpt = tradeRepository.getTrade(requestedCardId);

        if (requestedCardOpt.isEmpty()) {
            throw new TradeNotFoundException("The requested card does not exist!");
        }

        if (requestedCardOpt.get().getOwner().equals(user.getUserId())) {
            throw new InvalidTradeRequestException("The requested card is already owned by the user!");
        }

        if (tradeOpt.isEmpty()) {
            throw new TradeNotFoundException("The requested card is not being offered for trading!");
        }

        if (offeredCardOpt.isEmpty() || !offeredCardOpt.get().getOwner().equals(user.getUserId())) {
            throw new InvalidTradeRequestException("The offered card does not exist or is not owned by the user!");
        }

        if (tradeRepository.tradeExists(offeredCardId)) {
            throw new InvalidTradeRequestException("The offered card is currently offered for trading itself!");
        }

        if (cardIsInDeck(user, offeredCardId)) {
            throw new InvalidTradeRequestException("The card cannot be offered for trading while it is in the deck!");
        }

        Trade trade = tradeOpt.get();
        Card offeredCard = offeredCardOpt.get();
        Card requestedCard = requestedCardOpt.get();

        if (trade.getRequestedType() != offeredCard.getType()) {
            throw new InvalidTradeRequestException("The offered card does not have the requested type!");
        }

        if (trade.getRequestedElement() != null && trade.getRequestedElement() != offeredCard.getElement()) {
            throw new InvalidTradeRequestException("The offered card does not have the requested element!");
        }

        if (trade.getRequestedMinDamage() != null && trade.getRequestedMinDamage() > offeredCard.getDamage()) {
            throw new InvalidTradeRequestException("The offered card does not have the requested minimum damage!");
        }

        tradeRepository.doTrade(requestedCard.getOwner(), offeredCard.getOwner(), requestedCardId, offeredCardId);
    }

    private boolean userOwnsCard(User user, UUID cardId) throws InternalServerException {
        return cardRepository.userOwnsCards(user, new UUID[]{cardId});
    }

    private boolean cardIsInDeck(User user, UUID cardId) throws InternalServerException {
        return cardRepository.getDeck(user).stream()
                .anyMatch(o -> o.getId().equals(cardId));
    }
}
