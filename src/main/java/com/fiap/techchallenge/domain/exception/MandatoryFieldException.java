package com.fiap.techchallenge.domain.exception;

public class MandatoryFieldException extends Exception {

  public MandatoryFieldException(String fieldName) {
    super("Field " + fieldName + " is mandatory");
  }

}
