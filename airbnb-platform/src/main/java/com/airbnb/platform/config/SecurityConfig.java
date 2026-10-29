package com.airbnb.platform.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/admin/**", "/users/**", "/reservations/all").hasRole("ADMIN")
                .requestMatchers("/reservations/*/confirm").hasAnyRole("HOST", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/payments").hasRole("ADMIN")
                .requestMatchers("/payments/*/cancel", "/payments/*/refund").hasRole("ADMIN")
                .requestMatchers("/amenities/new", "/amenities/*/edit", "/amenities/*/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/amenities", "/amenities/*").hasRole("ADMIN")
                .requestMatchers("/properties/new", "/properties/*/edit", "/properties/*/delete",
                    "/properties/*/rooms/new", "/properties/*/rooms/*/edit", "/properties/*/rooms/*/delete").hasAnyRole("HOST", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/properties", "/properties/*",
                    "/properties/*/rooms", "/properties/*/rooms/*").hasAnyRole("HOST", "ADMIN")
                .requestMatchers("/profile", "/my-properties", "/reservations/**", "/payments/**",
                    "/properties/*/reviews/new", "/properties/*/reviews/*/edit", "/properties/*/reviews/*/delete").authenticated()
                .requestMatchers(HttpMethod.POST, "/properties/*/reviews", "/properties/*/reviews/*").authenticated()

                .requestMatchers(HttpMethod.GET,
                    "/properties", "/properties/search", "/properties/{id}",
                    "/properties/*/rooms", "/properties/*/rooms/*",
                    "/properties/*/reviews", "/properties/*/reviews/*",
                    "/amenities", "/amenities/*",
                    "/api/**"
                ).permitAll()
                .requestMatchers("/api/**").hasAnyRole("HOST", "ADMIN")
                .requestMatchers(
                    "/", "/index", "/about", "/contact",
                    "/auth/**",
                    "/error", "/error/**",
                    "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/h2-console/**",
                    "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                    "/actuator/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, exception) ->
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acces interzis"))
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
