/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.authentication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class OAuth2AuthenticationDecorator extends SavedRequestAwareAuthenticationSuccessHandler{

    private Map params = new HashMap();

    public void setServerHost(String oauthServerHost) {
        this.params.put("server_host", oauthServerHost);
    }

    public Map getDecoratorParams() {
        return params;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2Authentication auth = (OAuth2Authentication) authentication;
        Object details = auth.getUserAuthentication().getDetails();

        if(details instanceof Map){
            ((HashMap)details).putAll(params);
        }
        super.onAuthenticationSuccess(request, response, authentication); //To change body of generated methods, choose Tools | Templates.
    }



}
