package com.vending.entity;

import javax.persistence.Id;

public class RefundAmount {

	@Id
	public int refundamountvalue;

	public int getRefundamountvalue() {
		return refundamountvalue;
	}

	public void setRefundamountvalue(int refundamountvalue) {
		this.refundamountvalue = refundamountvalue;
	}



}
