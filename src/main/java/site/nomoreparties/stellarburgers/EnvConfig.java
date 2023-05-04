package site.nomoreparties.stellarburgers;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:env.properties")
public interface EnvConfig extends Config {
    @DefaultValue("dev")
    String env();

    @Key("${env}.host")
    String getHost();

    @Key("${env}.base_path")
    String getBasePath();
}
