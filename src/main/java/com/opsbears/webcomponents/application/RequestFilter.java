package com.opsbears.webcomponents.application;

import com.opsbears.webcomponents.net.http.ServerHttpRequest;

public interface RequestFilter {
    ServerHttpRequest filter(ServerHttpRequest request);
}
