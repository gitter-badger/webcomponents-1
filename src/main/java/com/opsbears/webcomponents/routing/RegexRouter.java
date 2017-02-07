package com.opsbears.webcomponents.routing;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.regex.Matcher;

@ParametersAreNonnullByDefault
public class RegexRouter implements Router {
    private RegexRouterConfiguration configuration;

    public RegexRouter(RegexRouterConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public RoutingResult route(RoutingRequest request) {
        String method = request.getRequest().getMethod();
        String path   = request.getRequest().getUri().getPath();

        @Nullable
        RoutingResult result = null;
        for (RegexRouterConfigurationEntry rule : configuration.getRules()) {
            Matcher matcher = rule.getPattern().matcher(path);
            if (!matcher.matches()) {
                continue;
            }
            if (!rule.getMethod().equals(method)) {
                result = configuration.getMethodNotAllowedRoute();
            } else {
                Map<String, String> parameters    = new HashMap<>();
                Set<String>         captureGroups = rule.getCaptureGroups();
                for (String captureGroup : captureGroups) {
                    String parameter = matcher.group(captureGroup);
                    if (parameter != null) {
                        parameters.put(captureGroup, parameter);
                    }
                }
                result = new RoutingResult(
                    200,
                    rule.getTarget(),
                    parameters
                );
            }
        }
        if (result == null) {
            result = configuration.getNotFoundRoute();
        }
        return result;
    }

    public RoutingResult getNotFoundRoute() {
        return configuration.getNotFoundRoute();
    }

    public RoutingResult getInternalServerErrorRoute() {
        return configuration.getInternalServerErrorRoute();
    }
}
