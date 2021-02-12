package de.demo.restjwtdemo.jwttesting;

import de.demo.restjwtdemo.model.Token;
import de.demo.restjwtdemo.security.TokenUtilIF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = {"jwt.validityduration.minutes=0"})
public class TokenUtilJwtImplExpiredTest {
    @Autowired
    private TokenUtilIF tokenUtil;

    Token tmpToken;

    @BeforeEach
    public void createTestToken() {
        tmpToken = tokenUtil.generateToken(new User("testuser", "testpassword", new ArrayList<>()));
    }

    @Test
    public void shouldBeExpired() {
        assertThat(tokenUtil.validateToken(tmpToken)).isFalse();
    }
}
