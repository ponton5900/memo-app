package com.example.hello_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()//csrfチェックを完全に無効化
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()  // すべて認証が必要
            )
            .formLogin()//フォームログインを有効か
            .defaultSuccessUrl("/",true)//ログイン成功後
            .and()
            .logout()
            .logoutSuccessUrl("/login");//ログアウト後の遷移

        return http.build();
    }
}

