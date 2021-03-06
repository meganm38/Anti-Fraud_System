package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();

        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .antMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .antMatchers("/actuator/shutdown").permitAll()
                .antMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                .mvcMatchers(HttpMethod.PUT, "/api/auth/access").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMINISTRATOR")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session

    }
}
