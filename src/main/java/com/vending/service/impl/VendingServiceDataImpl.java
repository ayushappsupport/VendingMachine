/**
 * 
 */
package com.vending.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.vending.entity.Coin;
import com.vending.entity.Machine;
import com.vending.repository.CoinRepository;
import com.vending.repository.MachineRepository;
import com.vending.service.IVendingServiceData;

/**
 * @author ayush.a.mittal
 *
 */
@Component
public class VendingServiceDataImpl implements IVendingServiceData {
	
	@Autowired
	private CoinRepository coinRepo;
	
	@Autowired
	private MachineRepository machineRepo;

	@Cacheable(value = "vendingmachine", key = "#name")
	@Override
	public Collection<Coin> findByMachineName(String name) {
		return coinRepo.findByMachineName(name);
	}

	@Override
	public Optional<Machine> findByName(String name) {
		return machineRepo.findByName(name);
	}

	@Override
	public Machine findOneByName(String name) {
		return machineRepo.findOneByName(name);
	}

	@Override
	public Coin saveCoin(Coin coin) {
		return coinRepo.save(coin);
		
	}

	@Override
	@CacheEvict(value="vendingmachine", key="#machine.name")
	public void saveMachine(Machine machine) {
		machineRepo.save(machine);
		
	}

	@Override
	public void saveAndFlushCoin(Coin coin) {
		coinRepo.saveAndFlush(coin);
		
	}

	@Override
	@CacheEvict(value="vendingmachine", key="#machine.name")
	public void saveAndFlushMachine(Machine machine) {
		machineRepo.saveAndFlush(machine);
		
	}

	@Override
	public void saveCoinBulk(List<Coin> coinsToSave) {
		coinRepo.saveAll(coinsToSave);
		
	}

	@Override
	public Coin getOneCoin(Long Id) {
		return coinRepo.findById(Id).get();
	}

}
