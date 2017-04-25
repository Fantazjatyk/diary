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
var UserOptions = (function (_super) {
    __extends(UserOptions, _super);
    function UserOptions() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.activeTabClassName = "active-tab";
        _this.deactiveTabClassName = "deactive-tab";
        return _this;
    }
    UserOptions.prototype.init = function () {
        var self = this;
        this.container = document.getElementById("switch");
        this.nav = document.getElementById("navigation");
        this.bindEvents();
        this.loadSummary();
    };
    UserOptions.prototype.getUrl = function () {
        return "user_options";
    };
    UserOptions.prototype.bindEvents = function () {
        var self = this;
        $(self.nav).on("click", "li", function (el) { return self.activateTab(el); });
        $("#summary").on("click", function (el) {
            self.loadSummary();
        });
        $("#settings").on("click", function () {
            self.loadSettings();
        });
    };
    UserOptions.prototype.loadSummary = function () {
        this.loadSubMode(new Summary("user_options/summary"));
    };
    UserOptions.prototype.loadSettings = function () {
        this.loadSubMode(new Settings("user_options/settings"));
    };
    UserOptions.prototype.clearTabs = function () {
        var tabs = $("#navigation li");
        tabs.removeClass(this.activeTabClassName);
    };
    UserOptions.prototype.activateTab = function (el) {
        this.clearTabs();
        $(el.target).removeClass(this.deactiveTabClassName);
        $(el.target).addClass(this.activeTabClassName);
    };
    UserOptions.prototype.loadSubMode = function (subMode) {
        $(this.container).load(subMode.url, function () { return subMode.init(); });
    };
    return UserOptions;
}(Mode));
var SubMode = (function () {
    function SubMode(url) {
        this.url = url;
    }
    return SubMode;
}());
var Summary = (function (_super) {
    __extends(Summary, _super);
    function Summary() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Summary.prototype.init = function () {
    };
    return Summary;
}(SubMode));
/*
Ładuje ustawienia z PreferencesManager i ładuje je do kontrolek
Po kliknięciu przycisku zapisz przeszyła je z powrotem do obiektu klasy PreferencesManager.
który zapisuje je i odświeża stronę, tak aby były one natychmiastowo dostępne.

*/
var Settings = (function (_super) {
    __extends(Settings, _super);
    function Settings() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.settingsDrawer = new SettingDrawer();
        return _this;
    }
    Settings.prototype.init = function () {
        this.setEventsHadlers();
        this.container = document.getElementById("settings_container");
    };
    Settings.prototype.setEventsHadlers = function () {
        var _this = this;
        $("#save_settings").off().on("click", function () {
            var settings = _this.getAllBack();
            PreferencesManager.savePreferences(settings);
        });
    };
    Settings.prototype.getAllBack = function () {
        var settings = this.container.getElementsByClassName("setting");
        var map = {};
        for (var i = 0; i < settings.length; i++) {
            var el = settings[i];
            map[el.getAttribute("data-cookie-name")] = el.value;
        }
        return map;
    };
    return Settings;
}(SubMode));
var SettingDrawer = (function () {
    function SettingDrawer() {
    }
    SettingDrawer.prototype.init = function (container, saveCallback) {
        this.container = container;
        this.saveCallback = saveCallback;
        this.draw();
    };
    SettingDrawer.prototype.mainColor = function () {
        var mainColor = document.createElement("select");
        mainColor.id = "mainColor";
        mainColor.appendChild(this.createOption("Czerwony", "red"));
        mainColor.classList.add("setting");
        return mainColor;
    };
    SettingDrawer.prototype.createOption = function (name, value) {
        var option = document.createElement("option");
        option.innerHTML = name;
        option.value = value;
        return option;
    };
    SettingDrawer.prototype.submitButton = function () {
        var saveButton = document.createElement("button");
        saveButton.innerHTML = "Zapisz";
        var self = this;
        saveButton.onclick = function () {
            self.saveCallback();
        };
        return saveButton;
    };
    SettingDrawer.prototype.draw = function () {
        var canvas = this.container;
        canvas.appendChild(this.mainColor());
        canvas.appendChild(this.submitButton());
    };
    return SettingDrawer;
}());
/*


Setting służy do przechowywania pary nazwy ustawienia i widoku kontrolki.
*/ 
//# sourceMappingURL=user_options.js.map