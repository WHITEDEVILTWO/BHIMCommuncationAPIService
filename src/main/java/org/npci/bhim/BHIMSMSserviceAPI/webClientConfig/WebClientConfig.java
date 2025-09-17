package org.npci.bhim.BHIMSMSserviceAPI.webClientConfig;

import io.netty.channel.ChannelOption;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() throws SSLException, ConnectTimeoutException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = null;
        try {
            httpClient = HttpClient.create()
                    .secure(t -> t.sslContext(sslContext))
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
        } catch (Exception e) {
            throw new ConnectTimeoutException("Connection Timed Out");
        }
        return  WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
}
