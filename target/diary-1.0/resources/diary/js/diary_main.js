var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var MainMode = (function (_super) {
    __extends(MainMode, _super);
    function MainMode(master, initJson) {
        var _this = _super.call(this, master) || this;
        _this.settings = initJson;
        return _this;
    }
    MainMode.prototype.init = function (data) {
        this.start(data);
    };
    MainMode.prototype.getUrl = function () {
        return "main";
    };
    MainMode.prototype.start = function (data) {
        var self = this;
        this.calendarChooserMode = new CalendarChooserMode(this, { containerHolder: "calendar_opener_content", yearHolder: "year-chooser", monthHolder: "month-chooser" });
        this.calendar = new Callendar(document.getElementById("yearView"), document.getElementById("monthView"), document.getElementById("left_arrow"), document.getElementById("right_arrow"), this);
        this.calendar.load(data.year, data.month, data.day);
        this.noteEdit = new NoteEdit(document.getElementById("text_view"), document.getElementById("date_view_big"), document.getElementById("title_view"), document.getElementById("save_note_button"));
        this.noteHeaders = new NoteHeaders(document.getElementById("list_container"), "list_container_list", function (note) { return self.noteEdit.loadNote(note); });
        this.noteHeaders.load(data);
        this.noteEdit.attach(this.noteHeaders);
        $("#open_calendar_opener").off().on("click", function () { return self.loadCalendarChooserMode(); });
        this.loadDay(data);
    };
    MainMode.prototype.displayDay = function (data) {
        var self = this;
        $.get(relHttpUrl("notes/get"), { year: data.year, month: data.month, day: data.day })
            .done(function (data) {
            self.noteEdit.loadNote(data);
        });
    };
    MainMode.prototype.loadDay = function (data) {
        var self = this;
        if (data.day != null) {
            self.displayDay(data);
        }
        else {
            self.loadDefaultLastAvaibleDayInMonth(data);
        }
    };
    MainMode.prototype.loadDefaultLastAvaibleDayInMonth = function (data) {
        data.day = this.settings.LAST_DAY_IN_MONTH[parseInt(data.month) - 1];
        this.displayDay(data);
    };
    MainMode.prototype.loadCalendarChooserMode = function () {
        this.calendarChooserMode.start();
    };
    return MainMode;
}(Mode));
//Loads year, MONTHS, go to last and next month - and shows its all.
//if day parameter is not null - also loads that day.
// DO NOT TOUCH!!!
// CLOSED!
var Callendar = (function () {
    function Callendar(yearView, monthView, buttonPreviousMonth, buttonNextMonth, master) {
        this.yearView = yearView;
        this.monthView = monthView;
        this.buttonPreviousMonth = buttonPreviousMonth;
        this.buttonNextMonth = buttonNextMonth;
        this.master = master;
        this.minYear = 2000;
        var date = new Date();
        this.actualYear = date.getFullYear();
        this.actualMonth = date.getMonth();
        this.actualDay = date.getDate();
        this.setEventHandlers();
    }
    Callendar.prototype.load = function (year, month, day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.yearView.innerHTML = year;
        this.monthView.innerHTML = MONTHS[month];
    };
    Callendar.prototype.setEventHandlers = function () {
        var self = this;
        $(this.buttonNextMonth).off().on("click", function () {
            self.goToNextMont();
        });
        $(this.buttonPreviousMonth).off().on("click", function () { return self.goToPreviousMonth(); });
    };
    Callendar.prototype.goToNextMont = function () {
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
    };
    Callendar.prototype.goToPreviousMonth = function () {
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
    };
    return Callendar;
}());
//Simply start MainView again, but with new parameters.
var TimeTraveller = (function () {
    function TimeTraveller() {
    }
    return TimeTraveller;
}());
// Do not touch!
//Simply shows note and save it (if there is such need).
// notify note headers (observer) that he should need refresh too.
var NoteEdit = (function () {
    function NoteEdit(contentView, dateView, titleView, saveButton) {
        this.contentView = contentView;
        this.dateView = dateView;
        this.titleView = titleView;
        this.saveButton = saveButton;
        this.observers = [];
        this.setEventHandlers();
    }
    NoteEdit.prototype.notify = function () {
        var _this = this;
        this.observers.forEach(function (e) { return e.update(_this.loadedNote); });
    };
    NoteEdit.prototype.loadNote = function (note) {
        this.loadedNote = note;
        this.contentView.value = note.content;
        this.titleView.value = note.title;
        this.dateView.innerHTML = note.day + " " + MONTHS[note.month];
    };
    NoteEdit.prototype.attach = function (obj) {
        this.observers.push(obj);
    };
    NoteEdit.prototype.setEventHandlers = function () {
        var self = this;
        $(this.saveButton).off().on("click", function () { return self.saveNote(); });
    };
    NoteEdit.prototype.saveNote = function () {
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
    };
    return NoteEdit;
}());
// do not touch!
// generates NoteHeaders by supplied arrays of notes;
var NoteHeaders = (function () {
    function NoteHeaders(view, viewReplaceable, callback) {
        this.viewHolder = view;
        this.viewReplaceable = viewReplaceable;
        this.headersCallback = callback;
        this.self = this;
    }
    NoteHeaders.prototype.load = function (data) {
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
    };
    NoteHeaders.prototype.update = function (data) {
        this.load(data);
    };
    NoteHeaders.prototype.createNoteHeaders = function (notes) {
        var self = this;
        this.viewHolder.replaceChild(new NotesHeadersFactory().generateNotesHeadersList(notes, function (note, element) {
            self.headersCallback(note);
            self.clearAllSelectedElements(document.getElementById(self.viewReplaceable));
            self.markListElementAsActive(element);
        }), document.getElementById(this.viewReplaceable));
    };
    NoteHeaders.prototype.markListElementAsActive = function (li) {
        $(li).attr("class", selectedListElementClass);
    };
    NoteHeaders.prototype.getListElementByAttribute = function (list, attrName, attrValue) {
        var result;
        for (var i = 0; i < list.length; i++) {
            var node = list.item(i);
            if (node.getAttribute(attrName) == attrValue) {
                result = node;
                break;
            }
        }
        return result;
    };
    NoteHeaders.prototype.getActiveNote = function () {
        var list = this.viewHolder.getElementsByTagName("li");
        var result;
        for (var i = 0; i < list.length; i++) {
            var node = list.item(i);
            if (node.className == selectedListElementClass) {
                result = node;
                break;
            }
        }
        return result;
    };
    NoteHeaders.prototype.clearAllSelectedElements = function (list) {
        var elements = list.getElementsByTagName("li");
        for (var i = 0; i < elements.length; i++) {
            if (elements[i].getAttribute("class") === selectedListElementClass) {
                $(elements[i]).attr("class", unselectedListElementClass);
            }
        }
    };
    return NoteHeaders;
}());
var NotesHeadersFactory = (function () {
    function NotesHeadersFactory() {
    }
    NotesHeadersFactory.prototype.generateNotesHeadersList = function (notes, callback) {
        console.log("Drawing notes headers for: " + notes);
        var listRoot = document.createElement("ul");
        listRoot.setAttribute("id", "list_container_list");
        $("#list_container").off();
        var self = this;
        notes.forEach(function (el) {
            listRoot.appendChild(self.generateNoteHeader(el, callback));
        });
        console.log("result is " + listRoot);
        return listRoot;
    };
    NotesHeadersFactory.prototype.generateNoteHeader = function (note, headerCallback) {
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
    };
    return NotesHeadersFactory;
}());
;
var CalendarChooserMode = (function () {
    function CalendarChooserMode(modesManager, viewHolders) {
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
                            jquery.notify("Time travel not allowed... YET!");
                        }
                    }
                },
            ],
            classes: { "ui-dialog-titlebar": "my-dialog" }
        });
        this.loadDefaultValues();
    }
    CalendarChooserMode.prototype.start = function () {
        $(this.containerHolder).dialog("open");
        $("#year-chooser").spinner();
    };
    CalendarChooserMode.prototype.isInputValid = function () {
        var date = new Date();
        var actualYear = date.getFullYear();
        var actualMonth = date.getMonth() + 1;
        return (this.yearChooser.value < actualYear && this.yearChooser.value >= LIMIT_MINIMAL_YEAR || this.yearChooser.value == actualYear && this.monthChooser.value <= actualMonth);
    };
    CalendarChooserMode.prototype.loadDefaultValues = function () {
        var date = new Date();
        this.yearChooser.value = date.getFullYear();
        this.monthChooser.selectedIndex = date.getMonth();
    };
    CalendarChooserMode.prototype.openCalendarOpener = function () {
        $(this.containerHolder).dialog("open");
    };
    CalendarChooserMode.prototype.closeCalendarOpener = function () {
        $(this.containerHolder).dialog("close");
    };
    return CalendarChooserMode;
}());
//# sourceMappingURL=diary_main.js.map