package com.vending.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vending.entity.Coin;
import com.vending.entity.RefundAmount;
import com.vending.exception.BadRequestException;
import com.vending.exception.CoinNotFoundException;
import com.vending.exception.MachineNotFoundException;
import com.vending.exception.UserServiceException;
import com.vending.repository.CoinRepository;
import com.vending.repository.MachineRepository;
import com.vending.service.IVendingService;

/**
 * 
 * @author ayush.a.mittal
 *
 */
@RestController
@RequestMapping("/{machineId}/coins")
public class CoinsRestController {

	private final MachineRepository machineRepository;
	private final CoinRepository coinRepository;

	private IVendingService vendingService;

	/**
	 * Coins Rest Controller constructor
	 *
	 * @param productRepository product repository
	 * @param machineRepository machine repository
	 * @param coinRepository    coin repository
	 */
	@Autowired
	CoinsRestController(MachineRepository machineRepository, CoinRepository coinRepository,IVendingService vendingService) {
		this.machineRepository = machineRepository;
		this.coinRepository = coinRepository;
		this.vendingService=vendingService;
	}

	/**
	 * Get list of all coins in the system
	 *
	 * @param machineId the machine name
	 * @return the list of the coins
	 */
	@RequestMapping(method = RequestMethod.GET)
	Collection<Coin> getCoins(@PathVariable String machineId) {
		validateMachine(machineId);
		return coinRepository.findByMachineName(machineId);
	}

	/**
	 * Add a new coin into the vending machine
	 *
	 * @param machineId the vending machine name
	 * @param coin      the coin you want to add
	 * @return the machine that the coin was added
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/addCoins")
	Optional<?> addCoin(@PathVariable String machineId, @RequestBody Coin coin) throws Exception {
		validateMachine(machineId);
		if(!IntStream.of(Coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.denomination)) {
			throw new BadRequestException("Not a Valid Denomination");
		}
		return machineRepository.findByName(machineId).map(machine -> {
			try {
				return vendingService.addCoin(machineId, coin, machine);
			} catch (Exception e) {
				throw new UserServiceException("Unexpected Error");
			}
		});
	}

	/**
	 * Initial Setup of the Machine to add the Coins
	 * 
	 * @param machineId
	 * @param coins
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/addInitialCoins")
	ResponseEntity<?> addInitialCoin(@PathVariable String machineId, @RequestBody List<Coin> coins) {
		return new ResponseEntity<>(vendingService.addInitialCoins(machineId, coins), HttpStatus.CREATED);
	}

	/**
	 * Return all the money that has not been spent in the system
	 *
	 * @param machineId machine name
	 * @return the list of coins that have been returned
	 * @throws Exception 
	 * @throws UserServiceException 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/refund")
	ResponseEntity<?> refund(@PathVariable String machineId, @RequestBody RefundAmount refund) throws UserServiceException, Exception {
		this.validateMachine(machineId);
		return new ResponseEntity<>(vendingService.refundAmount(machineId, refund), HttpStatus.CREATED);
	}

	/**
	 * Get a specific coin
	 *
	 * @param machineId machine name
	 * @param coinValue coin value
	 * @return the details of coins in that vending machine
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{coinValue}")
	Coin getCoin(@PathVariable String machineId, @PathVariable int coinValue) {
		this.validateMachine(machineId);
		final Coin[] foundCoin = { null };
		this.coinRepository.findByMachineName(machineId).forEach((Coin coin) -> {
			if (coin.denomination == coinValue) {
				foundCoin[0] = coin;
			}
		});
		if (foundCoin[0] != null) {
			return foundCoin[0];
		} else {
			throw new CoinNotFoundException(coinValue);
		}
	}

	/**
	 * Check if a given machine name exists
	 *
	 * @param machineId the machine name
	 */
	private void validateMachine(String machineId) {
		machineRepository.findByName(machineId).orElseThrow(() -> new MachineNotFoundException(machineId));
	}
}
