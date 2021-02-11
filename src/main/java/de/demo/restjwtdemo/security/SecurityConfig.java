package de.demo.restjwtdemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // ?
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RequestFilter requestFilter;

    @Autowired
    private AuthEntryPointImpl authEntryPoint;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // declare which user details service should be used for load credentials for checking, set bcryptpw encoder
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    // declare the use of bcrypt password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // for login path authorization should be off, all other requests should require auth
        httpSecurity
                .csrf().disable()
                .authorizeRequests().antMatchers("/login").permitAll()
                .antMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                //.antMatchers(HttpMethod.POST).hasRole("ADMIN")
                //.antMatchers(HttpMethod.PUT).hasRole("ADMIN")
                //.anyRequest().authenticated().and()
                .and().exceptionHandling()
                .authenticationEntryPoint(authEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().addFilterBefore(requestFilter,
                UsernamePasswordAuthenticationFilter.class);

        // add filter so that all requests are checked for a valid token
        httpSecurity.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
