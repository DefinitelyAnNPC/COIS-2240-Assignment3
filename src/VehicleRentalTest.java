import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleRentalTest {

    private Vehicle vehicle;

    @BeforeEach
    public void setUp() {
        vehicle = new Car(null, null, 0, 0);
    }
	
	@Test
	void testLicensePlateValidation () {
        // Valid plates
		System.out.println("Checking 1st valid: " + vehicle.isValidPlate("AAA100"));
        assertTrue(vehicle.isValidPlate("AAA100"));
		System.out.println("Checking 2nd valid: " + vehicle.isValidPlate("ABC567"));
        assertTrue(vehicle.isValidPlate("ABC567"));
        System.out.println("Checking 3rd valid: " + vehicle.isValidPlate("ZZZ999"));
        assertTrue(vehicle.isValidPlate("ZZZ999"));

        // Invalid plates
        System.out.println("Checking 1st invalid: " + vehicle.isValidPlate(""));
        assertFalse(vehicle.isValidPlate(""));
        System.out.println("Checking 2nd invalid: " + vehicle.isValidPlate(null));
        assertFalse(vehicle.isValidPlate(null)); 
        System.out.println("Checking 3rd invalid: " + vehicle.isValidPlate("AAA1000"));
        assertFalse(vehicle.isValidPlate("AAA1000"));
        System.out.println("Checking 4th invalid: " + vehicle.isValidPlate("ZZZ99"));
        assertFalse(vehicle.isValidPlate("ZZZ99"));

        // Test setLicensePlate() if it throws IllegalArgumentException like it should
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("ZZZ99"));
	}

}
