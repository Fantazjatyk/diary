/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.notes;

import pl.diary.notes.Note;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class Notes {

    public static Note nameNoteByContent(Note note){
                    String noteContent = note.getContent();
                    note.setTitle(noteContent.length() <= 30 ? noteContent : noteContent.substring(0, 30));
                    return note;
    }
}
