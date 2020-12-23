package com.vending.constants;

public class VendingConstants {

	private VendingConstants() {

	}

	public static final String REQUEST_MAPPING = "/machine/{machineId}/coins";
	public static final String ADD_COINS = "/addCoins";
	public static final String NOT_A_VALID_DENOMINATION = "Not a Valid Denomination";
	public static final String UNEXPECTED_ERROR = "Unexpected Error";
	public static final String ADD_INITIAL_COINS = "/addInitialCoins";
	public static final String REFUND = "/refund";
	public static final String CACHE_VENDINGMACHINE = "vendingmachine";
	public static final String NUMBER_FORMATTING_EXCEPTION = "Exception in NumberFormatting";
	public static final String REFUND_ERROR = "Refund Cannot be completed as Insufficient Coins are there";
}
