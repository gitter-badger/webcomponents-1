package com.opsbears.webcomponents.application;

import com.opsbears.webcomponents.net.http.ServerHttpRequest;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface ViewModelFilter {
    Map<String, Object> filter(Map<String, Object> viewModel, ServerHttpRequest request);
}
