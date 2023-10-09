package com.coursemanagement.config.extension;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AllureRestAssuredExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(final ExtensionContext extensionContext) {
        RestAssured.filters(new AllureRestAssured());
    }
}
