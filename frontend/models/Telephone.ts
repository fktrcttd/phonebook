export class Telephone {
    id: Number;
    number: string;
    title: string;
    

    constructor(number: string, title: string, id:Number) {
        this.number = number;
        this.title = title;
        this.id = id;
    }
    
   public titleIsValid(){
        return this.title !== "";
    }

    public numberIsValid(){
        var regexp = new RegExp('^(?:\\+|\\d)[\\d\\-\\(\\) ]{16}\\d$');
        return regexp.test(this.number);
    }
}