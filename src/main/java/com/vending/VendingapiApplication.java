package com.vending;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VendingapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendingapiApplication.class, args);
	}

	/*
	 * @Bean CommandLineRunner init(MachineRepository machineRepository,
	 * ProductRepository productRepository, CoinRepository coinRepository) { return
	 * (evt) -> Arrays.asList( "1,2".split(",")) .forEach( a -> { // Create the new
	 * machine Machine machine = machineRepository.save(new Machine(a, 0));
	 * 
	 * // Add 2 default products productRepository.save(new Product(machine,
	 * "Coca Cola", 120,10)); productRepository.save(new Product(machine, "Haribo",
	 * 300, 5));
	 * 
	 * // Add some cash float to the machine coinRepository.save(new Coin(machine,
	 * 50, 6)); coinRepository.save(new Coin(machine, 100, 2));
	 * coinRepository.save(new Coin(machine, 20, 4)); coinRepository.save(new
	 * Coin(machine, 1, 10)); }); }
	 */
}
