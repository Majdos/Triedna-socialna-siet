package sk.majo.maturita.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.security.authentication.GooglePrincipalExtractor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security configuration
 *
 * @author Marian Lorinc
 */
@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private AuthorizationCodeResourceDetails authorizationCodeResourceDetails;

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Autowired
    private GooglePrincipalExtractor googlePrincipalExtractor;

    /**
     *
     */
    @Order(1)
    @Configuration
    public class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .csrf().disable()
                    .headers()
                    .defaultsDisabled()
                    .cacheControl();
        }
    }

    @Order(2)
    @Configuration
    @EnableOAuth2Sso
    public class StaticResourcesWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers()
                    .antMatchers("/js/**", "/css/**", "/images/**", "/**/favicon.ico")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/images/public/**").permitAll()
                    .antMatchers("/images/**").authenticated()
                    .antMatchers("/js/**", "/css/**", "/**/favicon.ico").permitAll()
                    .anyRequest().denyAll()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/groups"))
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .and()
                    .addFilterAt(filter(), BasicAuthenticationFilter.class)
                    .csrf().disable()
                    .headers()
                    .defaultsDisabled()
                    .cacheControl();
        }
    }

    @Order(3)
    @Configuration
    @EnableOAuth2Sso
    public class GeneralWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/trieda", "/group/**", "/groups").authenticated()
                    .antMatchers("/spsjm-social/**", "/", "/error",
                            "/google/login", "/google/login/not-allowed").permitAll()
                    .anyRequest().denyAll()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/google/login"))
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .and()
                    .addFilterAt(filter(), BasicAuthenticationFilter.class)
                    .csrf().disable()
                    .headers()
                    .defaultsDisabled()
                    .cacheControl();
        }
    }

    private OAuth2ClientAuthenticationProcessingFilter filter() {
        //Creating the filter for "/google/login" url
        OAuth2ClientAuthenticationProcessingFilter oAuth2Filter = new OAuth2ClientAuthenticationProcessingFilter(
                "/google/login");

        //Creating the rest template for getting connected with OAuth service.
        //The configuration parameters will inject while creating the bean.
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(authorizationCodeResourceDetails,
                oauth2ClientContext);
        oAuth2Filter.setRestTemplate(oAuth2RestTemplate);

        // Setting the token service. It will help for getting the token and
        // user details from the OAuth Service.
        UserInfoTokenServices userInfoTokenServices = new UserInfoTokenServices(resourceServerProperties.getUserInfoUri(),
                resourceServerProperties.getClientId());

        oAuth2Filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler() {
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                SchoolUser user = (SchoolUser) authentication.getPrincipal();
                if (user.isEnabled()) {
                    this.setDefaultTargetUrl("/groups");
                    super.onAuthenticationSuccess(request, response, authentication);
                } else {
                    String targetUrl = "/google/login/not-allowed";
                    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
                    redirectStrategy.sendRedirect(request, response, targetUrl);
                    this.clearAuthenticationAttributes(request);
                }
            }
        });

        userInfoTokenServices.setPrincipalExtractor(googlePrincipalExtractor);
        oAuth2Filter.setTokenServices(userInfoTokenServices);
        return oAuth2Filter;
    }

}
