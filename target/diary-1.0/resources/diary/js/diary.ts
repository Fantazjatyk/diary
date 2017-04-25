

const MONTHS = ["", "Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"];
var selectedListElementClass = "diary_list_active_note";
var unselectedListElementClass = "diary_list_unactive_note";
const LIMIT_MINIMAL_YEAR = 2000;
declare var moment;
declare var initJson;
var jquery: any = $;

$(document).ready(function () {
    var modesManager = new ModesManager();
    modesManager.start(JSON.parse(initJson));
});

class ModesManager {

    container = document.getElementById("diary_container");
    mainMode: MainMode;
    searchMode: SearchMode = new SearchMode(this);
    userOptions: UserOptions = new UserOptions(this);
    modeLoader: ModeLoader = new ModeLoader();
    initJson;


    start(initJson) {
        this.initJson = initJson;
        this.setEventHandlers();
        moment.locale("pl");

        this.mainMode = new MainMode(this, initJson);
        if (window.location.hash.length > 0) {
            this.loadSpecificMode();
        }

        this.loadMainMode(initJson);
    }

    loadMainMode(initJson) {
        this.modeLoader.loadMode(this.mainMode, this.container, null, null, initJson);
    }

    loadSpecificMode() {

    }


    loadSearchMode() {
        this.modeLoader.loadMode(this.searchMode, this.container, null, null, null);
    }

    loadUserOptionsMode() {
        this.modeLoader.loadMode(this.userOptions, this.container, null, null, this.initJson);
    }

    setEventHandlers() {
        var self = this;
        $("#load_main_view").off().on("click", () => self.loadMainMode(self.initJson));
        $("#search_for_notes").off().on("click", () => self.loadSearchMode());
        $("#user_options").off().on("click", () => self.loadUserOptionsMode());
    }
}

class ModeLoader {

    loadMode(mode: Mode, container: Node, callbackBefore: () => any, callbackAfter: () => any, data) {
        $(container).load(relHttpUrl(mode.getUrl()), () => {
            if (callbackBefore != null)
                callbackBefore();

            mode.init(data);

            if (callbackAfter != null)
                callbackAfter();
        }
        );
    }

}

abstract class Mode {

    modesManager: ModesManager;
    container;

    constructor(modesManager: ModesManager) {
        this.modesManager = modesManager;
    }

    abstract init(data: {});
    abstract getUrl(): string;
}



class SearchMode extends Mode {

    inputView;
    resultView;
    searchResultsFactory: SearchResultFactory;
    loadedResults = [];

    constructor(master) {
        super(master);
        this.searchResultsFactory = new SearchResultFactory();

    }

    init(data) {
        this.inputView = document.getElementById("search");
        this.resultView = document.getElementById("search_results");
        this.setEventHandlers();
    }

    getUrl() {
        return "searcher";
    }

    setEventHandlers() {
        var self = this;
        $("#search_button").off().on("click", () => self.initSearch());
    }

    initSearch() {
        var self = this;
        $.get(relHttpUrl("notes/search_title"), { title: self.inputView.value }).done((data) => {
            self.resultView.innerHTML = self.loadResults(data).innerHTML;
        });
    }

    loadResults(data) {
        var self = this;
        this.loadedResults = data;
        return this.searchResultsFactory.drawResults(data, (id) => {
            let result = self.loadedResults[id];
            self.modesManager.loadMainMode({ year: result.year, month: result.month, day: result.day });
        }
        );
    }
}

function relHttpUrl(relative: string) {
    var url = httpUrl();
    if (relative.charAt(relative.length) === "/")
        relative = relative.substr(0, relative.length - 2);

    url += relative;

    return url;
}

function httpUrl() {
    var url = window.location.href.toString().replace(window.location.hash, "").replace("#", "");
    if (url.charAt(url.length - 1) !== "/")
        url += "/";
    return url;
}

class SearchResultFactory {

    titleHolder;
    contentHolder;

    constructor() {

    }

    public drawResults(data, callback: (id) => any) {
        var container = document.createElement("search_results");
        var self = this;

        $("#search_results").off();

        for (let i = 0; i < data.length; i++) {
            var article = self.drawSingleResult(data[i]);
            article.setAttribute("id", "" + i);
            $("#search_results").on("click", "#" + i,
                () => {
                    callback(i);
                    console.log("test");
                });
            container.appendChild(article);
        }
        return container;
    }

    public drawSingleResult(result) {
        var article = document.createElement("article");
        var header = document.createElement("header");
        var title = document.createElement("label");
        var date = document.createElement("label");
        var p = document.createElement("p");

        title.innerHTML = result.title;
        title.classList.add("note-title");
        date.classList.add("note-date");
        date.innerHTML = result.year + "-" + result.month + "-" + result.day;

        header.classList.add("note-result");
        header.appendChild(date);
        header.appendChild(title);
        p.innerHTML = result.content;

        article.setAttribute("class", "search_result");

        article.appendChild(header);
        article.appendChild(p);
        return article;
    }
}



