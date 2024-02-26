package at.technikum.apps.mtcg.repository;

import at.technikum.apps.mtcg.entity.Trade;
import at.technikum.apps.mtcg.exception.InternalServerException;
import at.technikum.apps.mtcg.exception.TradeNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradeRepository {
    void createTrade(Trade newTrade) throws InternalServerException;

    void deleteTrade(UUID cardId) throws TradeNotFoundException, InternalServerException;

    List<Trade> getTradesFromOthers(UUID userId) throws InternalServerException;

    boolean tradeExists(UUID cardId) throws InternalServerException;

    Optional<Trade> getTrade(UUID cardId) throws InternalServerException;

    void doTrade(UUID reqCardOwnerUserId, UUID offCardOwnerUserId, UUID requestedCardId, UUID offeredCardId) throws InternalServerException;
}