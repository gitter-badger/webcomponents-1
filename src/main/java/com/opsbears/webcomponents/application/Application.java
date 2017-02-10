package com.opsbears.webcomponents.application;

import com.opsbears.webcomponents.dic.InjectionConfiguration;
import com.opsbears.webcomponents.dic.Injector;
import com.opsbears.webcomponents.routing.*;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.HashMap;

import static org.eclipse.jetty.util.resource.Resource.newClassPathResource;

@ParametersAreNonnullByDefault
public class Application implements Runnable {
    private boolean development;

    public Application(boolean development) {
        this.development = development;
    }

    protected InjectionConfiguration getInjectorConfiguration() {
        InjectionConfiguration configuration = new InjectionConfiguration();

        HashMap<String, Object> errorControllerParameters = new HashMap<>();
        errorControllerParameters.put("development", development);
        configuration.define(ErrorController.class, errorControllerParameters);

        return configuration;
    }

    protected Router getRouter(Injector injector) throws NoSuchMethodException {
        ErrorController errorController = injector.make(ErrorController.class);

        RegexRouterConfiguration configuration = new RegexRouterConfiguration(
            new RoutingTarget(errorController, "onNotFound"),
            new RoutingTarget(errorController, "onMethodNotAllowed"),
            new RoutingTarget(errorController, "onInternalServerError")
        );

        return new RegexRouter(configuration);
    }

    protected ServerConnector getHttpConnector(Server server) {
        HttpConfiguration             config                  = new HttpConfiguration();
        HttpConnectionFactory         httpConnectionFactory   = new HttpConnectionFactory(config);
        HTTP2CServerConnectionFactory http2cConnectionFactory = new HTTP2CServerConnectionFactory(config);
        ServerConnector http = new ServerConnector(server, httpConnectionFactory, http2cConnectionFactory);
        http.setIdleTimeout(30000);
        return http;
    }

    @Nullable
    protected SslContextFactory getSslContextFactory() {
        return null;
    }

    @Nullable
    protected ServerConnector getHttpsConnector(Server server) {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(8443);

        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());
        HttpConnectionFactory https             = new HttpConnectionFactory(httpsConfig);
        SslContextFactory     sslContextFactory = getSslContextFactory();
        if (sslContextFactory == null) {
            return null;
        }
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        ServerConnector httpsConnector;
        try {
            ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
            alpn.setDefaultProtocol("h2");
            HTTP2ServerConnectionFactory h2  = new HTTP2ServerConnectionFactory(httpsConfig);
            SslConnectionFactory         ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());
            httpsConnector = new ServerConnector(server, ssl, alpn, h2, https);
        } catch (IllegalStateException e) {
            SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, https.getProtocol());
            httpsConnector = new ServerConnector(server, ssl, https);
        }
        return httpsConnector;
    }

    public void run() {
        Server server = new Server();
        ServerConnector http = getHttpConnector(server);
        if (development) {
            http.setHost("localhost");
        }
        http.setPort(8080);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        ServerConnector https = getHttpsConnector(server);
        if (https != null) {
            if (development) {
                https.setHost("localhost");
            }
            https.setPort(8443);
            https.setIdleTimeout(30000);
            server.addConnector(https);
        }

        for(Connector y : server.getConnectors()) {
            for(ConnectionFactory x  : y.getConnectionFactories()) {
                if(x instanceof HttpConnectionFactory) {
                    ((HttpConnectionFactory)x).getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }

        InjectionConfiguration injectorConfiguration = getInjectorConfiguration();
        Injector               injector              = new Injector(injectorConfiguration);

        try {
            server.setHandler(
                new RequestHandler(
                    development,
                    getRouter(injector),
                    injector,
                    injectorConfiguration
                )
            );
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        boolean     development = args.length > 0 && args[0].equals("--development");
        Application application = new Application(development);
        application.run();
    }
}
