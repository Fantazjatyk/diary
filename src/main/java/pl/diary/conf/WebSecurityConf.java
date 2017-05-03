/*
 * The MIT License
 *
 * Copyright 2017 Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import pl.diary.authentication.UserCleanerLogoutSuccesHandler;

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

                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID").invalidateHttpSession(true).logoutSuccessHandler(new UserCleanerLogoutSuccesHandler()).permitAll();

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