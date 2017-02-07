package com.opsbears.webcomponents.application;

import com.opsbears.webcomponents.net.http.ServerHttpResponse;

public interface ResponseFilter {
    ServerHttpResponse filter(ServerHttpResponse response);
}
