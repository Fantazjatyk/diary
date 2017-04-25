/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.authentication;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class User {

    private String id;
    private String name;
    private String avatarUrl;
    private String authServerHost;
    private UserDetails details = new UserDetails();

    public User(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public UserDetails getUserDetails() {
        return details;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setServerHost(String authServerHost) {
        this.authServerHost = authServerHost;
    }

    public String getServerHost() {
        return authServerHost;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
