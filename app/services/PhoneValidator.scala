package services

import api.models.PhoneApiModel

object PhoneValidator {

private def numberIsValid(num:String):Boolean = {
    val isMatch =num.matches("^(?:\\+|\\d)[\\d\\-\\(\\) ]{16}\\d$")
    isMatch
  }
  
  private def titleIsValid(title:String):Boolean = {
    !title.isEmpty
  }
  
  def telIsValid(tel:PhoneApiModel):Boolean ={
    titleIsValid(tel.title) && numberIsValid(tel.number)
  }  
}
