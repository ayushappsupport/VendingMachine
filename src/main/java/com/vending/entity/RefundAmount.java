package com.vending.entity;

import javax.persistence.Id;


public class RefundAmount {
	
	@Id
	public int refundamountvalue;

	public int getRefundAmount() {
		return refundamountvalue;
	}

	public void setRefundAmount(int refundamountvalue) {
		this.refundamountvalue = refundamountvalue;
	}


	
	
	
}
