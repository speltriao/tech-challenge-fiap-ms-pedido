package com.fiap.techchallenge_order.domain.exception;

public class OrderNotReadyException extends Exception{

  private String details;

  public OrderNotReadyException(String details) {
    super("Order is not ready to be paid");
    this.details = details;
  }

  public String getDetails() {
    return this.details;
  }

}
