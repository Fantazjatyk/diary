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
