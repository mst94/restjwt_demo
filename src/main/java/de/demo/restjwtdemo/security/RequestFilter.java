package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestFilter extends OncePerRequestFilter {
    @Autowired
    private TokenUtilIF tokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        // basic auth header should look like this: Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2F......
        final String requestTokenHeader = httpServletRequest.getHeader("Authorization");

        if (requestTokenHeader == null)
            return;

        // prepare header to get the token only
        if (!requestTokenHeader.startsWith("Basic"))
            return;

        final JwtToken tokenToCheck = new JwtToken(requestTokenHeader.substring(6));

        if (tokenUtil.validateToken(tokenToCheck))  {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, "", userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
    }
}
