package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.Token;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
class TokenUtilJwtImpl implements TokenUtilIF {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validityduration.minutes}")
    private long jwtValidityDuration;


    @Override
    public Token generateToken(final UserDetails userDetails) {
        long currentTimeMs = System.currentTimeMillis();
        long expDate = currentTimeMs + jwtValidityDuration * 1000 * 60;

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", "testrole");
        claims.put("username", userDetails.getUsername());
        System.out.println("set username: " +userDetails.getUsername());

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
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(final Token token)   {
        try {
            Claims claims = getAllClaimsFromToken(token.getToken());
            return (String) claims.get("username");
        } catch (NullPointerException | ClassCastException e)  {
            throw new JwtException("Username could not be retrieved from token");
        }
    }

}
