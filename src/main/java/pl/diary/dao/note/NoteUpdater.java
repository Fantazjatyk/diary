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

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.diary.utils.Encryption;
import pl.diary.notes.Note;
import pl.diary.notes.NoteDateFormatter;

/**
 *
 * @author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@Repository
public class NoteUpdater {

    @Autowired
    NoteResolver resolver;

    @Autowired
             @Qualifier("baza")
    NamedParameterJdbcTemplate template;

    @Autowired
    Encryption encryption;

    public void pushNote(Note note) {
        String statement = "update notes_details set modification_time = :modification_time,"
                + " modification_date = :modification_date where note_id = :note_id";
        String statement1 = "update notes_contents set content = :content, title = :title where note_id = :note_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("modification_time", LocalTime.now().toString());
        params.addValue("modification_date", LocalDate.now().toString());
        params.addValue("title", note.getTitle());
        params.addValue("content", encryption.encryptText(note.getContent()));
        params.addValue("date", NoteDateFormatter.formatDate(note.getYear(), note.getMonth(), note.getDay()));
        params.addValue("note_id", resolver.getNoteId(note));
        params.addValue("user_id", note.getAuthorId());
        template.update(statement, params);
        template.update(statement1, params);
    }

}
