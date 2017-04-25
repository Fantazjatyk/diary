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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import pl.diary.notes.validation.TitleOrContent;

/**
 *
 * @author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com
 */
@TitleOrContent
public class Note {

    //@Past(year=2000)
    @JsonIgnore
    private LocalDateTime dateTime;
    private String lastModification;
    private int year;
    private int month;
    private int day;
    private String content;
    private String title;
    private String authorId;

    public void setTitle(String title) {
        this.title = title;
    }


    public String getLastModification() {
        return lastModification;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    private Note() {
    }

    public Note(Builder b) {
        this.dateTime = b.dateTime;
        this.content = b.content;
        this.title = b.title;
        this.year = b.year;
        this.month = b.month;
        this.day = b.day;
        this.authorId = b.userId;
        this.lastModification = b.lastModification;
    }

    public static class Builder {

        private LocalDateTime dateTime;
        private String content;
        private String title;
        private int year;
        private int month;
        private int day;
        private String userId;
        private String noteId;
        private String lastModification;

        public Builder dateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            this.lastModification = dateTime.toString();
            return this;
        }

        public Builder content(String c) {
            this.content = c;
            return this;
        }

        public Builder title(String t) {
            this.title = t;
            return this;
        }

        public Builder userId(String id) {
            this.userId = id;
            return this;
        }

        public Builder creationDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
            return this;
        }

        public Note build() {
            return new Note(this);
        }
    }
}
