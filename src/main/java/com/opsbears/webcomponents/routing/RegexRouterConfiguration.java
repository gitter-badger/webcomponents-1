package com.opsbears.webcomponents.routing;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.*;

@ParametersAreNonnullByDefault
public class RegexRouterConfiguration {
    private RoutingTarget notFoundRoute;
    private RoutingTarget methodNotAllowedRoute;
    private RoutingTarget internalServerErrorRoute;
    private SortedSet<RegexRouterConfigurationEntry> rules = new TreeSet<>();

    public RegexRouterConfiguration(
        RoutingTarget notFoundRoute,
        RoutingTarget methodNotAllowedRoute,
        RoutingTarget internalServerErrorRoute
    ) {
        this.notFoundRoute = notFoundRoute;
        this.methodNotAllowedRoute = methodNotAllowedRoute;
        this.internalServerErrorRoute = internalServerErrorRoute;
    }

    public void addRule(RegexRouterConfigurationEntry rule) {
        rules.add(rule);
    }

    SortedSet<RegexRouterConfigurationEntry> getRules() {
        return Collections.unmodifiableSortedSet(rules);
    }

    RoutingResult getNotFoundRoute() {
        return new RoutingResult(
            404,
            notFoundRoute
        );
    }

    RoutingResult getMethodNotAllowedRoute() {
        return new RoutingResult(
            405,
            methodNotAllowedRoute
        );
    }

    RoutingResult getInternalServerErrorRoute() {
        return new RoutingResult(
            500,
            internalServerErrorRoute
        );
    }
}
