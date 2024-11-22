package com.galega.order.adapters.in.queue.sqs.enums;

public enum PaymentStatus {
	PENDING ("PENDING"),
	REFUSED ("REFUSED"),
	APPROVED ("APPROVED"),
	CANCELLED ("CANCELLED");

	private final String status;

	PaymentStatus(String status){this.status = status;}


	public String toString() {
		return this.status;
	}
}
