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
package pl.diary.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import pl.diary.dao.user.UserDetailsExtractor;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Component
@Scope(value="request", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class UserResolver {

    @Autowired
    UserDetailsExtractor detailsExtractor;

    private User loggedUser;

    private boolean isAuthienticated = true;


    public void logout() {
        this.isAuthienticated = false;
    }

    public boolean isAuthenticated() {
        return this.isAuthienticated;
    }



    public Optional<User> getLoggedUser() {
        String id = null;
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();

        if (loggedUser != null) {
            if (auth != null && this.isAuthenticated()) {
                return Optional.of(loggedUser);
            }
        }

        Authentication deepAuth;
        User user = null;
        if (auth != null && (deepAuth = auth.getUserAuthentication()) != null) {
            HashMap map = (HashMap) auth.getUserAuthentication().getDetails();
            user = new User((String) map.get("name"), getId(map));
            user.setServerHost((String) map.get("server_host"));
            user.setAvatarUrl(getAvatarUrl(user, map));
            addUserDetails(user, map);

        }
        loggedUser = user;

        return Optional.ofNullable(user);
    }

    public void addUserDetails(User user, Map map) {
        Map details = detailsExtractor.getDetails(getId(map));

        if (details != null && details.isEmpty()) {
            user.getUserDetails().putAll(details);
        }
    }

    private String getAvatarUrl(User user, Map details) {
        String url = "";
        switch (user.getServerHost()) {
            case "facebook.pl":
                url = "http://graph.facebook.com/";
                url += user.getId() + "/picture";
                break;
            case "google.pl":
                url = (String) details.get("picture");
                break;
        }
        ;
        return url;
    }

    private static String getId(Map details) {
        String id = "";
        switch ((String) details.get("server_host")) {
            case "facebook.pl":
                id = (String) details.get("id");
                break;
            case "google.pl":
                id = (String) details.get("sub");
                break;
        }
        ;
        return id;
    }
}
