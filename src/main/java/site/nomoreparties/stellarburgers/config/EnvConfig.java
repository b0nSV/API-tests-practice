package site.nomoreparties.stellarburgers.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({"system:properties",
        "classpath:env.properties"})
public interface EnvConfig extends Config {

    @Key("${stand}.host")
    String getHost();

    @Key("base_path")
    String getBasePath();
}
