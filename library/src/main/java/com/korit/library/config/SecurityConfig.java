package com.korit.library.config;

import com.korit.library.security.PrincipalOAuth2DetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOAuth2DetailsService principalOAuth2DetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic().disable();
        http.authorizeRequests()
                .antMatchers("/mypage/**", "security/**")
                .authenticated()
                .antMatchers("/admin/**")
                .hasRole("ADMIN")   // ROLE_ADMIN, ROLE_MANAGER
                .anyRequest()
                .permitAll()
                // 일반 회원가입 해서 로그인하는것
                .and()
                .formLogin()
                .loginPage("/account/login") // 로그인페이지 get요청
                .loginProcessingUrl("/account/login") // 로그인 인증 post 요청
                .failureForwardUrl("/account/login/error") // Forward는 앞의 요청과 같아야 함 (앞에 로그인 요청이 post 요청이기때문에 post 요청을 보내야 한다)
                // sns로 로그인하는것
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(principalOAuth2DetailsService)
                .and()
                .defaultSuccessUrl("/index");



//                .defaultSuccessUrl() // 로그인 성공했을때 보낼 url
//                .successForwardUrl() //
        // 직접 로그인페이지로 이동한것과 시큘리티가 강제로 로그인페이지로 이동했을때의 차이점
        // defaultSuccessUrl() = 직접 로그인페이지로 이동했을때 로그인 성공한경우 () 로 보낸다
        // 시큘리티에 걸려서 로그인페이지로 이동한경우 원래 요청을 날렷던 곳으로 보낸다
        // successForwardUrl() = 로그인 성공했을때 () 로 가라
        // failureForwardUrl() = 로그인 실패했을때 무조건 () 로 가라
    }
}
