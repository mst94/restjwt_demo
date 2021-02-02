package de.demo.restjwtdemo.security;

import de.demo.restjwtdemo.model.JwtToken;
import de.demo.restjwtdemo.model.Token;
import de.demo.restjwtdemo.model.UserData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
class TokenUtilJwtImpl implements TokenUtilIF {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validityduration}")
    private long jwtValidityDuration;


    @Override
    public Token generateToken(final UserData userData) {
        long currentTimeMs = System.currentTimeMillis();
        long expDate = currentTimeMs + jwtValidityDuration;

        Map<String,Object> claims = new HashMap<>();
        claims.put("roles", "testrole");

        String jwt = Jwts.builder()
                .setSubject("Testuser")
                .setIssuer("DemoCompany")
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMs))
                .setExpiration(new Date(expDate))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        return new JwtToken(jwt);
    }

    private String extractUsername() {
        return
    }

    @Override
    public boolean checkTokenIsValid(final JwtToken token) {
        // steps: check token signature is not broken, check token is not expired
        Jws<Claims> jws;

        try  {
            jws = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.getToken());
            return true;
        } catch (Exception e)  {
            return false;
        }
    }

    private Claims getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

}
