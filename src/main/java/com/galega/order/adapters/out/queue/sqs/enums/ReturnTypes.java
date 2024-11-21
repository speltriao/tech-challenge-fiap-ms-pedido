package com.galega.order.adapters.out.queue.sqs.enums;


public enum ReturnTypes {
	ORDER_STATUS_UPDATED("ORDER_STATUS_UPDATED");
	private final String status;

	ReturnTypes(String status){
		this.status = status;
	}

	public String toString() {
		return this.status;
	}
}
