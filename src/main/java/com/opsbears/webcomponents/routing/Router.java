package com.opsbears.webcomponents.routing;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Router {
    public RoutingResult route(RoutingRequest request);
    public RoutingResult getNotFoundRoute();
    public RoutingResult getInternalServerErrorRoute();
}
