/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserResolver {

    @Autowired
    UserDetailsExtractor detailsExtractor;

    private static User loggedUser;

    private static UserResolver instance;

    public static UserResolver getInstance() {
        return instance;
    }

    public UserResolver() {
        instance = this;
    }

    public Optional<User> getLoggedUser() {
        String id = null;
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();

        if (loggedUser != null) {
            if (auth != null && auth.isAuthenticated()) {
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
