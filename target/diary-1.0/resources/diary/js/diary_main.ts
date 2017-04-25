declare var moment: any;
class MainMode extends Mode {

    calendar: Callendar;
    noteHeaders: NoteHeaders;
    noteEdit: NoteEdit;
    container;
    settings;
    initJson;
    calendarChooserMode: CalendarChooserMode;

    constructor(master, initJson) {
        super(master);
        this.settings = initJson;
    }

    init(data) {
  this.start(data);
    }


getUrl(){
    return "main";
}

    start(data) {
        var self = this;
        this.calendarChooserMode = new CalendarChooserMode(this, { containerHolder: "calendar_opener_content", yearHolder: "year-chooser", monthHolder: "month-chooser" });
        this.calendar = new Callendar(document.getElementById("yearView")
            , document.getElementById("monthView")
            , document.getElementById("left_arrow")
            , document.getElementById("right_arrow"),
            this
        );

        this.calendar.load(data.year, data.month, data.day);

        this.noteEdit = new NoteEdit(
            document.getElementById("text_view")
            , document.getElementById("date_view_big")
            , document.getElementById("title_view")
            , document.getElementById("save_note_button")
        );

        this.noteHeaders = new NoteHeaders(
            document.getElementById("list_container"),
            "list_container_list"
            , (note) => self.noteEdit.loadNote(note));

        this.noteHeaders.load(data);

        this.noteEdit.attach(this.noteHeaders);
       

        $("#open_calendar_opener").off().on("click", () => self.loadCalendarChooserMode());



        this.loadDay(data);
    }

    displayDay(data) {
        var self = this;
        $.get(relHttpUrl("notes/get"), { year: data.year, month: data.month, day: data.day })
            .done(function (data) {
                self.noteEdit.loadNote(data);
            });
    }

    loadDay(data) {
        var self = this;
        if (data.day != null) {
            self.displayDay(data);
        }
        else {
            self.loadDefaultLastAvaibleDayInMonth(data);
        }
    }

    loadDefaultLastAvaibleDayInMonth(data) {
        data.day = this.settings.LAST_DAY_IN_MONTH[parseInt(data.month) - 1];
        this.displayDay(data)
    }

    loadCalendarChooserMode() {
        this.calendarChooserMode.start();
    }
}




//Loads year, MONTHS, go to last and next month - and shows its all.
//if day parameter is not null - also loads that day.
// DO NOT TOUCH!!!
// CLOSED!
class Callendar implements ModelAndView {

    year: number;
    month: number;
    day: number;
    minYear = 2000;
    actualYear: number;
    actualMonth: number;
    actualDay: number;

    constructor(public yearView, public monthView, public buttonPreviousMonth, public buttonNextMonth, public master: MainMode) {
        var date = new Date();
        this.actualYear = date.getFullYear();
        this.actualMonth = date.getMonth();
        this.actualDay = date.getDate();
        this.setEventHandlers();
    }

    load(year: number, month: number, day: number) {
        this.year = year;
        this.month = month;
        this.day = day;

        this.yearView.innerHTML = year;
        this.monthView.innerHTML = MONTHS[month];
    }

    setEventHandlers() {
        var self = this;
        $(this.buttonNextMonth).off().on("click", () => {
            self.goToNextMont();
        });
        $(this.buttonPreviousMonth).off().on("click", () => self.goToPreviousMonth());
    }

    goToNextMont() {
        var nextMonth = this.month;
        var nextYear = this.year;

        if (nextMonth < this.actualMonth + 1 || nextYear !== this.actualYear) {

            nextMonth++;
            if (nextMonth === 13) {
                nextMonth = 1;
                nextYear++;
            }
            var self = this;
            this.master.start({ year: nextYear, month: nextMonth });
        }

    }

    goToPreviousMonth() {
        var lastMonth = this.month;
        var lastYear = this.year;

        lastMonth--;
        if (lastMonth > 0 && lastYear >= this.minYear || lastMonth == 0 && lastYear > this.minYear) {

            if (lastMonth === 0 && lastYear != this.minYear) {
                lastYear--;
                lastMonth = 12;
            }
            var self = this;
            this.master.start({ year: lastYear, month: lastMonth });
        }



    }
}

//Simply start MainView again, but with new parameters.
class TimeTraveller {

}

// Do not touch!
//Simply shows note and save it (if there is such need).
// notify note headers (observer) that he should need refresh too.
class NoteEdit {

    constructor(public contentView, public dateView, public titleView, public saveButton) {
        this.setEventHandlers();
    }

    loadedNote;
    observers: Array<NoteRefreshObserver> = [];

    notify() {
        this.observers.forEach((e) => e.update(this.loadedNote));
    }

    loadNote(note) {
        this.loadedNote = note;
        this.contentView.value = note.content;
        this.titleView.value = note.title;
        this.dateView.innerHTML= note.day + " " + MONTHS[note.month];
    }

    attach(obj: NoteRefreshObserver) {
        this.observers.push(obj);
    }
    setEventHandlers() {
        var self = this;
        $(this.saveButton).off().on("click", () => self.saveNote());
    }

    saveNote() {
        var self = this;
        var connectionData = { year: self.loadedNote.year, month: self.loadedNote.month, day: self.loadedNote.day, content: self.contentView.value, title: self.titleView.value };

        var header = $("meta[name=_csrf_header]").attr("content");
        var csrfKey = $("meta[name=_csrf]").attr("content");

        $.ajax({
            url: relHttpUrl("notes/save"),
            type: "POST",
            data: JSON.stringify(connectionData),
            contentType: 'application/json; charset=utf-8',
            success: function (status) {

                jquery.notify("Done!", { globalPosition: "bottom right", className: 'success' });
                self.notify();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                var message = jqXHR.responseText;

                if (!message)
                    message = "Error";

                jquery.notify(message, { globalPosition: "bottom right", className: 'error' });
                console.log(errorThrown);
            },

            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, csrfKey.toString());
            }
        });

    }
}


// do not touch!
// generates NoteHeaders by supplied arrays of notes;
class NoteHeaders implements ModelAndView, NoteRefreshObserver {

    notesHeadersFactory: NotesHeadersFactory;
    viewHolder;
    viewReplaceable;
    headersCallback;
    loadedNotes;
    avaibleData;
    self;

    constructor(view, viewReplaceable, callback: (note) => any) {
        this.viewHolder = view;
        this.viewReplaceable = viewReplaceable;
        this.headersCallback = callback;
        this.self = this;
    }

    load(data) {
        this.avaibleData = data;
        var self = this;
        $.get(relHttpUrl("notes/get_notes"), {
            year: data.year,
            month: data.month
        }).done(function (result) {
            self.loadedNotes = result;
            self.createNoteHeaders(result);
            var list = self.viewHolder.getElementsByTagName("li");
            self.markListElementAsActive(self.getListElementByAttribute(list, "data-day", data.day));
        });
    }

    update(data) {
        this.load(data);
    }

    createNoteHeaders(notes) {
        var self = this;
        this.viewHolder.replaceChild(new NotesHeadersFactory().generateNotesHeadersList(notes, (note, element: Node) => {
            self.headersCallback(note);
            self.clearAllSelectedElements(document.getElementById(self.viewReplaceable));
            self.markListElementAsActive(element);
        }),
            document.getElementById(this.viewReplaceable));
    }

    public markListElementAsActive(li: Node): void {

        $(li).attr("class", selectedListElementClass);

    }


    public getListElementByAttribute(list, attrName, attrValue): Node {
        var result: Node;

        for (let i = 0; i < list.length; i++) {
            var node: Element = list.item(i);

            if (node.getAttribute(attrName) == attrValue) {
                result = node;
                break;
            }

        }
        return result;
    }

    public getActiveNote(): Node {
        var list: HTMLCollection = this.viewHolder.getElementsByTagName("li");
        var result: Node;
        for (let i = 0; i < list.length; i++) {
            var node: Element = list.item(i);
            if (node.className == selectedListElementClass) {
                result = node;
                break;
            }

        }
        return result;
    }
    public clearAllSelectedElements(list): void {

        var elements = list.getElementsByTagName("li");

        for (let i = 0; i < elements.length; i++) {
            if (elements[i].getAttribute("class") === selectedListElementClass) {
                $(elements[i]).attr("class", unselectedListElementClass);

            }
        }
    }
}

class NotesHeadersFactory {

    notesHeadersHolder: HTMLElement;


    generateNotesHeadersList(notes: Array<any>, callback): HTMLElement {

        console.log("Drawing notes headers for: " + notes);
        var listRoot: HTMLElement = document.createElement("ul");
        listRoot.setAttribute("id", "list_container_list");
        $("#list_container").off();

        let self = this;
        notes.forEach(function (el) {
            listRoot.appendChild(self.generateNoteHeader(el, callback));
        });
        console.log("result is " + listRoot);
        return listRoot;
    }

    generateNoteHeader(note, headerCallback): HTMLLIElement {
        var listPoint = document.createElement("li");
        var date = document.createElement("span");
        var noteTitle = document.createElement("span");

        date.innerHTML = note.day.toString() + " " + MONTHS[note.month];
        noteTitle.innerText = note.title;
        noteTitle.setAttribute("class", "note_title");
        date.setAttribute("class", "note_date");

        listPoint.setAttribute("data-day", note.day.toString());
        listPoint.setAttribute("class", unselectedListElementClass);
        listPoint.setAttribute("id", note.day.toString());
        listPoint.innerHTML = date.outerHTML + noteTitle.outerHTML;

        var self = this;
        var callback = function (event) {
            console.log(this);
            console.log(event);
            headerCallback(note, listPoint);
            ;
        };

        $("#list_container").on("click", "#" + note.day, callback);

        $("#list_container").on("tap", "#" + note.day, callback);

        return listPoint;
    }
};





class CalendarChooserMode {

    containerHolder;
    yearChooser;
    monthChooser;

    mainMode: MainMode;

    constructor(modesManager: MainMode, viewHolders: { containerHolder, yearHolder, monthHolder }) {
        this.containerHolder = document.getElementById(viewHolders.containerHolder);
        this.mainMode = modesManager;
        this.yearChooser = document.getElementById(viewHolders.yearHolder);
        this.monthChooser = document.getElementById(viewHolders.monthHolder);

        var self = this;
        $(self.containerHolder).dialog({
            modal: true, width: "auto", resizeable: false, autoOpen: false, title: "Otwórz kalendarz",
            buttons: [
                {
                    text: "Otwórz",
                    click: function () {
                        if (self.isInputValid()) {
                            self.closeCalendarOpener();
                            self.mainMode.init({ year: self.yearChooser.value, month: self.monthChooser.value });
                        }
                        else {
                            jquery.notify("Time travel not allowed... YET!")
                        }
                    }
                },

            ],
            classes: {"ui-dialog-titlebar": "my-dialog"}
        });
        this.loadDefaultValues();
    }

    start() {
        $(this.containerHolder).dialog("open");
        $("#year-chooser").spinner();
    }
    isInputValid(): boolean {
        var date = new Date();
        var actualYear = date.getFullYear();
        var actualMonth = date.getMonth() + 1;
        return (this.yearChooser.value < actualYear && this.yearChooser.value >= LIMIT_MINIMAL_YEAR || this.yearChooser.value == actualYear && this.monthChooser.value <= actualMonth);
    }

    loadDefaultValues() {
        var date = new Date();
        this.yearChooser.value = date.getFullYear();
        this.monthChooser.selectedIndex = date.getMonth();
    }
    openCalendarOpener() {
        $(this.containerHolder).dialog("open");
    }

    closeCalendarOpener() {
        $(this.containerHolder).dialog("close");
    }

}




































interface NoteRefreshObserver {
    update(data);
}

interface ModelAndView {

}