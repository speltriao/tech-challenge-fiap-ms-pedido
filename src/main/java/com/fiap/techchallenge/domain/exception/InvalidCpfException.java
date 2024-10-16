package com.fiap.techchallenge.domain.exception;

public class InvalidCpfException extends Exception {

  public InvalidCpfException() {
    super("CPF must have 11 characters and only numbers");
  }

}
