import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(OrderAnnotation.class)
class VehicleRentalTest {

    private Vehicle vehicle;
    private Customer customer;
    private RentalSystem rentalSystem;
    
    @BeforeEach
    public void setUp() {
        vehicle = new Car("Toyota", "Corolla", 2010, 5);
        customer = new Customer(123, "JUnitTesting");
        rentalSystem = RentalSystem.getInstance();
    }
	
	@Test
	@Order(1)
	void testLicensePlateValidation() {
        // Valid plates
		System.out.println("1. Is 1st valid(True)?: " + vehicle.isValidPlate("AAA100"));
        assertTrue(vehicle.isValidPlate("AAA100"));
		System.out.println("1. Is 2nd valid(True)?: " + vehicle.isValidPlate("ABC567"));
        assertTrue(vehicle.isValidPlate("ABC567"));
        System.out.println("1. Is 3rd valid(True)?: " + vehicle.isValidPlate("ZZZ999"));
        assertTrue(vehicle.isValidPlate("ZZZ999"));

        // Invalid plates
        System.out.println("\n1. Is 1st invalid(false): " + vehicle.isValidPlate(""));
        assertFalse(vehicle.isValidPlate(""));
        System.out.println("1. Is 2nd invalid(false): " + vehicle.isValidPlate(null));
        assertFalse(vehicle.isValidPlate(null)); 
        System.out.println("1. Is 3rd invalid(false): " + vehicle.isValidPlate("AAA1000"));
        assertFalse(vehicle.isValidPlate("AAA1000"));
        System.out.println("1. Is 4th invalid(false): " + vehicle.isValidPlate("ZZZ99"));
        assertFalse(vehicle.isValidPlate("ZZZ99"));

        // Test setLicensePlate() if it throws IllegalArgumentException like it should
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("ZZZ99"));
	}

	@Test
	@Order(2)
	void testRentAndReturnVehicle() {
		// Is initially available
		System.out.println("\n2. Checking vehicle available: " + vehicle.getStatus().equals(Vehicle.VehicleStatus.AVAILABLE));
		assertEquals(vehicle.getStatus(), Vehicle.VehicleStatus.AVAILABLE);
		
		
		// RentVehicle method assertions
		// Try to rent
		System.out.print("\n2. Try rent: ");
		boolean rented = rentalSystem.rentVehicle(vehicle, customer, null, 0);
		// Was it borrowed
		assertTrue(rented);
		// Is it marked as rented
		System.out.println("2. Vehicle status avaiable?: " + vehicle.getStatus().equals(Vehicle.VehicleStatus.RENTED));
		assertEquals(vehicle.getStatus(), Vehicle.VehicleStatus.RENTED);
		
		// Try to rent again
		System.out.print("2. Try rent: ");
		boolean rentAgain = rentalSystem.rentVehicle(vehicle, customer, null, 0);
		assertFalse(rentAgain);
		
		
		// ReturnVehicle method assertions
		// Try to return
		System.out.print("2. Try return: ");
		boolean returned = rentalSystem.returnVehicle(vehicle, customer, null, 0);
		// Was it returned
		assertTrue(returned);
		// Is it marked as available
		System.out.println("2. Vehicle status avaiable?: " + vehicle.getStatus().equals(Vehicle.VehicleStatus.AVAILABLE));
		assertEquals(vehicle.getStatus(), Vehicle.VehicleStatus.AVAILABLE);
		
		// Try to return again
		System.out.print("2. Try return: ");
		boolean returnAgain = rentalSystem.returnVehicle(vehicle, customer, null, 0);
		assertFalse(returnAgain);
	}
}
