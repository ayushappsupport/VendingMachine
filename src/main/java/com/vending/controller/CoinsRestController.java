package com.vending.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.vending.entity.Machine;
import com.vending.entity.RefundAmount;
import com.vending.repository.CoinRepository;
import com.vending.repository.MachineRepository;

/**
 * Created by Lloyd on 02/11/2017.
 */
@RestController
@RequestMapping("/{machineId}/coins")
public class CoinsRestController {

    private final MachineRepository machineRepository;
    private final CoinRepository coinRepository;

    /**
     * Coins Rest Controller constructor
     *
     * @param productRepository product repository
     * @param machineRepository machine repository
     * @param coinRepository    coin repository
     */
    @Autowired
    CoinsRestController(
                        MachineRepository machineRepository,
                        CoinRepository coinRepository) {
        this.machineRepository = machineRepository;
        this.coinRepository = coinRepository;
    }

    /**
     * Get list of all coins in the system
     *
     * @param machineId the machine name
     * @return the list of the coins
     */
    @RequestMapping(method = RequestMethod.GET)
    Collection<Coin> getCoins(@PathVariable String machineId) {
        this.validateMachine(machineId);
        return this.coinRepository.findByMachineName(machineId);
    }

    /**
     * Add a new coin into the vending machine
     *
     * @param machineId the vending machine name
     * @param coin      the coin you want to add
     * @return the machine that the coin was added
     */
    @RequestMapping(method = RequestMethod.POST ,value = "/addCoins")
    Optional<?> addCoin(@PathVariable String machineId, @RequestBody Coin coin) {
    	
    	System.out.println("IN Controller");
        this.validateMachine(machineId);

        return this.machineRepository
                .findByName(machineId)
                .map(machine -> {
                    final boolean[] coinFound = {false};
                    // Increase the amount of coins if the machine already has seen that coin
                    this.coinRepository.findByMachineName(machineId).forEach(machineCoin -> {
                        if (coin.denomination == machineCoin.denomination) {
                        	machineCoin.count=machineCoin.count+1;
                            //coin.setId(machineCoin.getId());
                            coinFound[0] = true;
                            this.coinRepository.save(machineCoin);
                        }
                    });

                    // Else add the coin to the repository
                    if (!coinFound[0] && IntStream.of(coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.denomination)) {
                        this.coinRepository.saveAndFlush(new Coin(machine, coin.denomination, 1));
                        coinFound[0] = true;
                    }

                    if (coinFound[0]) {
                        machine.currentAmount += coin.denomination;
                    }

                    this.machineRepository.saveAndFlush(machine);

                    return this.machineRepository.findByName(machineId);
                });
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/addInitialCoins")
    ResponseEntity<?> addInitialCoin(@PathVariable String machineId, @RequestBody List<Coin> coins) {
    	List<Coin> resultCoins=new ArrayList<Coin>();
    	Map<Integer, String> coinPresentMap=new HashMap<Integer, String>();
    	Machine machine=null;
    	Optional<Machine> machineopt=this.machineRepository.findByName(machineId);
    	if(machineopt.isPresent()) {
    		machine=machineopt.get();
    	}else {
    		machine=new Machine(machineId,0);
    	}
    	Collection<Coin> coinsPresent=this.coinRepository.findByMachineName(machineId);
    	for(Coin coinsP: coinsPresent) {
    		coinPresentMap.put(coinsP.denomination, coinsP.count+","+coinsP.getId());
    	}
    	for (Coin coin : coins) {
    		if (IntStream.of(Coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.denomination)) {
    			if(!coinPresentMap.containsKey(coin.denomination)) {
    			machine.currentAmount=machine.currentAmount+(coin.denomination*coin.count);
    			resultCoins.add(this.coinRepository.save(new Coin(machine,coin.denomination, coin.count)));
    			}else {
    				machine.currentAmount=machine.currentAmount+(coin.denomination*coin.count);
    				String keySplit[]=coinPresentMap.get(coin.denomination).split(",");
    				Coin coinToPersist=this.coinRepository.getOne(Long.valueOf((keySplit[1])));
    				coinToPersist.count=coinToPersist.count+coin.count;
    				//coin.setId(Long.valueOf((keySplit[1])));
    				resultCoins.add(this.coinRepository.save(coinToPersist));
    			}
    			
            }
		}
    	this.machineRepository.save(machine);
    	return new ResponseEntity<>(resultCoins,  HttpStatus.CREATED);
    }
    

    /**
     * Return all the money that has not been spent in the system
     *
     * @param machineId machine name
     * @return the list of coins that have been returned
     */
    @RequestMapping(method = RequestMethod.POST, value = "/refund")
    List<Coin> refund(@PathVariable String machineId,@RequestBody RefundAmount refund) {
        this.validateMachine(machineId);

        final int[] refundTotal = new int[1];
        refundTotal[0]=refund.getRefundAmount();
        
        Machine machine=this.machineRepository.findByName(machineId).get();
        int initialMachineAmount=machine.currentAmount;
            if(refundTotal[0]>initialMachineAmount) {
            	 return new ArrayList<Coin>();
            }
        List<Coin> refundCoins = new ArrayList<>();
        List<Coin> coinsToSave=new ArrayList<Coin>();
        
        //
        for (int value : Coin.POSSIBLE_VALUES) {
            this.coinRepository.findByMachineName(machineId).forEach(coin -> {
                if (value == coin.denomination && coin.count > 0 && coin.denomination <= refundTotal[0]) {
                    double max_coins = Math.min(Math.floor(refundTotal[0] / coin.denomination), coin.count);
                    coin.count -= max_coins;
                    coinsToSave.add(coin);
                    //this.coinRepository.saveAndFlush(coin);
                    refundCoins.add(new Coin(coin.denomination, (int) max_coins));
                    refundTotal[0] -= (int) max_coins * coin.denomination;
                }
            });
        }
        if(refundTotal[0]>0) {
        	return new ArrayList<Coin>();
        }else {
        	machine.currentAmount=machine.currentAmount-refund.getRefundAmount();
        	this.coinRepository.saveAll(coinsToSave);
        	this.machineRepository.save(machine);
        }
        // To DO
        // to minus total from Machine Amount
        return refundCoins;
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
        final Coin[] foundCoin = {null};
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
        this.machineRepository.findByName(machineId).orElseThrow(
                () -> new MachineNotFoundException(machineId));
    }
}
