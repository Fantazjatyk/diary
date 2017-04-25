declare var Cookies;

$(document).ready(() => PreferencesManager.loadPreferences());


var PreferencesManager = {
    loadPreferences: function () {
        var mainColor = Cookies.get(Preferences.MAIN_COLOR);

        document.body.style.setProperty("--theme-color-primary", mainColor);
    },

    savePreference: function (name:string, value) {
        Cookies.set(name, value);
    },

    savePreferences(preferences:{}){
        var self = this;
        var keys = Object.keys(preferences);

        keys.forEach((el)=>{
            self.savePreference(el, preferences[el]);
        });
        window.location.reload();
    }

}

var Preferences = {
    MAIN_COLOR: "mainColor"
}




