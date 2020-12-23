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
	 * @param list      coin
	 * @param machine
	 * @return
	 */
	@Override
	public List<Coin> addInitialCoins(String machineId, List<Coin> coins) {
		List<Coin> resultCoins = new ArrayList<>();
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
			if (coin.getDenomination() == machineCoin.getDenomination()) {
				machineCoin.setCount(machineCoin.getCount() + 1);
				coinFound[0] = true;
				vendingServiceData.saveCoin(machineCoin);
			}
		});

		// Else add the coin to the repository
		if (!coinFound[0] && IntStream.of(Coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.getDenomination())) {
			vendingServiceData.saveAndFlushCoin((new Coin(machine, coin.getDenomination(), 1)));
			coinFound[0] = true;
		}

		if (coinFound[0]) {
			machine.setCurrentAmount(machine.getCurrentAmount() + coin.getDenomination());
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
	public List<Coin> refundAmount(String machineId, RefundAmount refund) throws UserServiceException, Exception {

		List<Coin> refundCoins = new ArrayList<>();
		try {
			final int[] refundTotal = new int[1];
			Machine machine = new Machine();
			refundTotal[0] = refund.getRefundamountvalue();
			Optional<Machine> mach = vendingServiceData.findByName(machineId);
			if (mach.isPresent()) {
				machine = mach.get();
			}
			int initialMachineAmount = machine.getCurrentAmount();
			if (refundTotal[0] > initialMachineAmount || refundTotal[0] <= 0) {
				throw new UserServiceException(VendingConstants.REFUND_ERROR);
			}

			List<Coin> coinsToSave = new ArrayList<>();

			//
			for (int value : Coin.POSSIBLE_VALUES) {
				vendingServiceData.findByMachineName(machineId).forEach(coin -> {
					if (value == coin.getDenomination() && coin.getCount() > 0
							&& coin.getDenomination() <= refundTotal[0]) {
						double maxCoins = Math.min(refundTotal[0] / coin.getDenomination(), coin.getCount());
						coin.setCount(coin.getCount() - (int) (maxCoins));
						coinsToSave.add(coin);
						refundCoins.add(new Coin(coin.getDenomination(), (int) maxCoins));
						refundTotal[0] -= (int) maxCoins * coin.getDenomination();
					}
				});
			}
			if (refundTotal[0] > 0) {
				throw new UserServiceException(VendingConstants.REFUND_ERROR);
			} else {
				machine.setCurrentAmount(machine.getCurrentAmount() - refund.getRefundamountvalue());
				vendingServiceData.saveCoinBulk(coinsToSave);
				vendingServiceData.saveMachine(machine);
			}
		} catch (UserServiceException e) {
			throw new UserServiceException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new UserServiceException(VendingConstants.UNEXPECTED_ERROR);
		}
		return refundCoins;
	}

}
