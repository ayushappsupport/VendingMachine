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
import com.vending.repository.CoinRepository;
import com.vending.service.IVendingServiceData;

/**
 * @author ayush.a.mittal
 *
 */
public class VendingUtility {

	/**
	 * To get a Map of current Coins
	 * 
	 * @param coinsPresent
	 * @return 
	 */
	public static Map<Integer, String> getCoinsMap(Collection<Coin> coinsPresent) {
		Map<Integer, String> coinPresentMap=new HashMap<Integer, String>();
		for(Coin coinsP: coinsPresent) {
    		coinPresentMap.put(coinsP.denomination, coinsP.count+","+coinsP.getId());
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
    		if (IntStream.of(Coin.POSSIBLE_VALUES).anyMatch(x -> x == coin.denomination)) {
    			if(!coinPresentMap.containsKey(coin.denomination)) {
    			machine.currentAmount=machine.currentAmount+(coin.denomination*coin.count);
    			resultCoins.add(vendingServiceData.saveCoin(new Coin(machine,coin.denomination, coin.count)));
    			}else {
    				machine.currentAmount=machine.currentAmount+(coin.denomination*coin.count);
    				String keySplit[]=coinPresentMap.get(coin.denomination).split(",");
    				coinToPersist=vendingServiceData.getOneCoin(Long.valueOf((keySplit[1])));
    				coinToPersist.count=coinToPersist.count+coin.count;
    				resultCoins.add(vendingServiceData.saveCoin(coinToPersist));
    			}
    			
            }
		}
	}
}
