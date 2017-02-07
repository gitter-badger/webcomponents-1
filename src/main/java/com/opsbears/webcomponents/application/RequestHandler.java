package com.opsbears.webcomponents.application;

import com.opsbears.webcomponents.dic.InjectionConfiguration;
import com.opsbears.webcomponents.dic.Injector;
import com.opsbears.webcomponents.net.http.ServerHttpRequest;
import com.opsbears.webcomponents.net.http.ServerHttpResponse;
import com.opsbears.webcomponents.routing.Router;
import com.opsbears.webcomponents.routing.RoutingRequest;
import com.opsbears.webcomponents.routing.RoutingResult;
import com.opsbears.webcomponents.routing.RoutingTarget;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
class RequestHandler extends AbstractHandler {
    private boolean  debug;
    private Router   router;
    private Injector injector;
    private InjectionConfiguration injectorConfiguration;

    RequestHandler(
        boolean debug,
        Router router,
        Injector injector,
        InjectionConfiguration injectorConfiguration
    ) {
        this.debug = debug;
        this.router = router;
        this.injector = injector;
        this.injectorConfiguration = injectorConfiguration;
    }

    private ServerHttpResponse invokeRoutingTarget(
        Injector injector,
        RoutingResult routingResult
    ) throws InvocationTargetException, IllegalAccessException {
        RoutingTarget routingTarget = routingResult.getTarget();

        Map<String,Object> methodParameters = new HashMap<>();
        for (Map.Entry<String, String> entry : routingResult.getParameters().entrySet()) {
            methodParameters.put(entry.getKey(), entry.getValue());
        }

        Object result = injector.execute(
            routingTarget.getController(),
            routingTarget.getMethod(),
            methodParameters
        );

        ServerHttpResponse response = new ServerHttpResponse();
        if (result instanceof String) {
            return response.withBodyReader(new StringReader((String) result));
        } else if (result instanceof Map) {
            JtwigModel model = JtwigModel.newModel();

            for (Object entry : ((Map) result).entrySet()) {
                Map.Entry typedEntry = (Map.Entry) entry;
                if (!(typedEntry.getKey() instanceof String)) {
                    throw new RuntimeException(
                        "Constructor returned invalid map key of " + typedEntry.getKey().getClass()
                    );
                }
                String key = (String) typedEntry.getKey();
                model = model.with(key, typedEntry.getValue());
            }

            JtwigTemplate template = JtwigTemplate.classpathTemplate(
                routingTarget.getController().getClass().getName().replace('.', '/') + "/" +
                routingTarget.getMethod().getName() + ".twig"
            );

            return response.withBodyReader(
                new StringReader(
                    template.render(model)
                )
            );
        } else if (result instanceof HttpServletResponse) {
            return (ServerHttpResponse) result;
        } else {
            assert result != null;
            throw new RuntimeException(
                "Constructor returned an invalid return type of " + result.getClass()
            );
        }
    }

    @Override
    public void handle(
        String target,
        Request baseRequest,
        HttpServletRequest servletRequest,
        HttpServletResponse servletResponse
    ) throws IOException, ServletException {
        servletResponse.setStatus(200);
        servletResponse.setHeader("Content-Type", "text/html;charset=utf-8");

        ServerHttpRequest  request  = new ServerHttpRequest(servletRequest);
        ServerHttpResponse response = new ServerHttpResponse();

        RoutingRequest routingRequest  = new RoutingRequest(request);
        RoutingResult  routingResponse = router.route(routingRequest);

        InjectionConfiguration injectionConfiguration = this.injectorConfiguration.clone();
        injectionConfiguration.share(request);
        Injector injector = new Injector(injectionConfiguration);

        try {
            response = invokeRoutingTarget(
                injector,
                routingResponse
            );
        } catch (InvocationTargetException|IllegalAccessException e) {
            try {
                Exception exception = e;
                if (exception instanceof InvocationTargetException) {
                    Throwable throwable = exception.getCause();
                    if (throwable instanceof Exception) {
                        exception = ((Exception) throwable);
                    }
                }
                injectionConfiguration = this.injectorConfiguration.clone();
                injectionConfiguration.alias(Exception.class, exception.getClass());
                injectionConfiguration.share(exception);
                injector = new Injector(injectionConfiguration);
                response = invokeRoutingTarget(injector, router.getInternalServerErrorRoute());
            } catch (Exception e2) {
                response = response.withStatusCode(500);
                String responseText = "<h1>Internal Server Error</h1>";
                if (debug) {
                    responseText += "<pre><code>";
                    StringWriter exceptionStringWriter = new StringWriter();
                    PrintWriter exceptionPrintWriter = new PrintWriter(exceptionStringWriter);
                    e.printStackTrace(exceptionPrintWriter);
                    responseText += StringUtils.replaceEach(
                        exceptionStringWriter.toString(),
                        new String[]{"&", "\"", "<", ">"},
                        new String[]{"&amp;", "&quot;", "&lt;", "&gt;"}
                    );
                    responseText += "</code></pre>";
                }
                response = response.withBodyReader(
                    new StringReader(responseText)
                );
            }
        }

        response.toServletResponse(servletResponse);
        servletResponse.flushBuffer();
    }
}
