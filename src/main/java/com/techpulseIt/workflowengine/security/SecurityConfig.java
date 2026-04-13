package com.techpulseIt.workflowengine.security;



import com.techpulseIt.workflowengine.security.jwt.AuthTokenFilter;
import com.techpulseIt.workflowengine.security.jwt.JwtAuthEntryPoint;
import com.techpulseIt.workflowengine.security.user.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    private  final AppUserDetailsService userDetailsService;
    private  final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer :: disable)
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        //Public APIs (only auth related)
                        .requestMatchers("/auth/**").permitAll()

                        // CREATE request → REQUESTER
                        .requestMatchers(HttpMethod.POST, "/requests")
                        .hasRole("REQUESTER")

                        // GET request → REQUESTER, APPROVER, ADMIN
                        .requestMatchers(HttpMethod.GET, "/requests/*")
                        .hasAnyRole("REQUESTER", "APPROVER", "ADMIN")

                        // APPROVE / REJECT → APPROVER, ADMIN
                        .requestMatchers(HttpMethod.POST, "/requests/*/approve")
                        .hasAnyRole("APPROVER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/requests/*/reject")
                        .hasAnyRole("APPROVER", "ADMIN")

                        // HISTORY → REQUESTER, ADMIN
                        .requestMatchers(HttpMethod.GET, "/requests/history/*")
                        .hasAnyRole("REQUESTER", "ADMIN")
                        .anyRequest().authenticated()
                );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}