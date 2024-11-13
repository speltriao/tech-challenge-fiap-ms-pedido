package com.galega.order.domain.exception;

public class MandatoryFieldException extends Exception {

  public MandatoryFieldException(String fieldName) {
    super("Field " + fieldName + " is mandatory");
  }

}
