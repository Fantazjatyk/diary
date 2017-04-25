/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.dao.user;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.diary.authentication.UserStatistics;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Repository
public class UserStatisticsExtractor {

    @Autowired
    NamedParameterJdbcTemplate template;

    public UserStatistics getUserStatistics(String id){
        String totalNotes = "select * from notes where user_id = :user_id";
        String notesInThisMonth = "select * from notes where user_id = :user_id and YEAR(date) = :year and MONTH(date) = :month";

        Map params = getParams(id);
        int totalNotesValue = template.queryForList(totalNotes, params).size();
        int notesInThisMonthValue = template.queryForList(notesInThisMonth, params).size();

        UserStatistics statistics = new UserStatistics();
        statistics.setTotalNotes(totalNotesValue);
        statistics.setNotesInThisMonth(notesInThisMonthValue);
        return statistics;
    }

    private Map getParams(String id){
        int thisMonth = LocalDate.now().getMonthValue();
        int thisYear = LocalDate.now().getYear();
        String thisMonthFormatted  = String.format("%02d", thisMonth);
        Map map = new HashMap();
        map.put("user_id", id);
        map.put("year", thisYear);
        map.put("month", thisMonthFormatted);
        return map;
    }
}
