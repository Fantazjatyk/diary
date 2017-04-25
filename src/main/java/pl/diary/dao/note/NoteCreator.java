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
package pl.diary.dao.note;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import pl.diary.utils.Encryption;
import pl.diary.notes.Note;
import pl.diary.notes.NoteDateFormatter;


/**
 *
@author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@Component
public class NoteCreator {


        @Autowired
        Encryption encryption;

        @Autowired
                @Qualifier("baza")
        NamedParameterJdbcTemplate jdbc;

        @Autowired
        NoteResolver resolver;

        public void insertNote(@Valid Note note){
           String statement = "insert into notes (date,user_id) values (:date, :user_id)";
           String statement2 = "insert into notes_details (note_id, modification_time, modification_date)"
                   + "values (:note_id, :modification_time, :modification_date)";
           String statement3 = "insert into notes_contents (note_id, content, title) values (:note_id, :content, :title)";
           MapSqlParameterSource params = mapToParams(note);
           jdbc.update(statement, params);
           params.addValue("note_id", resolver.getNoteId(note));
           jdbc.update(statement2, params);
           jdbc.update(statement3, params);
        }

        private MapSqlParameterSource mapToParams(Note note){
           MapSqlParameterSource source = new MapSqlParameterSource();
           source.addValue("content", encryption.encryptText(note.getContent()));
           source.addValue("modification_time", LocalTime.now().toString());
           source.addValue("modification_date", LocalDate.now().toString());
           source.addValue("date", NoteDateFormatter.formatDate(note.getYear(), note.getMonth(), note.getDay()));
           source.addValue("title", note.getTitle());
           source.addValue("user_id", note.getAuthorId());
           return source;
        }

}
