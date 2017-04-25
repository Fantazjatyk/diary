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
package pl.diary.notes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.stereotype.Component;
import pl.diary.utils.Encryption;

/**
 *
 * @author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@Component
public class NoteMapper implements RowMapper<Note> {

    @Autowired
    Encryption encryption;

    @Override
    public Note mapRow(ResultSet rs, int i) throws SQLException {
        LocalDate creation = getCreationTime(rs);
        LocalDateTime modification = getModificationDateTime(rs);
        Note note = new Note.Builder()
                .content(encryption.decryptText(rs.getString("content")))
                .title(rs.getString("title"))
                .dateTime(modification)
                .userId(rs.getString("user_id"))
                .creationDate(creation.getYear(), creation.getMonthValue(), creation.getDayOfMonth())
                .build();
        return note;
    }

    private LocalDate getCreationTime(ResultSet set) throws SQLException {
        LocalDate data = set.getDate("date").toLocalDate();
        return data;
    }

    private LocalDateTime getModificationDateTime(ResultSet rs) throws SQLException {
        LocalDateTime dateTime;
        LocalDate date = getModificationDate(rs);
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        LocalTime time = getModificationTime(rs);
        int hour = time.getHour();
        int seconds = time.getSecond();
        int minute = time.getMinute();
        dateTime = LocalDateTime.of(year, month, day, hour, minute, seconds);
        return dateTime;
    }

    private LocalDate getModificationDate(ResultSet rs) throws SQLException {
        LocalDate date = null;
        date = rs.getDate("modification_date").toLocalDate();
        return date;
    }

    private LocalTime getModificationTime(ResultSet rs) throws SQLException {
        LocalTime time = null;
        String t = rs.getTime("modification_time").toString();
        time = LocalTime.parse(t);

        return time;
    }

}
