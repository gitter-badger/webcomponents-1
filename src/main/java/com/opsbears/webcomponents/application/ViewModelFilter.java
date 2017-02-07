package com.opsbears.webcomponents.application;

import java.util.Map;

public interface ViewModelFilter {
    Map<String, Object> filter(Map<String, Object> viewModel);
}
