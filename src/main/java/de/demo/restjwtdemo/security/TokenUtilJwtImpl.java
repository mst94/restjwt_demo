package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.Token;
import de.demo.restjwtdemo.model.UserRolesEnum;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TokenUtilJwtImpl implements TokenUtilIF {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validityduration.minutes}")
    private long jwtValidityDuration;


    @Override
    public Token generateToken(final UserDetails userDetails) {
        long currentTimeMs = System.currentTimeMillis();
        long expDate = currentTimeMs + jwtValidityDuration * 1000 * 60;

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        for (GrantedAuthority auth : roles) {
            claims.put(auth.getAuthority(), true);
        }

        String jwt = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuer("DemoCompany")
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMs))
                .setExpiration(new Date(expDate))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        return new Token(jwt);
    }

    // validate token via jwt library, if any exception is thrown validation token is not validated
    @Override
    public boolean validateToken(final Token token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.getToken());
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Exception: JWT is expired!");
            return false;
        } catch (JwtException e) {
            System.out.println("Exception: Another JwtException during JWT parsing.");
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaimsFromToken(final Token token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token.getToken()).getBody();
    }

    // extract claims from token and compare it to roles enum, if equal, insert only true boolean values into auth list
    @Override
    public List<GrantedAuthority> getRolesFromToken(Token token) {
        List<GrantedAuthority> list = new ArrayList<>();
        Claims claims = getClaimsFromToken(token);
        for (UserRolesEnum c : UserRolesEnum.values()) {
            if (claims.containsKey(c.toString())) {
                if (claims.get(c.toString(), Boolean.class)) {
                    list.add(new SimpleGrantedAuthority(c.toString()));
                }
            }
        }
        return list;
    }

    public String getUsernameFromToken(final Token token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("username", String.class);
        } catch (NullPointerException | ClassCastException e) {
            throw new JwtException("Username could not be retrieved from token");
        }
    }
}
