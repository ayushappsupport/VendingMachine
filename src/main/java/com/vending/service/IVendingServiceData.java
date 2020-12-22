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
     * Find a machines with a specific name
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
    /**
     * to save a coin
     * 
     * @param coin
     * @return
     */
    Coin saveCoin(Coin coin);
    /**
     * To save a machine
     * 
     * @param machine
     */
    void saveMachine(Machine machine);
    /**
     * To save and flush the coin data
     * 
     * @param coin
     */
    void saveAndFlushCoin(Coin coin);
    /**
     * To save and flush the machine data
     * 
     * @param machine
     */
    void saveAndFlushMachine(Machine machine);
    /**
     * To save the coins in Bulk
     * 
     * @param coinsToSave
     */
    void saveCoinBulk(List<Coin> coinsToSave);
    
    /**
     * To get a Coin
     * 
     * @param Id
     * @return
     */
    Coin getOneCoin(Long id);
}
