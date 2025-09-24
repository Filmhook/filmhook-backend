package com.annular.filmhook.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.annular.filmhook.security.jwt.AuthEntryPointJwt;
import com.annular.filmhook.security.jwt.AuthTokenFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors()
                .and()
                .csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(
                        (authorize) -> authorize
                                .antMatchers("/og/post/view/**","/media/**","/report/**","/","/.well-known/**","/user/register","/industryUser/deleteTemporaryDetails", "/user/verifyEmailOtp", "/user/changePassword", "/user/getAddressListOnSignUp", "/user/emailNotification",
                                        "/user/login", "/user/logins", "/Film/getProfessionList", "/Film/getProfessionMapList", "/industryUser/getTemporaryDuplicateDetails",
                                        "/user/refreshToken", "/user/forgotPassword", "/admin/adminRegister", "/admin/updateRegister", "/user/getNewAddressListOnSignUp",
                                        "/user/changeUserPassword", "/user/verifyUser", "/admin/deleteRegister", "/admin/getRegister",
                                        "/user/verify", "/user/resendOtp", "/user/verifyForgotOtp", "/industryUser/addIndustryUserPermanentDetails",
                                        "/api/printName", "/industryUser/getDetails", "/industryUser/addTemporaryDetails","/user/saveCoverPhotos",
                                        "/industryUser/getTemporaryDetails", "/industryUser/addIndustryUserPermanentDetails","/user/saveProfilePhotos",
                                        "/industryUser/saveIndustryUserFiles", "/industryUser/updateTemporaryDetails","/user/updateUserFlag","/industryUser/saveOneMinuteVideo","/user/updateRerferrralcode","/industryUser/saveGovermentIdProof","/api/shooting-location/types",
                                        "/payment/payment-failure", "/payment/payment-success","/deeplink/**","/retry-payment/**", "/audition-post/**",  "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/v2/api-docs",   // for old swagger
                                        "/swagger-resources/**",
                                        "/webjars/**","/audition/payment-failure/**", "/audition/payment-success/**","/deeplink/**","/retry-payment/**","/audition/retry-payment/**", "/audition-post/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Allow only these origins
        configuration.setAllowedOrigins(List.of(
            "https://www.filmhookapps.com",  // Production
            "http://localhost:3000"          // Local React or similar dev server
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
  

}
