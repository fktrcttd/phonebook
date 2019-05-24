import Vue from 'vue';
import { Component } from 'vue-property-decorator';

@Component
export default class NavmenuComponent extends Vue {
    
    sidebarHidden = false;
    toggleSidebar () {
        var sidebar = document.getElementById("sidebar");
        console.log(sidebar);
        this.sidebarHidden = !this.sidebarHidden;
        if (sidebar != null) {
            if (this.sidebarHidden) {
                var name, arr;
                name = "hidden";
                arr = sidebar.className.split(" ");
                if (arr.indexOf(name) == -1) {
                    sidebar.className += " " + name;
                }
            }
            else {
                sidebar.className = sidebar.className.replace(/\bhidden\b/g, "");
            }
        }
        
        
    }

}
