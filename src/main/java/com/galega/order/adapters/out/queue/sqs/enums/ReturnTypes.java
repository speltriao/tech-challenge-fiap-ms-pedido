package com.fiap.techchallenge_order.adapters.out.queue.sqs.enums;


public enum ReturnTypes {
	ORDER_CREATED("ORDER_CREATED"),
	ORDER_STATUS_UPDATED("ORDER_STATUS_UPDATED");
	private final String status;

	ReturnTypes(String status){
		this.status = status;
	}

	public String toString() {
		return this.status;
	}
}
