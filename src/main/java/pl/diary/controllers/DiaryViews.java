/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import michal.szymanski.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.diary.authentication.User;
import pl.diary.authentication.UserResolver;
import pl.diary.json.ClientInitData;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class DiaryViews {

    @Autowired
    ObjectMapper mapper;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getDiary(@RequestParam(name = "year", required = false, defaultValue = "0") int year, @RequestParam(name = "month", required = false, defaultValue = "0") int month,
            @RequestParam(name = "day", required = false, defaultValue = "0") int day,
            Model model, HttpServletRequest rq) throws JsonProcessingException {

        if (year == 0) {
            year = LocalDate.now().getYear();
        }
        if (month == 0) {
            month = LocalDate.now().getMonthValue();
        }
        if (day == 0) {
            day = LocalDate.now().getDayOfMonth();
        }

        ClientInitData initJson = new ClientInitData();
        initJson.month = month;
        initJson.year = year;
        initJson.day = day;
        initJson.LAST_DAY_IN_MONTH = Date.getArrayOfLastAvaibleDaysInMonths(year);

        String json = mapper.writeValueAsString(initJson);
        model.addAttribute("initJson", json);

        User user = UserResolver.getInstance().getLoggedUser().get();
        model.addAttribute("user", user);
        model.addAttribute("avatar", user.getAvatarUrl());
        return "diary";
    }



    @RequestMapping(value = "/main")
    public String getMainView() {
        return "diary_main";
    }

    @RequestMapping(value = "/searcher")
    public String getNotesSearche() {
        return "diary_searcher";
    }

}
