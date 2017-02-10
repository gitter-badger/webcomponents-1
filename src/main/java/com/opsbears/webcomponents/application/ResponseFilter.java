package com.opsbears.webcomponents.application;

import com.opsbears.webcomponents.net.http.ServerHttpRequest;
import com.opsbears.webcomponents.net.http.ServerHttpResponse;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ResponseFilter {
    ServerHttpResponse filter(ServerHttpResponse response, ServerHttpRequest request);
}
