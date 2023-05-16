package site.nomoreparties.stellarburgers.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;

public class BaseApiSpecs {

    private final EnvConfig envConfig = ConfigFactory.create(EnvConfig.class);
    private final String HOST = envConfig.getHost();
    private final String BASE_PATH = envConfig.getBasePath();

    protected RequestSpecification getReqSpecWithBody(ContentType contentType) {
        return new RequestSpecBuilder().addRequestSpecification(getReqSpec())
                .setContentType(contentType)
                .build();
    }

    protected RequestSpecification getReqSpec() {
        return new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(HOST)
                .setBasePath(BASE_PATH)
                .addFilter(new AllureRestAssured())
                .build();
    }

    protected Header createAuthHeader(String accessToken) {
        return new Header("Authorization", accessToken);
    }
}
