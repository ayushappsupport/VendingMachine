/**
 * 
 */
package com.vending.service;

import java.util.List;
import java.util.Optional;

import com.vending.entity.Coin;
import com.vending.entity.Machine;
import com.vending.entity.RefundAmount;
import com.vending.exception.UserServiceException;

/**
 * @author ayush.a.mittal
 *
 */
public interface IVendingService {

	public List<Coin> addInitialCoins(String machineId,List<Coin> coins);
	
	public  Optional<?> addCoin(String machineId, Coin coin, Machine machine) throws Exception;
	
	public List<Coin> refundAmount(String machineId,RefundAmount refund) throws UserServiceException, Exception;
}
