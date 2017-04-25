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
package pl.diary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.diary.authentication.User;
import pl.diary.authentication.UserResolver;
import pl.diary.dao.note.NoteResolver;
import pl.diary.notes.Note;
import pl.diary.notes.NotesFactory;

/**
 *
 * @author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class NoteOutput {

    @Autowired
    NoteResolver resolver;

    @Autowired
    ObjectMapper mapper;

    @ResponseBody
    @RequestMapping(value = "/notes/get", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String getNote(@RequestParam int year, @RequestParam int month, @RequestParam int day) throws JsonProcessingException {

        Optional<User> user = UserResolver.getInstance().getLoggedUser();

        if (!user.isPresent()) {
            return null;
        }
        Note note;
        note = resolver.getNote(user.get().getId(), year, month, day);

        String result = mapper.writeValueAsString(note);

        return result;
    }

    @Autowired
    NotesFactory factory;

    @ResponseBody
    @RequestMapping(value = "/notes/get_notes", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String getNotNullInMonth(@RequestParam int year, @RequestParam int month) throws JsonProcessingException, SQLException, ClassNotFoundException {

        Set<Note> notes = factory.createFullMonthNotes(UserResolver.getInstance().getLoggedUser().get().getId(), year, month);

        return mapper.writeValueAsString(notes);
    }

    @RequestMapping(value = "/notes/search_title", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String searchForNotesByTitles(@RequestParam String title) throws JsonProcessingException {

        String json = mapper.writeValueAsString(resolver.getNotesByTitle(UserResolver.getInstance().getLoggedUser().get().getId(), title));
        return json;
    }

}
