package com.tinqinacademy.authentication.api.restapiroutes;

public class RestApiRoutes {
    private static final String API_V1 = "/api/v1";
    //containing api_v1
    private static final String API_V1_AUTH = API_V1 + "/auth";
    //user
    public static final String AUTH_CHECK_USER_AGE = API_V1_AUTH + "/check/{userId}";
    public static final String AUTH_AUTHENTICATE = API_V1_AUTH + "/authenticate";
    public static final String AUTH_LOGIN = API_V1_AUTH + "/login";
    public static final String AUTH_REGISTER = API_V1_AUTH + "/register";
    public static final String AUTH_CHANGE_PASSWORD = API_V1_AUTH + "/change-password";
    public static final String AUTH_PROMOTE = API_V1_AUTH + "/promote";
    public static final String AUTH_DEMOTE = API_V1_AUTH + "/demote";
    public static final String AUTH_LOGOUT = API_V1_AUTH + "/logout";
    public static final String AUTH_CONFIRM_REGISTRATION = API_V1_AUTH + "/confirm-registration";
    public static final String AUTH_RECOVER_PASSWORD = API_V1_AUTH + "/recover-password";
}
