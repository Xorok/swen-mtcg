package at.technikum.apps.mtcg;

import at.technikum.apps.mtcg.controller.Controller;
import at.technikum.apps.mtcg.controller.PackageController;
import at.technikum.apps.mtcg.controller.SessionController;
import at.technikum.apps.mtcg.controller.UserController;
import at.technikum.apps.mtcg.data.Database;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.DatabaseCardRepository;
import at.technikum.apps.mtcg.repository.DatabaseUserRepository;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.service.PackageService;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.service.UserService;

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
        Database database = new Database();
        //TODO: Turn off autocommit and make transactions - connection.setAutoCommit(false); & connection.commit();
        UserRepository userRepository = new DatabaseUserRepository(database);
        CardRepository cardRepository = new DatabaseCardRepository(database);

        // Users: /users
        UserService userService = new UserService(userRepository);
        controllerList.add(new UserController(userService));

        // Session: /sessions
        SessionService sessionService = new SessionService(userRepository);
        controllerList.add(new SessionController(sessionService));

        // Packages: /packages
        PackageService packageService = new PackageService(cardRepository);
        controllerList.add(new PackageController(packageService));

        return controllerList;
    }
}
