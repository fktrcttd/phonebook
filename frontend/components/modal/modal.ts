import {Vue, Component, Prop, Watch, Emit} from 'vue-property-decorator'

@Component
export default class ModalComponent extends Vue {
    @Prop(Boolean) readonly show!: Boolean;
    @Prop(String) readonly title!: string;
    @Prop({ default: 'ОК' }) readonly okText!: string;
    @Prop({ default: 'modal' }) readonly transition!: string;
    @Prop({ default: 'Отмена' }) readonly cancelText!: string;
    @Prop({ default: false }) readonly closeWhenOk!: Boolean;
    
    duration:any = 0;
    dialog:Boolean = false;
    
    created () {
        this.dialog = this.show; 
        if (this.show) {
            document.body.className += ' modal-open';
        }
    }

    beforeDestroy () {
        document.body.className = document.body.className.replace(/\s?modal-open/, '');
    }

    @Watch('dialog')
    onShowChanged(val: Boolean, oldVal: Boolean) {
        
        console.log("deleteDialog changed", val);
        if (val) {
            document.body.className += ' modal-open';
        }
        else {
            if (!this.duration) {
                // @ts-ignore
                this.duration = window.getComputedStyle(this.$el)["transition-duration"].replace('s', '') * 1000;
            }
            window.setTimeout(() => {
                document.body.className = document.body.className.replace(/\s?modal-open/, '');
            }, this.duration || 0);
        }
    }

    @Emit('ok')
    ok() {
        if (this.closeWhenOk) {
            this.dialog = false;
        }
    }


    @Emit('cancel')
    cancel() {
        this.dialog = false;
    }
}


