import Vue from 'vue';
import { Component } from 'vue-property-decorator';

@Component
export default class NavmenuComponent extends Vue {
    
    sidebarShown = true;
    toggleSidebar () {
        var sidebar = document.getElementById("sidebar");
        this.sidebarShown = !this.sidebarShown;
        // if (sidebar != null) {
        //     if (this.sidebarShown) {
        //         var name, arr;
        //         name = "mystyle";
        //         arr = sidebar.className.split(" ");
        //         if (arr.indexOf(name) == -1) {
        //             sidebar.className += " " + name;
        //         }
        //     }
        //     else {
        //         sidebar.className = sidebar.className.replace(/\bactive\b/g, "");
        //     }
        // }
        
        
    }

}
