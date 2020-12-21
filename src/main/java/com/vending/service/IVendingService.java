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
	/**
	 * to Add all the coins during machine setup
	 * 
	 * @param machineId
	 * @param coins
	 * @return
	 */
	public List<Coin> addInitialCoins(String machineId,List<Coin> coins);
	/**
	 * To add a single coin by the user and register it in the machine
	 * 
	 * @param machineId
	 * @param coin
	 * @param machine
	 * @return
	 * @throws Exception
	 */
	public  Optional<?> addCoin(String machineId, Coin coin, Machine machine) throws Exception;
	
	/**
	 * To return the refund with coins 
	 * 
	 * @param machineId
	 * @param refund
	 * @return
	 * @throws UserServiceException
	 * @throws Exception
	 */
	public List<Coin> refundAmount(String machineId,RefundAmount refund) throws UserServiceException, Exception;
}
