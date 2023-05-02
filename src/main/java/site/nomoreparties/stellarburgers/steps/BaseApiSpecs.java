package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import site.nomoreparties.stellarburgers.EnvConfig;

public class BaseApiSpecs {

    private static final EnvConfig envConfig = ConfigFactory.create(EnvConfig.class);
    private static final String HOST = envConfig.getHost();
    private static final String BASE_PATH = envConfig.getBasePath();
    public static final String BASE_URL = HOST + BASE_PATH;

    public static RequestSpecification getPostReqSpec() {
        return new RequestSpecBuilder().log(LogDetail.ALL)
                .setContentType(ContentType.JSON).build()
                .filter(new AllureRestAssured());
    }

    public static RequestSpecification getPatchReqSpec() {
        return new RequestSpecBuilder().log(LogDetail.ALL)
                .setContentType(ContentType.JSON).build()
                .filter(new AllureRestAssured());
    }

    public static RequestSpecification getGetReqSpec() {
        return new RequestSpecBuilder().log(LogDetail.ALL)
                .build()
                .filter(new AllureRestAssured());
    }

    public static RequestSpecification getDeleteReqSpec() {
        return new RequestSpecBuilder().log(LogDetail.ALL)
                .build()
                .filter(new AllureRestAssured());
    }
}
