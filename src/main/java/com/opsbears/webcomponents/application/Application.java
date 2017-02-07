package com.opsbears.webcomponents.application;

import com.opsbears.webcomponents.dic.InjectionConfiguration;
import com.opsbears.webcomponents.dic.Injector;
import com.opsbears.webcomponents.routing.RegexRouter;
import com.opsbears.webcomponents.routing.RegexRouterConfiguration;
import com.opsbears.webcomponents.routing.Router;
import com.opsbears.webcomponents.routing.RoutingTarget;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class Application {
    private boolean debug;

    public Application(boolean debug) {
        this.debug = debug;
    }

    protected InjectionConfiguration getInjectorConfiguration() {
        InjectionConfiguration configuration = new InjectionConfiguration();
        configuration.define(ErrorController.class);
        return configuration;
    }

    protected Router getRouter(Injector injector) throws NoSuchMethodException {
        ErrorController errorController = new ErrorController(debug);

        RegexRouterConfiguration configuration = new RegexRouterConfiguration(
            new RoutingTarget(errorController, "onNotFound"),
            new RoutingTarget(errorController, "onMethodNotAllowed"),
            new RoutingTarget(errorController, "onInternalServerError")
        );

        return new RegexRouter(configuration);
    }

    public void run() throws Exception {
        Server server = new Server();

        ServerConnector http = new ServerConnector(server);

        http.setHost("localhost");
        http.setPort(8080);
        http.setIdleTimeout(30000);

        server.addConnector(http);

        InjectionConfiguration injectorConfiguration = getInjectorConfiguration();
        Injector injector = new Injector(injectorConfiguration);

        server.setHandler(
            new RequestHandler(
                debug,
                getRouter(injector),
                injector,
                injectorConfiguration
            )
        );

        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception {
        boolean debug = args.length > 0 && args[0].equals("--debug");
        Application application = new Application(debug);
        application.run();
    }
}
