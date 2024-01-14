package at.technikum.apps.mtcg;

import at.technikum.apps.mtcg.controller.Controller;
import at.technikum.server.ServerApplication;
import at.technikum.server.http.HttpContentType;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.util.List;

public class MtcgApp implements ServerApplication {

    private final List<Controller> controllers;

    public MtcgApp() {
        Injector injector = new Injector();

        this.controllers = injector.createController();
    }

    @Override
    public Response handle(Request request) {

        for (Controller controller : controllers) {
            if (!controller.supports(request.getRoute())) {
                continue;
            }

            // THOUGHT: implement this idea
            try {
                return controller.handle(request);
            /*
            // HttpException doesn't exists yet
            } catch (HttpException e) {
                // return e.getHttpStatus() response
            }
            */
            } catch (Exception e) {
                e.printStackTrace();
                // return 500 Internal Server Error
            }
        }

        Response response = new Response();
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType(HttpContentType.APPLICATION_JSON);
        response.setBody("Route not found!");

        return response;
    }
}
