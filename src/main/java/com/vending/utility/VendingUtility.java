/**
 * 
 */
package com.vending.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.vending.entity.Coin;
import com.vending.entity.Machine;
import com.vending.service.IVendingServiceData;

/**
 * @author ayush.a.mittal
 *
 */
public class VendingUtility {
	
	private VendingUtility() {
		
	}

	/**
	 * To get a Map of current Coins
	 * 
	 * @param coinsPresent
	 * @return 
	 */
	public static Map<Integer, String> getCoinsMap(Collection<Coin> coinsPresent) {
		Map<Integer, String> coinPresentMap=new HashMap<>();
		for(Coin coinsP: coinsPresent) {
    		coinPresentMap.put(coinsP.getDenomination(), coinsP.getCount()+","+coinsP.getId());
    	}
		return coinPresentMap;
	}
	
	/**
	 * @param coins
	 * @param resultCoins
	 * @param machine
	 * @param coinPresentMap
	 */
	public static void getResultCoins(List<Coin> coins, List<Coin> resultCoins, Machine machine,
			Map<Integer, String> coinPresentMap,IVendingServiceData vendingServiceData) {
		Coin coinToPersist=null;
		for (Coin coin : coins) {
			if(validateCoin(coin)) {
    		if (IntStream.of(Coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.getDenomination())) {
    			if(!coinPresentMap.containsKey(coin.getDenomination())) {
    			machine.setCurrentAmount(machine.getCurrentAmount()+(coin.getDenomination()*coin.getCount()));
    			resultCoins.add(vendingServiceData.saveCoin(new Coin(machine,coin.getDenomination(), coin.getCount())));
    			}else {
    				machine.setCurrentAmount(machine.getCurrentAmount()+(coin.getDenomination()*coin.getCount()));
    				String []keySplit=coinPresentMap.get(coin.getDenomination()).split(",");
    				coinToPersist=vendingServiceData.getOneCoin(Long.valueOf((keySplit[1])));
    				coinToPersist.setCount(coinToPersist.getCount()+coin.getCount());
    				resultCoins.add(vendingServiceData.saveCoin(coinToPersist));
    			}
    			
            }
		}}
	}
	
	public static boolean validateCoin(Coin coin) {
		boolean flag=false;
		if(coin.getCount()>=0) {
			flag=true;
		}
		return flag;
	}
	
	}

