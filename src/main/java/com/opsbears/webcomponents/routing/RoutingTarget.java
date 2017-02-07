package com.opsbears.webcomponents.routing;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;

@ParametersAreNonnullByDefault
public class RoutingTarget {
    private Object controller;
    private Method method;

    public RoutingTarget(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public RoutingTarget(Object controller, String methodName) throws NoSuchMethodException {
        this.controller = controller;

        @Nullable
        Method foundMethod = null;
        for (Method method : controller.getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                foundMethod = method;
                break;
            }
        }
        if (foundMethod == null) {
            throw new NoSuchMethodException(methodName);
        }
        this.method = foundMethod;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
