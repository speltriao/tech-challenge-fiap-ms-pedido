package com.galega.order.domain.exception;

public class MercadoPagoUnavailableException extends Exception {

  public MercadoPagoUnavailableException(){
    super("Error trying to communicate with MercadoPago. Try again later.");
  }

}
