package com.github.narcispurghel.bookswap.constant;

import java.util.Set;

public final class EndpointsConstants {
    public static final String API_VERSION = "/v1";
    public static final String API_ENDPOINT = "/api" + API_VERSION;
    public static final String OPENAPI_ENDPOINT = "/v3/api-docs";
    public static final String DOCUMENTATION_ENDPOINT = API_ENDPOINT + "/documentation";
    public static final String SCALAR_ENDPOINT = "/v3/scalar";
    public static final String AUTHENTICATION_ENDPOINT = API_ENDPOINT + "/authentication";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String SIGNUP_ENDPOINT = "/signup";
    public static final String LOGOUT_ENDPOINT = "/logout";
    public static final String HOME_ENDPOINT = "/";
    public static final Set<String> PUBLIC_ENDPOINTS =
            Set.of(AUTHENTICATION_ENDPOINT + LOGIN_ENDPOINT,
                    AUTHENTICATION_ENDPOINT + SIGNUP_ENDPOINT,
                    HOME_ENDPOINT,
                    DOCUMENTATION_ENDPOINT + SCALAR_ENDPOINT,
                    OPENAPI_ENDPOINT);

    private EndpointsConstants() {
    }
}
