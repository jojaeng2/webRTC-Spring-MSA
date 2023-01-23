package webrtc.v1.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import webrtc.v1.config.jwt.CustomPasswordEncoder;
import webrtc.v1.config.jwt.JwtAuthenticationEntryPoint;
import webrtc.v1.utils.jwt.service.JwtRequestFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final UserDetailsService jwtUserDetailsService;

    private final JwtRequestFilter jwtRequestFilter;


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(encoder.passwordEncoder());
    }



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and()
                .csrf().disable()
                .authorizeRequests().antMatchers(
                        "/api/v1/webrtc/chat/authenticate",
                        "/api/v1/webrtc/chat/register"
                        ).permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/configuration/ui").permitAll()
                .antMatchers("/configuration/security").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/**").permitAll()
                .antMatchers("/ws-stomp/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                // all other requests need to be authenticated
                .anyRequest().authenticated().and()
                        .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
