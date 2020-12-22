/**
 * 
 */
package com.vending.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.vending.entity.Coin;
import com.vending.entity.Machine;
import com.vending.entity.RefundAmount;

/**
 * @author ayush.a.mittal
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class VendingServiceImplTest {
	
	@InjectMocks
	VendingServiceImpl vendingService;
	
	@Mock
	private VendingServiceDataImpl vendingServiceData;
	
	private List<Coin> coins;
	private List<Coin> coinsPresent;
	private Machine machine = new Machine();

	@SuppressWarnings("deprecation")
	@Before
    public void setUp() {
	MockitoAnnotations.initMocks(this);
	coins=new ArrayList<Coin>();
	coinsPresent=new ArrayList<Coin>();
	Machine machine=new Machine();
	machine.name="1";
	coins.add(new Coin(machine,200, 10));
	coins.add(new Coin(machine,50, 10));
	Coin coin1=new Coin(machine,200, 10);
	Coin coin2=new Coin(machine,10, 10);
	coin1.setId(1L);
	coin2.setId(2L);
	coinsPresent.add(coin1);
	coinsPresent.add(coin2);
	
	}
	@Test
	public void addInitialCoinsTest() throws Exception {
		Coin getOneCoin=new Coin();
		getOneCoin.setId(1L);
		getOneCoin.denomination=200;
		getOneCoin.count=10;
		Mockito.<Optional<Machine>>when(vendingServiceData.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		Mockito.when(vendingServiceData.findByMachineName(Mockito.any())).thenReturn(coinsPresent);
		Mockito.when(vendingServiceData.getOneCoin(Mockito.any())).thenReturn(getOneCoin);
		Mockito.when(vendingServiceData.saveCoin(Mockito.any())).thenReturn(getOneCoin);
		Mockito.doNothing().when(vendingServiceData).saveMachine(machine);
		vendingService.addInitialCoins("1", coins);
	}
	
	@Test
	public void addCoinsTest() throws Exception {
		Coin getOneCoin=new Coin();
		getOneCoin.setId(1L);
		getOneCoin.denomination=200;
		getOneCoin.count=10;
		Mockito.<Optional<Machine>>when(vendingServiceData.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		Mockito.when(vendingServiceData.findByMachineName(Mockito.any())).thenReturn(coinsPresent);
		vendingService.addCoin("1", getOneCoin, machine);
	}
	
	/*
	 * @Test public void addInitialCoinsMachineNotPresentTest() throws Exception {
	 * Coin getOneCoin=new Coin(); getOneCoin.setId(1L);
	 * getOneCoin.denomination=200; getOneCoin.count=10;
	 * Mockito.<Optional<Machine>>when(vendingServiceData.findByName(Mockito.
	 * anyString())).thenReturn(Optional.of(new Machine()));
	 * Mockito.when(vendingServiceData.findByMachineName(Mockito.any())).thenReturn(
	 * coinsPresent);
	 * Mockito.when(vendingServiceData.getOneCoin(Mockito.any())).thenReturn(
	 * getOneCoin);
	 * Mockito.when(vendingServiceData.saveCoin(Mockito.any())).thenReturn(
	 * getOneCoin);
	 * Mockito.doNothing().when(vendingServiceData).saveMachine(machine);
	 * vendingService.addInitialCoins("1", coins); }
	 */
	
	@Test
	public void addCoinsNewTest() throws Exception {
		Coin getOneCoin=new Coin();
		getOneCoin.setId(1L);
		getOneCoin.denomination=2;
		getOneCoin.count=10;
		Mockito.<Optional<Machine>>when(vendingServiceData.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		Mockito.when(vendingServiceData.findByMachineName(Mockito.any())).thenReturn(coinsPresent);
		vendingService.addCoin("1", getOneCoin, machine);
	}
	
	@Test
	public void refundTest() throws Exception {
		RefundAmount refund=new RefundAmount();
		refund.setRefundAmount(50);
		Coin getOneCoin=new Coin();
		getOneCoin.setId(1L);
		getOneCoin.denomination=2;
		getOneCoin.count=10;
		machine.currentAmount=1000;
		Mockito.<Optional<Machine>>when(vendingServiceData.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		Mockito.when(vendingServiceData.findByMachineName(Mockito.any())).thenReturn(coinsPresent);
		vendingService.refundAmount("1", refund);
	}
	
	@Test(expected= Exception.class)
	public void refundWhenZeroTest() throws Exception {
		RefundAmount refund=new RefundAmount();
		refund.setRefundAmount(0);
		Coin getOneCoin=new Coin();
		getOneCoin.setId(1L);
		getOneCoin.denomination=2;
		getOneCoin.count=10;
		machine.currentAmount=1000;
		Mockito.<Optional<Machine>>when(vendingServiceData.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		vendingService.refundAmount("1", refund);
	}
	@Test(expected= Exception.class)
	public void refundWhenGreaterThanMachineAmountTest() throws Exception {
		RefundAmount refund=new RefundAmount();
		refund.setRefundAmount(1100);
		Coin getOneCoin=new Coin();
		getOneCoin.setId(1L);
		getOneCoin.denomination=2;
		getOneCoin.count=10;
		machine.currentAmount=1000;
		Mockito.<Optional<Machine>>when(vendingServiceData.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		vendingService.refundAmount("1", refund);
	}
	@Test(expected= Exception.class)
	public void refundInsufficientCoinsTest() throws Exception {
		RefundAmount refund=new RefundAmount();
		refund.setRefundAmount(110);
		Coin getOneCoin=new Coin();
		getOneCoin.setId(1L);
		getOneCoin.denomination=2;
		getOneCoin.count=10;
		machine.currentAmount=1000;
		Mockito.<Optional<Machine>>when(vendingServiceData.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		vendingService.refundAmount("1", refund);
	}
	
}
