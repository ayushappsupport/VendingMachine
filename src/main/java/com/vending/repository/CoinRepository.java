package com.vending.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vending.entity.Coin;

/**
 * 
 * @author ayush.a.mittal
 *
 */
public interface CoinRepository extends JpaRepository<Coin, Long> {
	/**
	 * Find all coins in a given machine
	 *
	 * @param name
	 * @return
	 */
	Collection<Coin> findByMachineName(String name);
}
