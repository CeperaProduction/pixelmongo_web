package ru.pixelmongo.pixelmongo.configs;

import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.annotation.RequestScope;

import ru.pixelmongo.pixelmongo.handlers.AuthHandler;
import ru.pixelmongo.pixelmongo.handlers.impl.AuthHandlerImpl;
import ru.pixelmongo.pixelmongo.model.AnonymousUser;
import ru.pixelmongo.pixelmongo.model.UserDetails;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.repositories.internal.UserDetailsValidCheckHttpSessionSecurityContextRepository;
import ru.pixelmongo.pixelmongo.services.UserService;
import ru.pixelmongo.pixelmongo.services.impl.UserServiceImpl;
import ru.pixelmongo.pixelmongo.utils.MD5PasswordEncoder;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:security.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Value( "${spring.security.rememberme.key}" )
    private String rememberMeKey;

    @Value( "${spring.security.rememberme.cookie}" )
    private String rememberMeCookie;

    @Value( "${spring.security.rememberme.param}" )
    private String rememberMeParam;

    @Value( "${spring.security.rememberme.secured}" )
    private boolean rememberMeSecure;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UploadConfig uploadCfg;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService()).passwordEncoder(passwordEncoder());
    }

    @Override
    public UserDetailsService userDetailsService() {
        return userService();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl() {

            @Override
            public User getCurrentUser() {
                return user();
            }
        };
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
    public AbstractRememberMeServices rememberMeServices() {
        PersistentTokenBasedRememberMeServices services
            = new PersistentTokenBasedRememberMeServices(
                    rememberMeKey, userDetailsService(), tokenRepository());
        services.setAlwaysRemember(true);
        services.setCookieName(rememberMeCookie);
        services.setParameter(rememberMeParam);
        services.setSeriesLength(16);
        services.setTokenLength(16);
        services.setUseSecureCookie(rememberMeSecure);
        return services;
    }

    @Bean
    public AuthHandler authHandler() {
        return new AuthHandlerImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new UserDetailsValidCheckHttpSessionSecurityContextRepository(
                userService(),
                rememberMeServices());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/css/**", "/js/**", "/fonts/**", "/img/**", "/favicon.ico",
                "/open/**", uploadCfg.getUploadUrl()+"/**"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .securityContext().securityContextRepository(securityContextRepository())
        .and()
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

            .antMatchers("/admin/playerlogs/**").hasAuthority("admin.panel.playerlogs")

            .antMatchers("/admin/monitoring/**").hasAuthority("admin.panel.monitoring")

            .antMatchers("/admin/donate/pages/**").hasAuthority("admin.panel.donate.content")
            .antMatchers("/admin/donate/servers/**").hasAuthority("admin.panel.donate.servers")
            .antMatchers("/admin/donate/discount/**").hasAuthority("admin.panel.donate.discount")
            .antMatchers("/admin/donate/balance/**").hasAuthority("admin.panel.donate.balances")
            .antMatchers("/admin/donate/give/**").hasAuthority("admin.panel.donate.give")
            .antMatchers("/admin/donate/query/**").hasAuthority("admin.panel.donate.logs")
            .antMatchers("/admin/donate/**").hasAuthority("admin.panel.donate")

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
                .deleteCookies(rememberMeCookie)
        .and()
            .rememberMe().rememberMeServices(rememberMeServices())
        .and()
            .sessionManagement()
                .maximumSessions(-1)
                .sessionRegistry(sessionRegistry())
            .and()
        .and();

    }



    @Bean
    @RequestScope
    public User user() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userDetails = auth.getPrincipal();
        Optional<User> optUser = Optional.empty();
        if(userDetails instanceof UserDetails) {
            optUser = userService().getUser((UserDetails) userDetails);
        }
        return optUser.orElse(AnonymousUser.getInstance());
    }

}
