package at.technikum.apps.mtcg;

import at.technikum.apps.mtcg.controller.*;
import at.technikum.apps.mtcg.converter.CardInDtoToCardConverter;
import at.technikum.apps.mtcg.converter.StatToStatOutDtoConverter;
import at.technikum.apps.mtcg.converter.TradeInDtoToTradeConverter;
import at.technikum.apps.mtcg.converter.UserToUserOutDtoConverter;
import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.repository.*;
import at.technikum.apps.mtcg.service.*;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.apps.mtcg.util.PasswordHashUtils;
import at.technikum.apps.mtcg.util.RandomUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Injector {

    /*
        A central place to create all classes.
        Dependency Injection via constructor injection.
     */
    public List<Controller> createController() {
        List<Controller> controllerList = new ArrayList<>();

        // General
        //  * DB Access
        Database database = new Database();
        //  * Repositories
        UserRepository userRepository = new DatabaseUserRepository(database);
        CardRepository cardRepository = new DatabaseCardRepository(database);
        TradeRepository tradeRepository = new DatabaseTradeRepository(database);
        //  * Utilities
        InputValidator inputValidator = new InputValidator();
        PasswordHashUtils passwordHashUtils = new PasswordHashUtils();
        HttpUtils httpUtils = new HttpUtils();
        RandomUtils randomUtils = new RandomUtils();
        ObjectMapper objectMapper = new ObjectMapper();

        // Session: /sessions
        SessionService sessionService = new SessionService(userRepository, inputValidator, passwordHashUtils);
        controllerList.add(new SessionController(sessionService, objectMapper));

        // User: /users
        UserToUserOutDtoConverter userConverter = new UserToUserOutDtoConverter();
        UserService userService = new UserService(sessionService, userRepository, inputValidator, passwordHashUtils, userConverter);
        controllerList.add(new UserController(userService, sessionService, inputValidator, httpUtils, objectMapper));

        // Package: /packages
        CardInDtoToCardConverter cardConverter = new CardInDtoToCardConverter(inputValidator);
        PackageService packageService = new PackageService(cardRepository, cardConverter);
        controllerList.add(new PackageController(packageService, sessionService, userService, inputValidator, httpUtils, objectMapper));

        // Transaction: /transactions
        TransactionService transactionService = new TransactionService(cardRepository, sessionService);
        controllerList.add(new TransactionController(transactionService, sessionService, inputValidator, httpUtils));

        // Card: /cards
        CardService cardService = new CardService(cardRepository);
        controllerList.add(new CardController(cardService, sessionService, inputValidator, httpUtils));

        // Deck: /deck
        DeckService deckService = new DeckService(cardRepository, tradeRepository, inputValidator);
        controllerList.add(new DeckController(deckService, sessionService, inputValidator, httpUtils, objectMapper));

        // Stat: /stats
        StatToStatOutDtoConverter statConverter = new StatToStatOutDtoConverter();
        StatService statService = new StatService(userRepository, statConverter);
        controllerList.add(new StatController(statService, sessionService, inputValidator, httpUtils));

        // Scoreboard: /scoreboard
        ScoreboardService scoreboardService = new ScoreboardService(userRepository);
        controllerList.add(new ScoreboardController(scoreboardService, sessionService, inputValidator, httpUtils));

        // Battle: /battles
        BattleService battleService = new BattleService(userRepository, cardRepository, randomUtils);
        controllerList.add(new BattleController(battleService, sessionService, inputValidator, httpUtils));

        // Battle: /trades
        TradeInDtoToTradeConverter tradeConverter = new TradeInDtoToTradeConverter(inputValidator);
        TradeService tradeService = new TradeService(tradeRepository, cardRepository, tradeConverter);
        controllerList.add(new TradeController(tradeService, sessionService, objectMapper, inputValidator, httpUtils));

        return controllerList;
    }
}
