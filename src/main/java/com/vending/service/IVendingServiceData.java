/**
 * 
 */
package com.vending.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.vending.entity.Coin;
import com.vending.entity.Machine;

/**
 * @author ayush.a.mittal
 *
 */
public interface IVendingServiceData {

	Collection<Coin> findByMachineName(String name);
	
	/**
     * Find all machines with a specific name
     *
     * @param name name of the machine
     * @return if the machine exists
     */
    Optional<Machine> findByName(String name);

    /**
     * Find the machine with a a specific name
     *
     * @param name name of the machine
     * @return the machine
     */
    Machine findOneByName(String name);
    
    Coin saveCoin(Coin coin);
    
    void saveMachine(Machine machine);
    
    void saveAndFlushCoin(Coin coin);
    
    void saveAndFlushMachine(Machine machine);
    
    void saveCoinBulk(List<Coin> coinsToSave);
    
    Coin getOneCoin(Long Id);
}
