package com.vending.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 
 * @author ayush.a.mittal
 *
 */
@Entity
public class Machine {

	@OneToMany(mappedBy = "machine")
	private Set<Coin> coinsList = new HashSet<>();

	private String name;
	private int currentAmount;

	@Id
	@GeneratedValue
	private Long id;

	public Long getId() {
		return id;
	}

	/**
	 * Machine constructor
	 * 
	 * @param name          the name of the vending machine
	 * @param currentAmount the amount of money in the machine
	 */
	public Machine(String name, int currentAmount) {
		this.name = name;
		this.currentAmount = currentAmount;
	}

	public Machine() { // jpa only
	}

	/**
	 * Get the name of the vending machine
	 * 
	 * @return the name of the vending machine
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the list of coins in the system
	 * 
	 * @return the list of coins and how many there are
	 */
	public Set<Coin> getCoinsList() {
		return coinsList;
	}

	/**
	 * Get the current amount of money stored by the vending machine that can be
	 * used to purchase things
	 * 
	 * @return the current spending money amount
	 */
	public int getCurrentAmount() {
		return currentAmount;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCoinsList(Set<Coin> coinsList) {
		this.coinsList = coinsList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}

}
