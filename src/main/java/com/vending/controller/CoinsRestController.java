package com.vending.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vending.constants.VendingConstants;
import com.vending.entity.Coin;
import com.vending.entity.RefundAmount;
import com.vending.exception.BadRequestException;
import com.vending.exception.MachineNotFoundException;
import com.vending.exception.UserServiceException;
import com.vending.repository.CoinRepository;
import com.vending.repository.MachineRepository;
import com.vending.service.IVendingService;
import com.vending.service.IVendingServiceData;

/**
 * 
 * @author ayush.a.mittal
 *
 */
@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping(VendingConstants.REQUEST_MAPPING)
public class CoinsRestController {

	private final MachineRepository machineRepository;

	private IVendingService vendingService;
	private IVendingServiceData vendingServiceData;
	Logger logger = LoggerFactory.getLogger(CoinsRestController.class);
	/**
	 * To inject the bean
	 * 
	 * @param machineRepository
	 * @param coinRepository
	 * @param vendingService
	 * @param vendingServiceData
	 */
	@Autowired
	CoinsRestController(MachineRepository machineRepository, CoinRepository coinRepository,IVendingService vendingService,IVendingServiceData vendingServiceData) {
		this.machineRepository = machineRepository;
		this.vendingService=vendingService;
		this.vendingServiceData=vendingServiceData;
	}

	/**
	 * Get list of all coins in the system
	 *
	 * @param machineId the machine name
	 * @return the list of the coins
	 */
	@GetMapping
	public Collection<Coin> getCoins(@PathVariable String machineId) {
		logger.debug("Entering into GET Method of coins");
		validateMachine(machineId);
		logger.debug("Exiting from GET Method of coins");
		return vendingServiceData.findByMachineName(machineId);
		
	}

	/**
	 * Add a new coin into the vending machine
	 *
	 * @param machineId the vending machine name
	 * @param coin      the coin you want to add
	 * @return the machine that the coin was added
	 */
	@PostMapping(value = VendingConstants.ADD_COINS)
	public Optional<?> addCoin(@PathVariable String machineId, @RequestBody Coin coin) throws Exception {
		logger.debug("Entering into POST Method of adding coins in the machine");
		validateMachine(machineId);
		if(!IntStream.of(Coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.getDenomination())) {
			throw new BadRequestException(VendingConstants.NOT_A_VALID_DENOMINATION);
		}
		return machineRepository.findByName(machineId).map(machine -> {
			try {
				return vendingService.addCoin(machineId, coin, machine);
			} catch (Exception e) {
				throw new UserServiceException(VendingConstants.UNEXPECTED_ERROR);
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
	@PostMapping(value = VendingConstants.ADD_INITIAL_COINS)
	public ResponseEntity<?> addInitialCoin(@PathVariable String machineId,@Validated @RequestBody List<Coin> coins) {
		logger.debug("Entering into POST Method of setting up Inital coins in the machine");
		return new ResponseEntity<>(vendingService.addInitialCoins(machineId, coins), HttpStatus.CREATED);
	}

	/**
	 * Return all the money that has been requested for a refund
	 *
	 * @param machineId machine name
	 * @return the list of coins that have been returned
	 * @throws Exception 
	 * @throws UserServiceException 
	 */
	@PostMapping(value = VendingConstants.REFUND)
	public ResponseEntity<?> refund(@PathVariable String machineId, @RequestBody RefundAmount refund) throws UserServiceException, Exception {
		this.validateMachine(machineId);
		return new ResponseEntity<>(vendingService.refundAmount(machineId, refund), HttpStatus.CREATED);
	}

	/**
	 * Check if a given machine name exists
	 *
	 * @param machineId the machine name
	 */
	private void validateMachine(String machineId) throws MachineNotFoundException {
		logger.debug("Entering into method validateMachine for machine");
		try {
			machineRepository.findByName(machineId);
		}
		catch(MachineNotFoundException me) {
			throw new MachineNotFoundException(machineId);
		}
	}
}
