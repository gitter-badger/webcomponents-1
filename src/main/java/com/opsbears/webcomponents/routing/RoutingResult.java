package com.opsbears.webcomponents.routing;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class RoutingResult {
    private Integer             statusCode = 200;
    private RoutingTarget       target;
    private Map<String, String> parameters;

    RoutingResult(Integer statusCode, RoutingTarget target) {
        this.statusCode = statusCode;
        this.target = target;
        this.parameters = new HashMap<>();
    }

    RoutingResult(Integer statusCode, RoutingTarget target, Map<String, String> parameters) {
        this.statusCode = statusCode;
        this.target = target;
        this.parameters = parameters;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public RoutingTarget getTarget() {
        return target;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
