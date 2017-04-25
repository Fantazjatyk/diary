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
