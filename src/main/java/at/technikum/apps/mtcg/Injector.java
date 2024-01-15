package at.technikum.apps.mtcg;

import at.technikum.apps.mtcg.controller.*;
import at.technikum.apps.mtcg.converter.CardDtoToCardConverter;
import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.DatabaseCardRepository;
import at.technikum.apps.mtcg.repository.DatabaseUserRepository;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.service.*;
import at.technikum.apps.mtcg.util.HttpUtils;
import at.technikum.apps.mtcg.util.InputValidator;
import at.technikum.apps.mtcg.util.PasswordHashUtils;

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
        // - DB Access
        Database database = new Database();
        // - Repositories
        UserRepository userRepository = new DatabaseUserRepository(database);
        CardRepository cardRepository = new DatabaseCardRepository(database, userRepository);
        // - Utilities
        InputValidator inputValidator = new InputValidator();
        PasswordHashUtils passwordHashUtils = new PasswordHashUtils();
        HttpUtils httpUtils = new HttpUtils();

        // Session: /sessions
        SessionService sessionService = new SessionService(userRepository, inputValidator, passwordHashUtils);
        controllerList.add(new SessionController(sessionService));

        // Users: /users
        UserService userService = new UserService(userRepository, inputValidator, passwordHashUtils);
        controllerList.add(new UserController(userService, inputValidator));

        // Packages: /packages
        CardDtoToCardConverter cardConverter = new CardDtoToCardConverter(inputValidator);
        PackageService packageService = new PackageService(cardRepository, cardConverter);
        controllerList.add(new PackageController(packageService, sessionService, userService, inputValidator, httpUtils));

        // Transactions: /transactions
        TransactionService transactionService = new TransactionService(cardRepository, sessionService);
        controllerList.add(new TransactionController(transactionService, sessionService, inputValidator, httpUtils));

        // Cards: /cards
        CardService cardService = new CardService(cardRepository);
        controllerList.add(new CardController(cardService, sessionService, inputValidator, httpUtils));

        return controllerList;
    }
}
