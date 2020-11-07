package cy.cs.ucy.ade.aioann01.SSO.Controller;

import cy.cs.ucy.ade.aioann01.SSO.Model.Http.Exchange;
import cy.cs.ucy.ade.aioann01.SSO.Model.Http.ResponseMessage;
import cy.cs.ucy.ade.aioann01.SSO.Utils.JwtTokenUtil;
import cy.cs.ucy.ade.aioann01.SSO.Model.Authentication.JwtRequest;
import cy.cs.ucy.ade.aioann01.SSO.Model.Authentication.JwtResponse;
import cy.cs.ucy.ade.aioann01.SSO.Services.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import static cy.cs.ucy.ade.aioann01.SSO.Utils.FrameworkConstants.*;

@RestController
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired

    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        try {
            final UserDetails userDetails = jwtUserDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (UsernameNotFoundException ex) {
            throw ex;
        }

    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public ResponseEntity<Exchange> authorize(@RequestHeader(AUTHORIZATION_HEADER) String authorizationHeader,
                                              @RequestHeader(REQUEST_URI_TO_BE_CHECK_FOR_AUTHORIZATION_HEADER) String requestUri) {
        log.debug("Retrieved authorization request for: " + requestUri + " with authorization header: " + authorizationHeader);
        String username = null;
        String jwtToken = null;
        String errorMessage = null;
        Exchange response = new Exchange();
        response.setHttpStatus(HTTP_SUCCESS);
        response.setProperty("Authorized", false);
        boolean authorized = false;
        if (authorizationHeader == null) {
            errorMessage = "Authorization header in request is not provided";
            log.error(errorMessage);
        }
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                errorMessage = "Unable to get JWT Token";
                log.error(errorMessage);
                log.error(EXCEPTION_CAUGHT + e.getMessage(), e);

            } catch (ExpiredJwtException e) {
                errorMessage = "JWT Token has expired";
                log.error(errorMessage);
                log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
            } catch (Exception exception) {
                errorMessage = exception.getMessage();
                log.error(EXCEPTION_CAUGHT + exception.getMessage(), exception);
            }

        } else {
            errorMessage = "JWT Token does not begin with Bearer String";
            log.warn(errorMessage);
        }
        try {
            if (username != null /*&& SecurityContextHolder.getContext().getAuthentication() == null*/) {
                UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                    authorized = true;
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                usernamePasswordAuthenticationToken
//                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                // After setting the Authentication in the context, we specify
//                // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (Exception e) {
            log.error(EXCEPTION_CAUGHT + e.getMessage(), e);
            errorMessage = UNEXPECTED_ERROR_OCCURRED + " while validating User in SSO: " + e.getMessage();
            response.setHttpStatus(HTTP_INTERNAL_SERVER_ERROR);
        }
        if (authorized) {
            response.setProperty("Authorized", true);
            response.setBody(new ResponseMessage("Authorized"));
        } else {
            response.setProperty(ERROR_MESSAGE, errorMessage);
            response.setBody(new ResponseMessage("Not Authorized"));
        }
        return  ResponseEntity.ok(response);
    }
}