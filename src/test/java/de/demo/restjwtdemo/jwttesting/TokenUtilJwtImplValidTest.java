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

@SpringBootTest
public class TokenUtilJwtImplValidTest {
    @Autowired
    private TokenUtilIF tokenUtil;

    Token tmpToken;

    @BeforeEach
    public void createTestToken()  {
        tmpToken = tokenUtil.generateToken(new User("testuser", "testpassword", new ArrayList<>()));
    }

    @Test
    public void shouldBeValid()  {
        assertThat(tokenUtil.validateToken(tmpToken)).isEqualTo(true);
    }

}
