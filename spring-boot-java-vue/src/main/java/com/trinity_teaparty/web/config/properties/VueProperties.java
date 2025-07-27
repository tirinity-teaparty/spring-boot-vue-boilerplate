package com.trinity_teaparty.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vue.proxy")
public class VueProperties {
    private boolean useVueBundle;
    private String host;
    private int port;
    private boolean ssl;

    public boolean isUseVueBundle() { return useVueBundle; }

    public void setUseVueBundle(boolean useVueBundle) {
        this.useVueBundle = useVueBundle;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}
