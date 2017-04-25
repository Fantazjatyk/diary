/*
 * The MIT License
 *
 * Copyright 2017 Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com.
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
package pl.diary.dao.note;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import pl.diary.notes.Note;
import pl.diary.notes.NoteDateFormatter;
import pl.diary.notes.NoteMapper;

/**
 *
 * @author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@Repository
public class NoteResolver {

    @Autowired
    @Qualifier("baza")
    NamedParameterJdbcTemplate jdbc;

    @Autowired
    NoteSearcher searcher;

    public long getNoteId(String userId, int year, int month, int day) {
        String statement = "select note_id from notes where user_id = :user_id and date = :date";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("date", NoteDateFormatter.formatDate(year, month, day));
        params.addValue("user_id", userId);
        SqlRowSet rowSet = jdbc.queryForRowSet(statement, params);
        long result = 0;
        if (rowSet.next()) {
            result = rowSet.getLong("note_id");
        }
        return result;
    }

    public long getNoteId(Note note) {
        return this.getNoteId(note.getAuthorId(), note.getYear(), note.getMonth(), note.getDay());
    }

    @Autowired
    NoteMapper mapper;

    public Note getNote(String userId, int year, int month, int day) {
        String statement = "select DISTINCT * from notes n inner join notes_details nd on n.note_id = nd.note_id inner join notes_contents nc on n.note_id = nc.note_id where n.user_id = :user_id"
                + " and n.date = :date";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("date", NoteDateFormatter.formatDate(year, month, day));
        Note note = null;
        try {
            note = jdbc.queryForObject(statement, params, mapper);
        } catch (EmptyResultDataAccessException ex) {

        }
        return note;
    }

    public Set<Note> getNotes(String userId, int year, int month) {
        String statement = "select DISTINCT * from notes n "
                + "inner join notes_details nd on n.note_id = nd.note_id inner join notes_contents nc on n.note_id = nc.note_id where n.user_id = :user_id and Year(n.date) = :year and Month(n.date) = :month";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("year", year);
        params.addValue("month", month);
        List<Note> result = jdbc.query(statement, params, mapper);
        Set<Note> notes = result.stream().collect(Collectors.toSet());

        return notes;
    }

    public List<Note> getNotesByTitle(String userId, String title) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("title", "%" + title + "%");

        List result = jdbc.query("select DISTINCT * from notes n inner join notes_details nd on n.note_id = nd.note_id inner join notes_contents nc on n.note_id = nc.note_id where n.user_id = :user_id"
                + " and title like :title", params, mapper);

        return result;
    }

}
