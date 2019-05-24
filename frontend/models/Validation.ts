import {Telephone} from "./Telephone";

export class Validator {
    tel: Telephone;
    serverValidationStatus:String;
    
    constructor(tel: Telephone, serverValidationStatus:String = "") {
        this.tel = tel;
        this.serverValidationStatus = serverValidationStatus
    }

    public titleIsValid(){
        return this.tel.title !== "";
    }

    public numberIsValid(){
        var regexp = new RegExp('^(?:\\+|\\d)[\\d\\-\\(\\) ]{16}\\d$');
        return regexp.test(this.tel.number);
    }
    
    public telephoneIsValid(){
        return this.titleIsValid() && this.numberIsValid();
    }
    
    
}