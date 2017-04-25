/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.diary.authentication.UserResolver;
import pl.diary.dao.user.UserStatisticsExtractor;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class User {

    @RequestMapping("/user_options")
    public String getUserOptions(){
        return "user_options";
    }

    @Autowired
    UserStatisticsExtractor statistics;

    @RequestMapping("/user_options/summary")
    public String getSummary(Model model){
        pl.diary.authentication.User user = UserResolver.getInstance().getLoggedUser().get();
        model.addAttribute("user", user);
        model.addAttribute("userStats", statistics.getUserStatistics(user.getId()));
        return "summary";
    }

     @RequestMapping("/user_options/settings")
    public String getSettings(){
        return "settings";
    }

        @RequestMapping("/login")
    public String login(){
        return "welcome";
    }
}
