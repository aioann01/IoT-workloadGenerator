package cy.cs.ucy.ade.aioann01.SSO.Utils;

import org.springframework.http.HttpStatus;

public class FrameworkConstants {

    public static final String SUCCESS_MESSAGE="Success";
    public static final HttpStatus HTTP_SUCCESS= HttpStatus.OK;
    public static final HttpStatus HTTP_BAD_REQUEST= HttpStatus.BAD_REQUEST;
    public static final HttpStatus HTTP_UNAUTHORIZED= HttpStatus.UNAUTHORIZED;
    public static final HttpStatus HTTP_FORBIDDEN= HttpStatus.FORBIDDEN;
    public static final HttpStatus HTTP_INTERNAL_SERVER_ERROR= HttpStatus.INTERNAL_SERVER_ERROR;
    public static final HttpStatus HTTP_NOT_FOUND= HttpStatus.NOT_FOUND;
    public static final String USERNAME ="username";
    public static final String PASSWORD= "password";
    public static final String SECRET= "secret";
    public static final String UNEXPECTED_ERROR_OCCURRED="Unexpected error occurred";
    public static final String AUTHORIZATION_ERROR="Authorization error";
    public static final String AUTHENTICATION_ERROR="Authentication error";
    public static final String VALIDATION_ERROR="Validation error";
    public static final String AUTHENTICATE_URI="authenticate";
    public static final String ERROR_MESSAGE="ErrorMessage";
    public static final String ERROR_MESSAGE_TYPE="ErrorMessageType";
    public static final String EXCEPTION_CAUGHT="Exception caught:";
    public static final String APPLICATION_PROPERTIES="application.properties";
    public static final String AUTHORIZATION_HEADER="Authorization";
    public static final String REQUEST_URI_TO_BE_CHECK_FOR_AUTHORIZATION_HEADER="requestToBeAuthorizedURI";
}
