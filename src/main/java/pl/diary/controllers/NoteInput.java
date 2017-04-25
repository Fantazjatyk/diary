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

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.diary.authentication.User;
import pl.diary.authentication.UserResolver;
import pl.diary.exceptions.NoContentException;
import pl.diary.exceptions.NoteValidationException;
import pl.diary.dao.note.NoteCreator;
import pl.diary.dao.note.NoteResolver;
import pl.diary.dao.note.NoteUpdater;
import pl.diary.notes.Note;
import pl.diary.notes.Notes;

/**
 *
 * @author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class NoteInput {

    @Autowired
    Validator validator;

    @Autowired
    NoteCreator creator;

    @Autowired
    NoteResolver resolver;

    @Autowired
    NoteUpdater updater;

    ResponseEntity response;

    @RequestMapping(value = "/notes/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void handleNoteChange(@RequestBody Note note) throws NoContentException, ClassNotFoundException, SQLException, Exception {

        Optional<User> user = UserResolver.getInstance().getLoggedUser();
        Set errors = validator.validate(note);
        if (errors.isEmpty() && user.isPresent()) {
            note.setAuthorId(user.get().getId());
            if (note.getTitle().isEmpty()) {
                Notes.nameNoteByContent(note);
            }

            if (resolver.getNoteId(note) == 0) {

                creator.insertNote(note);
            } else {
                updater.pushNote(note);
            }
        } else {
            throw new NoteValidationException(errors);
        }

    }

    @ExceptionHandler(NoteValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    List<String> handleDiaryException(NoteValidationException e) {
        return e.getErrors();
    }
}
