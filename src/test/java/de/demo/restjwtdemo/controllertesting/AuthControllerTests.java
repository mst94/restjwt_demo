package de.demo.restjwtdemo.controllertesting;

import de.demo.restjwtdemo.security.RequestFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestFilter filter;

    @Autowired
    private WebApplicationContext context;

    @BeforeTestMethod
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(filter).build();
    }

    @Test
    void shouldReturn401WhenRequestingInvalidPath() throws Exception {
        //AssertionsForClassTypes.assertThat(this.restTemplate.getForEntity("http://localhost:" +port, HttpStatus.class)
        //.getStatusCode()).isEqualTo(401);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/login")
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        int status = response.getStatus();
        assertThat(status).isEqualTo(405);

        requestBuilder = MockMvcRequestBuilders
                .delete("/login")
                .accept(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        status = response.getStatus();
        assertThat(status).isEqualTo(405);

        requestBuilder = MockMvcRequestBuilders
                .put("/login")
                .accept(MediaType.APPLICATION_JSON);
        result = mockMvc.perform(requestBuilder).andReturn();
        response = result.getResponse();
        status = response.getStatus();
        assertThat(status).isEqualTo(405);
    }
}
