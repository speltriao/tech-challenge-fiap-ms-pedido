package com.fiap.techchallenge_order.adapters.in.queue.sqs.enums;


public enum OperationTypes {
	UPDATE_ORDER_STATUS("UPDATE_ORDER_STATUS"),
	CREATE_ORDER("CREATE_ORDER");

	private final String status;

	OperationTypes(String status){
		this.status = status;
	}

	public String toString() {
		return this.status;
	}
}
