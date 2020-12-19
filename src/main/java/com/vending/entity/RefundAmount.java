package com.vending.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RefundAmount {
	
	@Id
	public int refundAmount;

	public int getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(int refundAmount) {
		this.refundAmount = refundAmount;
	}


	
	
	
}
