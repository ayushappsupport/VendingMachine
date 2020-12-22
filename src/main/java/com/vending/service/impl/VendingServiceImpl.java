/**
 * 
 */
package com.vending.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vending.constants.VendingConstants;
import com.vending.entity.Coin;
import com.vending.entity.Machine;
import com.vending.entity.RefundAmount;
import com.vending.exception.UserServiceException;
import com.vending.service.IVendingService;
import com.vending.service.IVendingServiceData;
import com.vending.utility.VendingUtility;

/**
 * @author ayush.a.mittal
 *
 */

@Service
public class VendingServiceImpl implements IVendingService {

	@Autowired
	private IVendingServiceData vendingServiceData;
	
	
	/**
	 * To add initial coins in the machine
	 * 
	 * @param machineId
	 * @param list coin
	 * @param machine
	 * @return
	 */
	@Override
	public List<Coin> addInitialCoins(String machineId, List<Coin> coins) {
		List<Coin> resultCoins = new ArrayList<Coin>();
		Machine machine = null;
		try {
			Optional<Machine> machineopt = vendingServiceData.findByName(machineId);
			if (machineopt.isPresent()) {
				machine = machineopt.get();
			} else {
				machine = new Machine(machineId, 0);
			}
			Collection<Coin> coinsPresent = vendingServiceData.findByMachineName(machineId);
			Map<Integer, String> coinPresentMap = VendingUtility.getCoinsMap(coinsPresent);
			VendingUtility.getResultCoins(coins, resultCoins, machine, coinPresentMap, vendingServiceData);
			vendingServiceData.saveMachine(machine);
			
		} catch (NumberFormatException ex) {
			throw new UserServiceException(VendingConstants.NUMBER_FORMATTING_EXCEPTION);
		} catch (Exception e) {
			throw new UserServiceException(VendingConstants.UNEXPECTED_ERROR);
		}
		return resultCoins;
	}

	
	/**
	 * To add a coin in the machine
	 * 
	 * @param machineId
	 * @param coin
	 * @param machine
	 * @return
	 */
	@Override
	public Optional<Machine> addCoin(String machineId, Coin coin, Machine machine) throws Exception {
		final boolean[] coinFound = { false };
		// Increase the amount of coins if the machine already has seen that coin
		vendingServiceData.findByMachineName(machineId).forEach(machineCoin -> {
			if (coin.denomination == machineCoin.denomination) {
				machineCoin.count = machineCoin.count + 1;
				// coin.setId(machineCoin.getId());
				coinFound[0] = true;
				vendingServiceData.saveCoin(machineCoin);
			}
		});

		// Else add the coin to the repository
		if (!coinFound[0] && IntStream.of(Coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.denomination)) {
			vendingServiceData.saveAndFlushCoin((new Coin(machine, coin.denomination, 1)));
			coinFound[0] = true;
		}

		if (coinFound[0]) {
			machine.currentAmount += coin.denomination;
		}

		vendingServiceData.saveAndFlushMachine(machine);

		return vendingServiceData.findByName(machineId);
	}

	@Override
	/**
	 * To return the refund
	 * 
	 * @param machineId
	 * @param refund
	 * @return
	 */
	public List<Coin> refundAmount(String machineId, RefundAmount refund) throws UserServiceException,Exception {
		
		List<Coin> refundCoins = new ArrayList<>();
		try {
		final int[] refundTotal = new int[1];
		refundTotal[0] = refund.getRefundAmount();
		Machine machine = vendingServiceData.findByName(machineId).get();
		int initialMachineAmount = machine.currentAmount;
		if (refundTotal[0] > initialMachineAmount) {
			throw new UserServiceException(VendingConstants.REFUND_ERROR);
		}else if(refundTotal[0]<=0) {
			throw new UserServiceException(VendingConstants.REFUND_ERROR);
		}
		
		List<Coin> coinsToSave = new ArrayList<Coin>();

		//
		for (int value : Coin.POSSIBLE_VALUES) {
			vendingServiceData.findByMachineName(machineId).forEach(coin -> {
				if (value == coin.denomination && coin.count > 0 && coin.denomination <= refundTotal[0]) {
					double max_coins = Math.min(Math.floor(refundTotal[0] / coin.denomination), coin.count);
					coin.count -= max_coins;
					coinsToSave.add(coin);
					// this.coinRepository.saveAndFlush(coin);
					refundCoins.add(new Coin(coin.denomination, (int) max_coins));
					refundTotal[0] -= (int) max_coins * coin.denomination;
				}
			});
		}
		if (refundTotal[0] > 0) {
			throw new UserServiceException(VendingConstants.REFUND_ERROR);
		} else {
			machine.currentAmount = machine.currentAmount - refund.getRefundAmount();
			vendingServiceData.saveCoinBulk(coinsToSave);
			vendingServiceData.saveMachine(machine);
		}
		}
		catch(UserServiceException e) {
			throw new UserServiceException(e.getLocalizedMessage());
		}
		catch(Exception e) {
			throw new UserServiceException(VendingConstants.UNEXPECTED_ERROR);
		}
		return refundCoins;
	}

}
