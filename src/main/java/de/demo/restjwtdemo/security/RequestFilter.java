package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestFilter extends OncePerRequestFilter {
    @Autowired
    private TokenUtilIF tokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    public void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("do filter");
        // basic auth header should look like this: Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2F......
        final String requestTokenHeader = httpServletRequest.getHeader("Authorization");

        Token tokenToCheck = null;

        // prepare header to get the token only
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Basic"))
            tokenToCheck = new Token(requestTokenHeader.substring(6));

        System.out.println(tokenToCheck.getToken());


        if (tokenToCheck != null && tokenUtil.validateToken(tokenToCheck)) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(tokenUtil.getUsernameFromToken(tokenToCheck));
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, "", userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
