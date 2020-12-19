package com.vending.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Lloyd on 02/11/2017.
 */
@Entity
public class Coin {

    /**
     * All the possible values of coin 50 = 50p, 200 = £2
     */
    @JsonIgnore
    public static final int [] POSSIBLE_VALUES = {200,100,50,20,10,5,2,1};

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.ALL})
    private Machine machine;

    @Id
    @GeneratedValue
    private Long id;

    /** The unique id of the coin
     * @return the unique id
     */
    public Long getId() {
        return id;
    }
    
    public Long setId(Long id) {
        return this.id=id;
    }


    public int denomination;
    public int count;

    /** Get the value of the type of coin
     * @return the value (e.g. 50 = 50p , 200 = £2)
     */
    public int getDenomination() {
        return denomination;
    }

    /** Get the number of coins available
     * @return the number of coins
     */
    public int getCount() {
        return count;
    }

    /** Coin constructor
     *
     * @param machine the vending machine being used
     * @param value how much the coin is worth
     * @param amount how many coins you have
     */
    public Coin(Machine machine, int denomination, int count) {
        this.machine = machine;
        this.denomination = denomination;
        this.count = count;
    }

    /** Coin constructor
     *
     * @param value how much the coin is worth
     * @param amount how many coins you have
     */
    public Coin(int denomination, int count){
        this.denomination = denomination;
        this.count = count;
    }

    public Coin(){
        //jpa only
    }
}
