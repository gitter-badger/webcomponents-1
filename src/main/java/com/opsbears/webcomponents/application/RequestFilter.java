package com.opsbears.webcomponents.application;

import com.opsbears.webcomponents.net.http.ServerHttpRequest;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface RequestFilter {
    ServerHttpRequest filter(ServerHttpRequest request);
}
