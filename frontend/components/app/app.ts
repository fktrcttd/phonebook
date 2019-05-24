import Vue from 'vue';
import { Component } from 'vue-property-decorator';


@Component({
    components: {
        NavmenuComponent: require('../navmenu/navmenu.vue.html').default,
        SidebarComponent: require('../sidebar/sidebar.vue.html').default,
    }
})
export default class AppComponent extends Vue {
    
    
}
