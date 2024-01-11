package at.technikum.apps.mtcg;

import at.technikum.apps.mtcg.controller.Controller;
import at.technikum.apps.mtcg.controller.SessionController;
import at.technikum.apps.mtcg.controller.UserController;
import at.technikum.apps.mtcg.repository.DatabaseUserRepository;
import at.technikum.apps.mtcg.repository.UserRepository;
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

        // Users: /users
        UserRepository userRepository = new DatabaseUserRepository();
        UserService userService = new UserService(userRepository);
        controllerList.add(new UserController(userService));

        // Session: /sessions
        SessionService sessionService = new SessionService(userRepository);
        controllerList.add(new SessionController(sessionService));

        return controllerList;
    }
}
