package com.eyoung.springbootadmin.security.config;

import com.eyoung.springbootadmin.security.core.LoginValidateAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginValidateAuthenticationProvider loginValidateAuthenticationProvider;

    /**
     * 配置受保护的url
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 匹配为 "/","/index","/login","/login-error","/401","/css/**","/js/**" 均无需登录授权即可访问
                .antMatchers(
                        "/",
                        "/index",
//                        "/login",
                        "/admin-login",
                        "/wechat-login",
                        "/mobile-auth",
//                        "/wechat/polling",
//                        "/wechat/wxLoginPage",
//                        "/wechat/pcAuth",
                        "/wechat/**",
                        "/endpointChat",
                        "/endpointChat/**",
                        "/pages/admin-404",
                        "/pages/admin-500",
                        "/pages/admin-blank",
                        "/login-error",
                        "/401",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/plugins/**",
                        "/webjars/**",
                        "/pages/include.html"
                ).permitAll()
                // 其他非匹配的url均需求授权方可访问
                .anyRequest()
                .authenticated()
                .and()
                //表单登录
                .formLogin()
                //登录页面
                .loginPage("/admin-login")
                // 点击登录跳转调用的登录接口
                //.loginProcessingUrl("")
                // 登录成功后跳转的url
                .defaultSuccessUrl("/pages/admin-index")
                //登录失败页面
                //.failureUrl("/login-error")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/401");
        http.logout()
                .logoutSuccessUrl("/admin-login");
//                .logoutSuccessUrl("/pages/admin-login");
        // 默认是启用的，需要禁用CSRF保护，防止出现csrf攻击
        http.csrf().disable().cors().disable();
//        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        http.rememberMe().rememberMeParameter("remember-me").rememberMeCookieName("remember-me").tokenValiditySeconds(60 * 60 * 24);
        http.headers().frameOptions().sameOrigin().disable();
    }

    /**
     * Override this method to configure {@link WebSecurity}. For example, if you wish to
     * ignore certain requests.
     *
     * @param web
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/img/**", "/plugins/**","/webjars/**")
        .antMatchers("/*.html","/*.js.map","/**favicon.ico");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里要设置自定义认证
        auth.authenticationProvider(loginValidateAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            //对密码进行加密
            @Override
            public String encode(CharSequence charSequence) {
                System.out.println(charSequence.toString());
                return DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
            }
            //对密码进行判断匹配
            @Override
            public boolean matches(CharSequence charSequence, String s) {
                String encode = DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
                boolean res = s.equals(encode);
                return res;
            }
        };
    }
}
