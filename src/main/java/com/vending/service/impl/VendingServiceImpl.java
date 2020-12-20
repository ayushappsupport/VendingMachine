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

import com.vending.entity.Coin;
import com.vending.entity.Machine;
import com.vending.entity.RefundAmount;
import com.vending.exception.UserServiceException;
import com.vending.repository.CoinRepository;
import com.vending.repository.MachineRepository;
import com.vending.service.IVendingService;
import com.vending.utility.VendingUtility;

/**
 * @author ayush.a.mittal
 *
 */

@Service
public class VendingServiceImpl implements IVendingService {

	@Autowired
	private MachineRepository machineRepo;

	@Autowired
	private CoinRepository coinRepo;

	@Override
	public List<Coin> addInitialCoins(String machineId, List<Coin> coins) {
		List<Coin> resultCoins = new ArrayList<Coin>();
		Machine machine = null;
		try {
			Optional<Machine> machineopt = machineRepo.findByName(machineId);
			if (machineopt.isPresent()) {
				machine = machineopt.get();
			} else {
				machine = new Machine(machineId, 0);
			}
			Collection<Coin> coinsPresent = coinRepo.findByMachineName(machineId);
			Map<Integer, String> coinPresentMap = VendingUtility.getCoinsMap(coinsPresent);
			VendingUtility.getResultCoins(coins, resultCoins, machine, coinPresentMap, coinRepo);
			machineRepo.save(machine);
			
		} catch (NumberFormatException ex) {
			throw new UserServiceException("Exception in NumberFormatting");
		} catch (Exception e) {
			throw new UserServiceException("Generic Exception");
		}
		return resultCoins;
	}

	
	/**
	 * @param machineId
	 * @param coin
	 * @param machine
	 * @return
	 */
	@Override
	public Optional<Machine> addCoin(String machineId, Coin coin, Machine machine) throws Exception {
		final boolean[] coinFound = { false };
		// Increase the amount of coins if the machine already has seen that coin
		coinRepo.findByMachineName(machineId).forEach(machineCoin -> {
			if (coin.denomination == machineCoin.denomination) {
				machineCoin.count = machineCoin.count + 1;
				// coin.setId(machineCoin.getId());
				coinFound[0] = true;
				coinRepo.save(machineCoin);
			}
		});

		// Else add the coin to the repository
		if (!coinFound[0] && IntStream.of(Coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.denomination)) {
			coinRepo.saveAndFlush(new Coin(machine, coin.denomination, 1));
			coinFound[0] = true;
		}

		if (coinFound[0]) {
			machine.currentAmount += coin.denomination;
		}

		machineRepo.saveAndFlush(machine);

		return machineRepo.findByName(machineId);
	}

	@Override
	/**
	 * @param machineId
	 * @param refund
	 * @return
	 */
	public List<Coin> refundAmount(String machineId, RefundAmount refund) throws UserServiceException,Exception {
		
		List<Coin> refundCoins = new ArrayList<>();
		try {
		final int[] refundTotal = new int[1];
		refundTotal[0] = refund.getRefundAmount();
		Machine machine = machineRepo.findByName(machineId).get();
		int initialMachineAmount = machine.currentAmount;
		if (refundTotal[0] > initialMachineAmount) {
			throw new UserServiceException("Refund Cannot be completed as Insufficient Coins are there");
		}
		
		List<Coin> coinsToSave = new ArrayList<Coin>();

		//
		for (int value : Coin.POSSIBLE_VALUES) {
			coinRepo.findByMachineName(machineId).forEach(coin -> {
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
			throw new UserServiceException("Refund Cannot be completed as Insufficient Coins are there");
		} else {
			machine.currentAmount = machine.currentAmount - refund.getRefundAmount();
			coinRepo.saveAll(coinsToSave);
			machineRepo.save(machine);
		}
		}
		catch(Exception e) {
			throw new UserServiceException("Generic Error");
		}
		return refundCoins;
	}

}
