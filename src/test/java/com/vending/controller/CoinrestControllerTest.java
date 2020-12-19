/**
 * 
 */
package com.vending.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vending.entity.Coin;
import com.vending.entity.Machine;
import com.vending.repository.CoinRepository;
import com.vending.repository.MachineRepository;
import com.vending.service.impl.VendingServiceImpl;

/**
 * @author ayush.a.mittal
 *
 */

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CoinrestControllerTest {

	
	@InjectMocks
	private CoinsRestController coinsRestController;
	
	@Mock
	private MachineRepository machineRepository;
	
	@Mock
	private CoinRepository coinRepository;
	
	@Mock
	private VendingServiceImpl vendingService;
	
	private Machine machine = new Machine();
	
	private MockMvc mockMvc;
	
	
	private ObjectMapper mapper=new ObjectMapper();
	 
	 @Before
	    public void setUp() {
	     this.mockMvc = MockMvcBuilders.standaloneSetup(coinsRestController).build();
	     //ReflectionTestUtils.setField(CoinsRestController.class, "vendingService", new VendingServiceImpl());
	 }
	
	@Test
	public void getCoinsTest() throws Exception {
		Mockito.<Optional<Machine>>when(machineRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		Mockito.when(coinRepository.findByMachineName(Mockito.any())).thenReturn(new ArrayList<Coin>());
		 this.mockMvc.perform(MockMvcRequestBuilders.get("/1/coins")
		           .accept(MediaType.parseMediaType("application/json")))
		           .andExpect(status().isOk())
		           .andExpect(content().contentType("application/json"));
	}
	
	@Test
	public void addInitialCoinsTest() throws Exception {
		Mockito.when(vendingService.addInitialCoins(Mockito.anyString(), Mockito.anyList())).thenReturn(new ArrayList<Coin>());
		 this.mockMvc.perform(MockMvcRequestBuilders.post("/1/coins/addInitialCoins")
				 	.contentType(MediaType.APPLICATION_JSON)
				   .content(mapper.writeValueAsString(new ArrayList<Coin>()))
		           .accept(MediaType.parseMediaType("application/json")))
		           .andExpect(status().isCreated())
		           .andExpect(content().contentType("application/json"));
	}
	
	@Test
	public void addCoinsTest() throws Exception {
		Mockito.<Optional<Machine>>when(machineRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(machine));
		Coin coin=new Coin();
		coin.denomination=200;
		Mockito.when(vendingService.addCoin(Mockito.anyString(), Mockito.any(),Mockito.any())).thenReturn(Optional.of(machine));
		 this.mockMvc.perform(MockMvcRequestBuilders.post("/1/coins/addCoins")
				 	.contentType(MediaType.APPLICATION_JSON)
				   .content(mapper.writeValueAsString(coin))
		           .accept(MediaType.parseMediaType("application/json")))
		           .andExpect(status().isOk())
		           .andExpect(content().contentType("application/json"));
	}
	
}
