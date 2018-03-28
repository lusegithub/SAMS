package com.jacky.sams.config;

import com.jacky.sams.service.CustomUserService;
import com.jacky.sams.util.MyPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    UserDetailsService customUserService() {
        return new CustomUserService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService()).passwordEncoder(new MyPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(new String[]{"/student/signup","/bootstrap-table/**","/webjars/**","/static/**","/**/favicon.ico"}).permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login").successForwardUrl("/dispatch").permitAll()
                .and().rememberMe().tokenValiditySeconds(12009600).key("mykey")
                .and().logout().permitAll();
    }
}
