package com.github.vevc.config;

import java.util.Properties;

/**
 * @author vevc
 */
public class AppConfig {

    public static final String DOMAIN = "domain";
    public static final String PORT = "port";
    public static final String UUID = "uuid";
    public static final String PASSWORD = "password";
    public static final String TUIC_VERSION = "tuic-version";
    public static final String REMARKS_PREFIX = "remarks-prefix";

    private String domain;
    private String port;
    private String uuid;
    private String password;
    private String tuicVersion;
    private String remarksPrefix;

    public static AppConfig load(Properties props) {
        if (props == null) {
            return null;
        }
        AppConfig cfg = new AppConfig();
        cfg.setDomain(props.getProperty(DOMAIN, "vevc.github.com"));
        cfg.setPort(props.getProperty(PORT, "25565"));
        cfg.setUuid(props.getProperty(UUID, java.util.UUID.randomUUID().toString()));
        cfg.setPassword(props.getProperty(PASSWORD, java.util.UUID.randomUUID().toString().substring(0, 8)));
        cfg.setTuicVersion(props.getProperty(TUIC_VERSION, "1.6.5"));
        cfg.setRemarksPrefix(props.getProperty(REMARKS_PREFIX, "vevc"));
        return cfg;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTuicVersion() {
        return tuicVersion;
    }

    public void setTuicVersion(String tuicVersion) {
        this.tuicVersion = tuicVersion;
    }

    public String getRemarksPrefix() {
        return remarksPrefix;
    }

    public void setRemarksPrefix(String remarksPrefix) {
        this.remarksPrefix = remarksPrefix;
    }
}
