class UserOptions extends Mode {

    activeTabClassName = "active-tab";
    deactiveTabClassName = "deactive-tab";
    nav;

    container;

    init() {
        var self = this;
        this.container = document.getElementById("switch");
        this.nav = document.getElementById("navigation");
        this.bindEvents();
        this.loadSummary();
    }

    getUrl() {
        return "user_options";
    }

    bindEvents() {
        var self = this;

        $(self.nav).on("click", "li", (el) => self.activateTab(el));


        $("#summary").on("click", (el) => {
            self.loadSummary();
        });

        $("#settings").on("click", () => {
            self.loadSettings()
        });
    }

    loadSummary() {
        this.loadSubMode(new Summary("user_options/summary"));
    }


    loadSettings() {
        this.loadSubMode(new Settings("user_options/settings"));
    }

    clearTabs() {

        var tabs = $("#navigation li");
        tabs.removeClass(this.activeTabClassName);

    }


    activateTab(el) {
        this.clearTabs();
        $(el.target).removeClass(this.deactiveTabClassName);
        $(el.target).addClass(this.activeTabClassName);
    }

    loadSubMode(subMode:SubMode){
    $(this.container).load(subMode.url, ()=> subMode.init());
    }
}

abstract class SubMode {
    constructor(public url:string){}
    abstract init();
}

class Summary extends SubMode {

    init() {

    }


}
/* 
Ładuje ustawienia z PreferencesManager i ładuje je do kontrolek 
Po kliknięciu przycisku zapisz przeszyła je z powrotem do obiektu klasy PreferencesManager.
który zapisuje je i odświeża stronę, tak aby były one natychmiastowo dostępne.

*/
class Settings extends SubMode {

    settingsDrawer:SettingDrawer = new SettingDrawer();
    container;

    init() {
        this.setEventsHadlers();
        this.container = document.getElementById("settings_container");
    }

    setEventsHadlers(){
        $("#save_settings").off().on("click", ()=>{
    var settings = this.getAllBack();
    PreferencesManager.savePreferences(settings);
        })
    }

    getAllBack():{} {
        var settings =  this.container.getElementsByClassName("setting");
        var map = {};

        for(let i = 0; i < settings.length; i++){
            let el:any = settings[i];
            map[el.getAttribute("data-cookie-name")] = el.value;
        }
        return map;
    }
}



class SettingDrawer {

    saveCallback;
    container:HTMLElement;

    init(container, saveCallback) {
        this.container = container;
        this.saveCallback = saveCallback;
        this.draw();
    }

    mainColor(): Node {
        var mainColor = document.createElement("select");
        mainColor.id = "mainColor";

        mainColor.appendChild(this.createOption("Czerwony", "red"));
        mainColor.classList.add("setting");
        return mainColor;
    }

    createOption(name:string, value:string){
        var option = document.createElement("option");
        option.innerHTML = name;
        option.value = value;
        return option;
    }

    submitButton(): Node {
        var saveButton = document.createElement("button");
        saveButton.innerHTML = "Zapisz";
        var self = this;
        saveButton.onclick = function () {
                self.saveCallback();
        }
        return saveButton;
    }

    private draw() {
        var canvas = this.container;
        
        canvas.appendChild(this.mainColor());
        canvas.appendChild(this.submitButton());
    }


}
/*


Setting służy do przechowywania pary nazwy ustawienia i widoku kontrolki.
*/