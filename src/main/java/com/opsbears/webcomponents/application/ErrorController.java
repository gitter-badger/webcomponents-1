package com.opsbears.webcomponents.application;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ErrorController {
    private boolean debug;

    public ErrorController(boolean debug) {
        this.debug = debug;
    }

    public String onNotFound() {
        throw new RuntimeException("test");
    }

    public String onMethodNotAllowed() {
        return "<h1>Method not allowed</h1>";
    }

    public Map<String, Object> onInternalServerError(Exception e) {
        Map<String,Object> viewModel = new HashMap<>();
        viewModel.put("exception", e);

        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        viewModel.put("stacktrace", writer.toString());

        return viewModel;
    }
}
