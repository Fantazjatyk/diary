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
package pl.diary.notes;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import michal.szymanski.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.diary.dao.note.NoteResolver;

/**
 *
@author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@Component
public class NotesFactory {

    @Autowired
    NoteResolver resolver;

    public Set<Note> createMaxAvaibleDaysMonthMockNotes(int year, int month){
        int maxDays = Date.getLastAvaibleDayInMonth(year, month);
        List<Note> result = new ArrayList();
        for(int i = 1; i < maxDays+1; i++){
            Note note = new Note.Builder()
                    .content("")
                    .title("")
                    .creationDate(year, month, i)
                    .dateTime(LocalDateTime.now())
                    .build();
           result.add(note);
        }
        Collections.reverse(result);
        Set resultSet = new LinkedHashSet();
        resultSet.addAll(result);
        return resultSet;
    }

    public Set<Note> createFullMonthNotes(String userId, int year, int month){
        Set<Note> mocks = createMaxAvaibleDaysMonthMockNotes(year, month);
        Set<Note> existingNotes = resolver.getNotes(userId, year, month);
        Map<Integer, Note> map = existingNotes.stream().collect(Collectors.toMap((el)->el.getDay(), (el)->el));
        Set result = new LinkedHashSet();


       mocks.iterator().forEachRemaining((el)->{
       if(map.containsKey(el.getDay()))
               result.add(map.get(el.getDay()));
       else{
           result.add(el);
       }

       });

        return result;
    }

}
