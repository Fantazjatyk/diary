/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.authentication;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class UserStatistics {

    private int totalNotes;
    private int notesInThisMonth;

    public int getTotalNotes() {
        return totalNotes;
    }

    public void setTotalNotes(int totalNotes) {
        this.totalNotes = totalNotes;
    }

    public int getNotesInThisMonth() {
        return notesInThisMonth;
    }

    public void setNotesInThisMonth(int notesInThisMonth) {
        this.notesInThisMonth = notesInThisMonth;
    }

    
}
