/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.conf;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CompositeFilter;
import pl.diary.authentication.OAuth2AuthenticationDecorator;

/**
 *
 * @author MichaĹ‚ SzymaĹ„ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@Configuration
@SpringBootApplication
@EnableOAuth2Client
public class WebSecurityConf extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/login**", "/signin**", "/resources/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .and()

                .addFilterBefore(getSignUpsFilters(), BasicAuthenticationFilter.class)
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login").permitAll();

    }

    private CompositeFilter getSignUpsFilters(){
        CompositeFilter composite = new CompositeFilter();
        List<Filter> filters = new ArrayList();
        filters.add(getFacebookFilter());
        filters.add(getGoogleFilter());
        composite.setFilters(filters);
        return composite;
    }
    private Filter getFacebookFilter() {
        OAuth2ClientAuthenticationProcessingFilter facebookFilter
                = new  OAuth2ClientAuthenticationProcessingFilter("/signin/facebook");
        OAuth2RestTemplate template = new OAuth2RestTemplate(facebook(), oauth2ClientContext);
        OAuth2AuthenticationDecorator decorator = new OAuth2AuthenticationDecorator();
        decorator.setServerHost("facebook.pl");
       facebookFilter.setAuthenticationSuccessHandler(decorator);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(facebookResource().getUserInfoUri(),facebook().getClientId());
        tokenServices.setRestTemplate(template);
        facebookFilter.setRestTemplate(template);
        facebookFilter.setTokenServices(tokenServices);
        return facebookFilter;
    }

    private Filter getGoogleFilter(){
        OAuth2ClientAuthenticationProcessingFilter googleFilter = new OAuth2ClientAuthenticationProcessingFilter("/signin/google");
        OAuth2RestTemplate template = new OAuth2RestTemplate(google(), oauth2ClientContext);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(googleResources().getUserInfoUri(), google().getClientId());
        tokenServices.setRestTemplate(template);
        googleFilter.setRestTemplate(template);
        googleFilter.setTokenServices(tokenServices);


                OAuth2AuthenticationDecorator decorator = new OAuth2AuthenticationDecorator();
        decorator.setServerHost("google.pl");
       googleFilter.setAuthenticationSuccessHandler(decorator);
        return googleFilter;
    }

    @Bean
    @ConfigurationProperties("google.client")
    public AuthorizationCodeResourceDetails google(){
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        return details;
    }

    @Bean
    @ConfigurationProperties("google.resource")
    public ResourceServerProperties googleResources(){
        return new ResourceServerProperties();
    }
    @Bean
    @ConfigurationProperties("facebook.client")
    public AuthorizationCodeResourceDetails facebook() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("facebook.resource")
    public ResourceServerProperties facebookResource() {
        return new ResourceServerProperties();
    }

@Bean
public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter){
    FilterRegistrationBean regBean = new FilterRegistrationBean();
    regBean.setFilter(filter);
    regBean.setOrder(-100);
  return regBean;

}
}