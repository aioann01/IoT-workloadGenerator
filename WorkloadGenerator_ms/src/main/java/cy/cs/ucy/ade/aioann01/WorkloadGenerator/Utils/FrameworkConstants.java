package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Utils;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public class FrameworkConstants {

    public static final String SUCCESS_MESSAGE = "Success";
    public static final HttpStatus HTTP_SUCCESS = HttpStatus.OK;
    public static final HttpStatus HTTP_CREATED = HttpStatus.CREATED;
    public static final HttpStatus HTTP_NO_CONTENT = HttpStatus.NO_CONTENT;
    public static final HttpStatus HTTP_BAD_REQUEST = HttpStatus.BAD_REQUEST;
    public static final HttpStatus HTTP_UNAUTHORIZED = HttpStatus.UNAUTHORIZED;
    public static final HttpStatus HTTP_FORBIDDEN = HttpStatus.FORBIDDEN;
    public static final HttpStatus HTTP_NOT_FOUND = HttpStatus.NOT_FOUND;
    public static final HttpStatus HTTP_INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SECRET = "secret";
    public static final String UNEXPECTED_ERROR_OCCURRED = "Unexpected error occurred";
    public static final String INTERNAL_SERVER_ERROR = "Internal error error:";
    public static final String AUTHORIZATION_ERROR = "Authorization error";
    public static final String AUTHENTICATION_ERROR = "Authentication error";
    public static final String VALIDATION_ERROR = "Validation error";
    public static final String AUTHENTICATE_URI = "authenticate";
    public static final String ERROR_MESSAGE = "ErrorMessage";
    public static final String ERROR_MESSAGE_TYPE = "ErrorMessageType";
    public static final String EXCEPTION_CAUGHT = "Exception caught:";
    public static final String EXCEPTION_CAUGHT_WHILE = "Exception caught ";
    public static final String UNEXPECTED_EXCEPTION_CAUGHT = "Unexpected Exception caught:";
    public static final String VALIDATION_EXCEPTION_CAUGHT = "Validation Exception caught:";
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String WORKLOAD_GENERATOR_PROPERTIES_WINDOWS = "workloadGenerator.properties";
    public static final String WORKLOAD_GENERATOR_PROPERTIES_LINUX = "workloadGenerator.cfg";
    public static String CONFIGS_DIRECTORY_PROPERTY = "configs.directory";
    public static String RESOURCES_DIRECTORY_PROPERTY = "resources.directory";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REQUEST_URI_TO_BE_CHECK_FOR_AUTHORIZATION_HEADER = "requestToBeAuthorizedURI";
    public static final String SSO_PORT_PROPERTY_NAME = "sso.service.port";
    public static final String SSO_SERVICE_HOST_PROPERTY_NAME = "sso.service.host";
    public static final String WORKLOAD_GENERATOR_SERVICE_PORT_PROPERTY_NAME = "workloadGenerator.service.port";
    public static final String WORKLOAD_GENERATOR_SERVICE_HOST_PROPERTY_NAME = "workloadGenerator.service.host";
    public static final String AUTHORIZE_URI = "/authorize";
    public static final String SENSORS_URI = "/sensors";
    public static final String INTER_MS_CALL_HEADER = "interMsCall";
    public static final HttpMethod REQUEST_GET_HTTP_METHOD = HttpMethod.GET;
    public static final HttpMethod REQUEST_POST_HTTP_METHOD = HttpMethod.POST;
    public static final HttpMethod REQUEST_DELETE_HTTP_METHOD = HttpMethod.DELETE;
    public static final String EXCEPTION_IS_SET = "exceptionIsSet";
    public static final String REQUEST_URI = "requestUri";
    public static final String REQUEST_PAYLOAD = "requestPayload";
    public static final String REQUEST_HTTP_METHOD = "requestHttpMethod";
    public static final String REQUEST_RETURN_TYPE = "requestReturnType";
    public static final String HTTP_QUERY_PARAMETER = "?";
    public static final String AUTHORIZATION_ENABLED_PROPERTY = "Authorization.enabled";
    public static int MILLISECONDS_TO_SECONDS = 1000;
    public static final String UTF8_BOM = "\uFEFF";
    public static final String SIMPLE_DATE_FORMAT_FOR_SORTED_CSV = "yyyy-MM-dd HH:mm:ss";








}
