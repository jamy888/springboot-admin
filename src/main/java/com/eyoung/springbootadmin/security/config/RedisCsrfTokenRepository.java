package com.eyoung.springbootadmin.security.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author eYoung
 * @description:
 * @date create at 2020/11/3 11:14
 */
@Component
public class RedisCsrfTokenRepository implements CsrfTokenRepository {

    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    private String headerName = DEFAULT_CSRF_HEADER_NAME;
    private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

    /**
     * Generates a {@link CsrfToken}
     *
     * @param request the {@link HttpServletRequest} to use
     * @return the {@link CsrfToken} that was generated. Cannot be null.
     */
    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new DefaultCsrfToken(this.headerName, this.parameterName,
                createNewToken());
    }

    /**
     * Saves the {@link CsrfToken} using the {@link HttpServletRequest} and
     * {@link HttpServletResponse}. If the {@link CsrfToken} is null, it is the same as
     * deleting it.
     *
     * @param token    the {@link CsrfToken} to save or null to delete
     * @param request  the {@link HttpServletRequest} to use
     * @param response the {@link HttpServletResponse} to use
     */
    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String tokenValue = token == null ? "" : token.getToken();
    }

    /**
     * Loads the expected {@link CsrfToken} from the {@link HttpServletRequest}
     *
     * @param request the {@link HttpServletRequest} to use
     * @return the {@link CsrfToken} or null if none exists
     */
    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return null;
    }

    private String createNewToken() {
        return UUID.randomUUID().toString();
    }
}
