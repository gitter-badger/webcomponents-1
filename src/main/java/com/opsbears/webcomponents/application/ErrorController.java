package com.opsbears.webcomponents.application;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class ErrorController {
    private boolean development;

    public ErrorController(boolean development) {
        this.development = development;
    }

    public Map<String, Object> onNotFound() {
        return new HashMap<>();
    }

    public Map<String, Object> onMethodNotAllowed() {
        return new HashMap<>();
    }

    public Map<String, Object> onInternalServerError(Exception e) {
        Map<String,Object> viewModel = new HashMap<>();
        viewModel.put("exception", e);

        viewModel.put("debug", development);

        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        viewModel.put("stacktrace", writer.toString());

        return viewModel;
    }
}
