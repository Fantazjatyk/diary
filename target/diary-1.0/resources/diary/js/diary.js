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
var MONTHS = ["", "Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"];
var selectedListElementClass = "diary_list_active_note";
var unselectedListElementClass = "diary_list_unactive_note";
var LIMIT_MINIMAL_YEAR = 2000;
var jquery = $;
$(document).ready(function () {
    var modesManager = new ModesManager();
    modesManager.start(JSON.parse(initJson));
});
var ModesManager = (function () {
    function ModesManager() {
        this.container = document.getElementById("diary_container");
        this.searchMode = new SearchMode(this);
        this.userOptions = new UserOptions(this);
        this.modeLoader = new ModeLoader();
    }
    ModesManager.prototype.start = function (initJson) {
        this.initJson = initJson;
        this.setEventHandlers();
        moment.locale("pl");
        this.mainMode = new MainMode(this, initJson);
        if (window.location.hash.length > 0) {
            this.loadSpecificMode();
        }
        this.loadMainMode(initJson);
    };
    ModesManager.prototype.loadMainMode = function (initJson) {
        this.modeLoader.loadMode(this.mainMode, this.container, null, null, initJson);
    };
    ModesManager.prototype.loadSpecificMode = function () {
    };
    ModesManager.prototype.loadSearchMode = function () {
        this.modeLoader.loadMode(this.searchMode, this.container, null, null, null);
    };
    ModesManager.prototype.loadUserOptionsMode = function () {
        this.modeLoader.loadMode(this.userOptions, this.container, null, null, this.initJson);
    };
    ModesManager.prototype.setEventHandlers = function () {
        var self = this;
        $("#load_main_view").off().on("click", function () { return self.loadMainMode(self.initJson); });
        $("#search_for_notes").off().on("click", function () { return self.loadSearchMode(); });
        $("#user_options").off().on("click", function () { return self.loadUserOptionsMode(); });
    };
    return ModesManager;
}());
var ModeLoader = (function () {
    function ModeLoader() {
    }
    ModeLoader.prototype.loadMode = function (mode, container, callbackBefore, callbackAfter, data) {
        $(container).load(relHttpUrl(mode.getUrl()), function () {
            if (callbackBefore != null)
                callbackBefore();
            mode.init(data);
            if (callbackAfter != null)
                callbackAfter();
        });
    };
    return ModeLoader;
}());
var Mode = (function () {
    function Mode(modesManager) {
        this.modesManager = modesManager;
    }
    return Mode;
}());
var SearchMode = (function (_super) {
    __extends(SearchMode, _super);
    function SearchMode(master) {
        var _this = _super.call(this, master) || this;
        _this.loadedResults = [];
        _this.searchResultsFactory = new SearchResultFactory();
        return _this;
    }
    SearchMode.prototype.init = function (data) {
        this.inputView = document.getElementById("search");
        this.resultView = document.getElementById("search_results");
        this.setEventHandlers();
    };
    SearchMode.prototype.getUrl = function () {
        return "searcher";
    };
    SearchMode.prototype.setEventHandlers = function () {
        var self = this;
        $("#search_button").off().on("click", function () { return self.initSearch(); });
    };
    SearchMode.prototype.initSearch = function () {
        var self = this;
        $.get(relHttpUrl("notes/search_title"), { title: self.inputView.value }).done(function (data) {
            self.resultView.innerHTML = self.loadResults(data).innerHTML;
        });
    };
    SearchMode.prototype.loadResults = function (data) {
        var self = this;
        this.loadedResults = data;
        return this.searchResultsFactory.drawResults(data, function (id) {
            var result = self.loadedResults[id];
            self.modesManager.loadMainMode({ year: result.year, month: result.month, day: result.day });
        });
    };
    return SearchMode;
}(Mode));
function relHttpUrl(relative) {
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
var SearchResultFactory = (function () {
    function SearchResultFactory() {
    }
    SearchResultFactory.prototype.drawResults = function (data, callback) {
        var container = document.createElement("search_results");
        var self = this;
        $("#search_results").off();
        var _loop_1 = function (i) {
            article = self.drawSingleResult(data[i]);
            article.setAttribute("id", "" + i);
            $("#search_results").on("click", "#" + i, function () {
                callback(i);
                console.log("test");
            });
            container.appendChild(article);
        };
        var article;
        for (var i = 0; i < data.length; i++) {
            _loop_1(i);
        }
        return container;
    };
    SearchResultFactory.prototype.drawSingleResult = function (result) {
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
    };
    return SearchResultFactory;
}());
//# sourceMappingURL=diary.js.map