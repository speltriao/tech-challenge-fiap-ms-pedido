package com.galega.order.domain.enums;

public enum PaymentStatusEnum {
	PENDING ("PENDING"),
	REFUSED ("REFUSED"),
	APPROVED ("APPROVED"),
	CANCELLED ("CANCELLED");

	private final String status;

	PaymentStatusEnum(String status){
		this.status = status;
	}

	public String toString() {
		return this.status;
	}

}
