import Vue from 'vue';
import {Component, Watch} from 'vue-property-decorator';
import {Telephone} from "../../models/Telephone";
import axios from "axios";
import {Validator} from "../../models/Validation";

@Component({
    components: {
        ModalComponent: require('../modal/modal.vue.html').default,
    }
})
export default class List extends Vue {
    telephones:Telephone[] = [];    
    telephoneToEdit:Telephone = new Telephone("","", 0);
    telephoneToDelete:Telephone = new Telephone("","", 0);
    telephoneToCreate:Telephone = new Telephone("","", 0);
    
    //1- поиск по номеру
    //2 - поиск по имени
    searchType:Number = 1;
    search:String = "";
    
    deleteDialog:Boolean=false;
    
    createDialog:Boolean=false;
    createValidator = new Validator(this.telephoneToCreate);
    createTitleIsDirty:Boolean = false;
    createNumberIsDirty:Boolean = false;
    
    editDialog:Boolean=false;
    editValidator = new Validator(this.telephoneToEdit);
    
    mounted() {
      this.loadList();
    } 

    showDeleteModal(telephone: Telephone) {
        this.telephoneToDelete = telephone;
        this.deleteDialog = true;        
    }

    deleteTelephone(){
        axios.delete(`/api/phone/${this.telephoneToDelete.id}`)
            .then(respone => {
                this.loadList();
                    this.deleteDialog = false;
                    console.log("success");
                }
            )
            .catch(function (error) {
                alert(error);
            });
    }
    
    showEditModal(telephone: Telephone){
        //clone
        this.telephoneToEdit.id = telephone.id;
        this.telephoneToEdit.title = telephone.title;
        this.telephoneToEdit.number = telephone.number;
        this.editDialog = true;
        
        //сбрасываем состояние валидации на сервере
        this.editValidator.serverValidationStatus = "";
    }
    
    editTelephone(){
        if (!this.editValidator.telephoneIsValid()){
            return;
        }
        const data = {
            "title": this.telephoneToEdit.title,
            "number": this.telephoneToEdit.number
        };

        axios.post(`/api/phone/${this.telephoneToEdit.id}`, JSON.stringify(data), {
            headers: {
                'Content-Type': 'application/json',
            }})
            .then((response) => {
                this.loadList();
                this.editValidator.serverValidationStatus = "success";
            })
            .catch((error) => {
                this.editValidator.serverValidationStatus = "fail";
            });
    }
    
    showCreateModal(){
        this.createDialog = true;
        this.createValidator.serverValidationStatus= "";
    }
    
    createTelephone(){
        if (!this.createValidator.telephoneIsValid()){
            return;
        }
        const data = {
            "title": this.telephoneToCreate.title,
            "number": this.telephoneToCreate.title
        };

        axios.post('/api/phone/create', JSON.stringify(data), {
            headers: {
                'Content-Type': 'application/json',
            }})
            .then((response) => {
                this.createValidator.serverValidationStatus = "success";
                this.telephoneToCreate = new Telephone("","", 0);
                this.loadList();
            })
            .catch((error) => {
                this.createValidator.serverValidationStatus = "fail";
            });
    }
    
    cancel(){
        this.deleteDialog = false;
        this.editDialog = false;
        this.createDialog = false;
    }
    
    loadList(){
        axios.get('/api/phones')
            .then(response => response.data as Promise<Telephone[]>)
            .then(data =>
            {
                let phones = data;
                this.telephones = phones;
            })
            .catch(function (error) {
                // handle error
                console.log(error);
            })
    }
    
    @Watch("search")
    onSearchChanged(val:string, oldVal:string){
        this.applySearch();
    }

    applySearch(){
        let params = {
            params: {
                term: this.search
            },
        };
        var type = this.searchType==1? "searchByNumber":"searchByName";
        var searchUrl = "/api/phone/" + type;

        axios.get(searchUrl, params)
            .then(response => response.data as Promise<Telephone[]>)
            .then(data =>
            {
                this.telephones = data;
            })
            .catch(function (error) {
                // handle error
            })
    }

    @Watch("telephoneToCreate.title")
    onTelephoneToCreateTitleChanged(val:any, oldVal:any){
        this.createTitleIsDirty = true;
    }

    @Watch("telephoneToCreate.number")
    onTelephoneToCreateNumberChanged(val:any, oldVal:any){
        this.createNumberIsDirty = true;        
    }
    
}



