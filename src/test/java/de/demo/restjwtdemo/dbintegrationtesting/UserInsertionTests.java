package de.demo.restjwtdemo.dbintegrationtesting;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import de.demo.restjwtdemo.controller.UserController;
import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.persistence.PersistenceServiceIF;
import de.demo.restjwtdemo.persistence.PersistenceServiceSQLImpl;
import de.demo.restjwtdemo.security.RequestFilter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.event.annotation.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class UserInsertionTests {
    @LocalServerPort
    private int port;

    @Autowired
    private UserController userController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PersistenceServiceSQLImpl serviceSQL;

    @Autowired
    private RequestFilter filter;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersistenceServiceIF persistence;

    private Authentication authentication;

    public Authentication getAuthentication() {
        return this.authentication;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    int tmpUserId;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(filter).build();
        System.out.println("create test user");

        UserModel user = new UserModel();
        user.setLname("Testlastname");
        user.setFname("Testfirstname");
        user.setEmail("testemail@test.de");
        user.setLogin("testuser");
        user.setPassword("unhashedtestpw");
        persistence.createUser(user);

        UserModel userFromDb = persistence.getUserByUsername("testuser");
        tmpUserId = userFromDb.getId();
    }

    @Test
    @WithMockUser(username = "test", password = "password", authorities = {"USER", "ADMIN"})
    public void shouldReturnInsertedUser() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/" +tmpUserId);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        System.out.println(result.getResponse().toString());
        int status = response.getStatus();
        assertThat(status).isEqualTo(200);
    }

    @AfterEach
    public void cleanDb() throws Exception {
        persistence.deleteUserById(tmpUserId);
    }
}
