package com.opsbears.webcomponents.routing;

import com.opsbears.webcomponents.net.http.ServerHttpRequest;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RoutingRequest {
    private ServerHttpRequest request;

    public RoutingRequest(ServerHttpRequest request) {
        this.request = request;
    }

    ServerHttpRequest getRequest() {
        return request;
    }
}
