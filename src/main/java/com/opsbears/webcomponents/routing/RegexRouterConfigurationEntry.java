package com.opsbears.webcomponents.routing;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class RegexRouterConfigurationEntry {
    private String  method;
    private Pattern pattern;
    private Set<String> captureGroups = new TreeSet<>();
    private RoutingTarget  target;

    public RegexRouterConfigurationEntry(String method, Pattern pattern, RoutingTarget target) {
        this.method = method;
        this.pattern = pattern;
        this.target = target;

        Matcher matcher = Pattern.compile("\\(\\?<(?<name>.*?)>").matcher(pattern.toString());

        for (int i = 0; i < matcher.groupCount(); i++) {
            captureGroups.add(matcher.group(i));
        }
    }

    RoutingTarget getTarget() {
        return target;
    }

    Pattern getPattern() {
        return pattern;
    }

    String getMethod() {
        return method;
    }

    Set<String> getCaptureGroups() {
        return Collections.unmodifiableSet(captureGroups);
    }
}
