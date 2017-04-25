$(document).ready(function () { return PreferencesManager.loadPreferences(); });
var PreferencesManager = {
    loadPreferences: function () {
        var mainColor = Cookies.get(Preferences.MAIN_COLOR);
        document.body.style.setProperty("--theme-color-primary", mainColor);
    },
    savePreference: function (name, value) {
        Cookies.set(name, value);
    },
    savePreferences: function (preferences) {
        var self = this;
        var keys = Object.keys(preferences);
        keys.forEach(function (el) {
            self.savePreference(el, preferences[el]);
        });
        window.location.reload();
    }
};
var Preferences = {
    MAIN_COLOR: "mainColor"
};
//# sourceMappingURL=prefs_loader.js.map