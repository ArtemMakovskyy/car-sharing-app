package com.personal.carsharing.carsharingapp.config;

import lombok.RequiredArgsConstructor;
//import online.book.store.security.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.
// configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
//@EnableMethodSecurity
public class SecurityConfig {
    // TODO: 07.11.2023 customize encoder, filterChain, AuthenticationManager
//    private final UserDetailsService userDetailsService;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .cors(AbstractHttpConfigurer::disable)
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(
//                        auth -> auth
//                                .requestMatchers("/auth/**").permitAll()
//                                .anyRequest().authenticated()
//                )
//                .httpBasic(Customizer.withDefaults())
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtAuthenticationFilter,
//                        UsernamePasswordAuthenticationFilter.class)
//                .userDetailsService(userDetailsService)
//                .build();
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration authenticationConfiguration
//    ) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
}
