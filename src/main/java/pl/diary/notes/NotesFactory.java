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



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import michal.szymanski.util.Date;

/**
 *
@author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */

public class NotesFactory {


    public static Set<Note> createMaxAvaibleDaysMonthMockNotes(int year, int month){
        int maxDays = Date.getLastAvaibleDayInMonth(year, month);
        List<Note> result = new ArrayList();
        for(int i = 1; i < maxDays+1; i++){
            Note note = createEmptyNote(year, month, i);
           result.add(note);
        }
        Collections.reverse(result);
        Set resultSet = new LinkedHashSet();
        resultSet.addAll(result);
        return resultSet;
    }

    public static Note createEmptyNote(int year, int month, int day){
          Note note = new Note.Builder()
                    .content("")
                    .title("")
                    .creationDate(year, month, day)
                    .dateTime(LocalDateTime.now())
                    .build();
          return note;
    }



}
