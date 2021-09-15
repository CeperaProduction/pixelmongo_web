package ru.pixelmongo.pixelmongo.configs;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.context.annotation.RequestScope;

import ru.pixelmongo.pixelmongo.handlers.AuthHandler;
import ru.pixelmongo.pixelmongo.handlers.impl.AuthHandlerImpl;
import ru.pixelmongo.pixelmongo.model.AnonymousUser;
import ru.pixelmongo.pixelmongo.model.UserDetails;
import ru.pixelmongo.pixelmongo.model.dao.User;
import ru.pixelmongo.pixelmongo.services.UserService;
import ru.pixelmongo.pixelmongo.utils.MD5PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Value( "${spring.security.rememberme.key}" )
    private String rememberMeKey;

    @Autowired
    private UserService userService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        /* This password encryption method is old and weak, but we must use it
        for compability with our other old systems.  */
        return new MD5PasswordEncoder(2);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setCreateTableOnStartup(false);
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        PersistentTokenBasedRememberMeServices services
            = new PersistentTokenBasedRememberMeServices(
                    rememberMeKey, userDetailsService(), tokenRepository());
        services.setAlwaysRemember(true);
        services.setCookieName("remember-me");
        services.setParameter("remember-me");
        services.setSeriesLength(16);
        services.setTokenLength(16);
        //services.setUseSecureCookie(true);
        return services;
    }

    @Bean
    public AuthHandler authHandler() {
        return new AuthHandlerImpl();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()

            .antMatchers("/admin/users/**").hasAuthority("admin.panel.users")
            //Users controller handles edit permissions by itself.
            //It lets user manage his own profile.

            .antMatchers(HttpMethod.POST, "/admin/groups/**").hasAuthority("admin.panel.groups.edit")
            .antMatchers(HttpMethod.DELETE, "/admin/groups/**").hasAuthority("admin.panel.groups.edit")
            .antMatchers("/admin/groups/new").hasAuthority("admin.panel.groups.edit")
            .antMatchers("/admin/groups/**").hasAuthority("admin.panel.groups")

            .antMatchers("/admin/rules/**").hasAuthority("admin.panel.rules")

            .antMatchers("/admin/logs/**").hasAuthority("admin.panel.logs")

            .antMatchers("/admin/**").hasAuthority("admin.panel.access")

            .anyRequest().permitAll()

        .and()
            .exceptionHandling()
            .authenticationEntryPoint(authHandler())
        .and()
            .formLogin()
                .usernameParameter("login")
                .passwordParameter("password")
                .loginProcessingUrl("/auth/login")
                .loginPage("/auth/login-required")
                .failureHandler(authHandler())
                .successHandler(authHandler())
        .and()
            .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler(authHandler())
                .deleteCookies("JSESSIONID", "remember-me")
        .and()
            .rememberMe().rememberMeServices(rememberMeServices());

    }



    @Bean
    @RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    public User user() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userDetails = auth.getPrincipal();
        Optional<User> optUser = Optional.empty();
        if(userDetails instanceof UserDetails) {
            optUser = userService.getUser((UserDetails) userDetails);
        }
        return optUser.orElse(AnonymousUser.getInstance());
    }

}
