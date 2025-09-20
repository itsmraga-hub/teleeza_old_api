package com.teleeza.wallet.teleeza.common.config;

import com.teleeza.wallet.teleeza.authentication.teleeza.security.CustomUserDetailsService;
import com.teleeza.wallet.teleeza.authentication.teleeza.security.JwtAuthenticationEntryPoint;
import com.teleeza.wallet.teleeza.authentication.teleeza.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {"com.teleeza.wallet.teleeza"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    public CustomUserDetailsService customUserDetailsService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/v1/user-auth/**").permitAll()
                .antMatchers("/v1/user-auth/signin").permitAll()
                .antMatchers("/v1/user-auth/signup").permitAll()
                .antMatchers("/v1/user-auth/password-reset-request").permitAll()
                .antMatchers("/v1/user-auth/reset-password").permitAll()
                .antMatchers("/v1/teleeza-wallet/register-customer").permitAll()
                .antMatchers("/v1/teleeza-wallet/customer-confirmation").permitAll()
                .antMatchers("/v1/teleeza-wallet/resend-otp").permitAll()
//                .antMatchers("/v1/teleeza-wallet/registered-customer").permitAll()
                .antMatchers("/v1/teleeza-wallet/airtime-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/bank-transfer-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/paybills-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/c2c-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/kplc-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/lip-fare-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/till-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/cashout-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/validation").permitAll()
                .antMatchers("/v1/merchant-to-beneficiarry/subscription-discount/validation").permitAll()
                .antMatchers("/v1/merchant-to-beneficiarry/subscription-cashback/validation").permitAll()
                .antMatchers("/v1/merchant-to-beneficiarry/residual-income/validation").permitAll()
                .antMatchers("/v1/merchant-to-beneficiarry/validation").permitAll()
                .antMatchers("/v1/merchant-to-beneficiarry/top-earners").permitAll()
                .antMatchers("/v1/teleeza-wallet/mobilemoney-transfer-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/postpaid-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/freemium-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/subscription-extension-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/subscription-plans").permitAll()
                .antMatchers("/v1/teleeza-wallet/tv-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/subscription-organisations").permitAll()
                .antMatchers("/v1/teleeza-wallet/fcm-token").permitAll()
                .antMatchers("/v1/teleeza-wallet/reversal-validation").permitAll()
                .antMatchers("/v1/teleeza-wallet/paybill-reversal-validation").permitAll()
                .antMatchers("/v1/teleeza/counties").permitAll()
                .antMatchers("/v1/teleeza/locations").permitAll()
                .antMatchers("/v1/teleeza-wallet/agents").permitAll()
                .antMatchers("/v1/teleeza-wallet/registration-callback").permitAll()
                .antMatchers("/v1/teleeza-wallet/upload-direcly-to-s3").permitAll()
                .antMatchers("/mobile-money/token").permitAll()
                .antMatchers("/mobile-money/register-url").permitAll()
                .antMatchers("/mobile-money/simulate-c2b").permitAll()
                .antMatchers("/mobile-money/b2c-transaction").permitAll()
                .antMatchers("/mobile-money/validation").permitAll()
                .antMatchers("/mobile-money/confirmation").permitAll()
                .antMatchers("/mobile-money/transaction-result").permitAll()
                .antMatchers("/mobile-money/stk-transaction-callback").permitAll()
                .antMatchers("/mobile-money/stk-transaction-request").permitAll()
                .antMatchers("/mobile-money/check-account-balance").permitAll()
                .antMatchers("/v1/teleeza/rewarded/rewarded-ads-transaction-result").permitAll()
                .antMatchers("/v1/merchant/api/companies").permitAll()
                .antMatchers("/api/v1/freemium-calculator").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("Teleeza")
                .password("d53b0dcb88cdf182a9817737e99ea833869fc3e9")
                .roles("USER");
    }

//    @Autowired
//    public InMemoryUserDetailsManager userDetailsManager() {
//        UserDetails userDetails = User.withUsername("Teleeza")
//                .password("d53b0dcb88cdf182a9817737e99ea833869fc3e9")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(userDetails);
//    }
}
