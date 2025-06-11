package com.github.narcispurghel.bookswap.constant;

public final class EndpointsConstants {
    public static final String API_VERSION = "/v1";
    public static final String API_ENDPOINT = "/api" + API_VERSION;
    public static final String DOCUMENTATION_ENDPOINT = API_ENDPOINT + "/documentation";
    public static final String SCALAR_ENDPOINT = "/v3/scalar";
    public static final String AUTHENTICATION_ENDPOINT = API_ENDPOINT + "/authentication";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String SIGNUP_ENDPOINT = "/signup";
    public static final String LOGOUT_ENDPOINT = "/logout";
    
    private EndpointsConstants() {
    }
}
