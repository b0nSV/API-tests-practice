package org.example;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:env.properties")
public interface EnvConfig extends Config {

    @Key("host")
    String getHost();

    @Key("base_path")
    String getBasePath();
}
