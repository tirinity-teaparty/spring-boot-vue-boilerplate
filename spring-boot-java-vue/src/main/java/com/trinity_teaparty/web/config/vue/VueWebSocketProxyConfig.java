package com.trinity_teaparty.web.config.vue;

import com.trinity_teaparty.web.config.properties.VueProperties;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.client.WebSocketClientNegotiation;
import io.undertow.websockets.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

@Configuration
public class VueWebSocketProxyConfig {
    private final Logger log = LoggerFactory.getLogger(VueWebSocketProxyConfig.class);
    private final VueProperties vueProperties;

    public VueWebSocketProxyConfig(VueProperties vueProperties) {
        this.vueProperties = vueProperties;
    }

    @Bean
    public UndertowDeploymentInfoCustomizer websocketProxyCustomizer() {
        return deploymentInfo -> deploymentInfo.addInitialHandlerChainWrapper(next ->
                exchange -> {
                    if (exchange.getRequestPath().startsWith("/ws")) {
                        handleWebSocketProxy(exchange);
                    } else {
                        next.handleRequest(exchange);
                    }
                }
        );
    }

    private void handleWebSocketProxy(HttpServerExchange exchange) {
        try {
            XnioWorker worker = Xnio.getInstance().createWorker(OptionMap.EMPTY);

            try (DefaultByteBufferPool pool = new DefaultByteBufferPool(true, 1024)) {
                OptionMap optionMap = OptionMap.EMPTY;

                String scheme = Boolean.TRUE.equals(vueProperties.getSsl()) ? "wss" : "ws";
                String hostUrl = scheme + "://" + vueProperties.getHost() + ":" + vueProperties.getPort();
                URI targetUri = URI.create(hostUrl + exchange.getRequestURI());

                HttpHandler handler = new WebSocketProtocolHandshakeHandler((WebSocketConnectionCallback) (exchange1, clientChannel) -> {
                    WebSocketClientNegotiation negotiation = new WebSocketClientNegotiation(Collections.emptyList(), Collections.emptyList()) {
                    };

                    IoFuture<WebSocketChannel> future = WebSocketClient.connect(
                            worker,
                            pool,
                            optionMap,
                            targetUri,
                            WebSocketVersion.V13,
                            negotiation
                    );

                    future.addNotifier((IoFuture.Notifier<WebSocketChannel, Void>) (future1, attachment) -> {
                        try {
                            WebSocketChannel serverChannel = future1.get();
                            setupProxyChannel(clientChannel, serverChannel);
                            setupProxyChannel(serverChannel, clientChannel);
                        } catch (Exception e) {
                            log.error("handleWebSocketProxy.IoFuture.Notifier error", e);

                            try {
                                clientChannel.sendClose();
                            } catch (IOException e1) {
                                log.error("send close error", e1);
                            }
                        }
                    }, null);
                });

                handler.handleRequest(exchange);
            }
        } catch (Exception e) {
            log.error("handleWebSocketProxy error", e);
        }
    }

    private void setupProxyChannel(WebSocketChannel from, WebSocketChannel to) {
        from.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                WebSockets.sendText(message.getData(), to, null);
            }

            @Override
            protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) {
                WebSockets.sendBinary(message.getData().getResource(), to, null);
            }

            @Override
            protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) throws IOException {
                to.sendClose();
            }
        });

        from.resumeReceives();
    }
}
