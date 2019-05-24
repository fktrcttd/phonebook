import './css/site.css';
import 'bootstrap';
import Vue from 'vue';
import VueRouter from 'vue-router';
import Debounce from 'vue-debounce-component';
// @ts-ignore
import VueTheMask from 'vue-the-mask';
Vue.use(VueTheMask);
Vue.use(Debounce);
Vue.use(VueRouter);

const routes = [
    { path: '/', component: require('./components/list/list.vue.html').default },
];

new Vue({
    el: '#app-root',
    router: new VueRouter({ mode: 'history', routes: routes }),
    render: h => h(require('./components/app/app.vue.html').default)
    
});
