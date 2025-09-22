package com.project.salesmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // swagger groups: protect admin docs, allow public docs
                        .requestMatchers("/v3/api-docs/public/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/v3/api-docs/admin/**").hasRole("ADMIN")
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Thymeleaf view (optional): allow viewing
                        .requestMatchers("/products/view").permitAll()
                        // revenue + gets are public (authenticated USER) if you want; or permitAll
                        .requestMatchers("/api/products/**", "/api/revenue/**", "/api/pdf/**").permitAll()
                        // admin changes
                        .requestMatchers("/api/sales/**").hasRole("ADMIN")
                        .requestMatchers("/api/products", "/api/products/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(basicEntryPoint()));
        return http.build();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint basicEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("api");
        return entryPoint;
    }
}
