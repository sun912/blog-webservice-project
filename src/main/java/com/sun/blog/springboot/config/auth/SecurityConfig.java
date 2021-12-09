package com.sun.blog.springboot.config.auth;

import com.sun.blog.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity      //Spring security conf. 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    //URL별 관리를 설정하는 옵션의 시작점
                    .authorizeRequests()
                    //권한 관리 대상을 지정하는 옵션(URL, HTTP method 별로 관리가 가능함)
                    //특정 URL에는 permitAll, /api/v1/**는 USER 에게만 퍼밋
                    .antMatchers("/", ".css.**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                    //이외 나머지 URL 경우 로그인한 사용자들은 모두 퍼밋 처리
                    .anyRequest().authenticated()
                .and()
                    .logout()
                    .logoutSuccessUrl("/")          //로그아웃시 "/" 로 이동
                .and()
                    // 로그인 기능 설정
                    .oauth2Login()
                        //OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정 담당
                        .userInfoEndpoint()
                            .userService(customOAuth2UserService);
    }
}
