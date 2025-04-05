import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
	private static RentalSystem instance;
	private List<Vehicle> vehicles = new ArrayList<>();
	private List<Customer> customers = new ArrayList<>();
	private RentalHistory rentalHistory = new RentalHistory();

	private File veh = new File("vehicles.txt");
	private File cus = new File("customers.txt");;
	private File rec = new File("rental_records.txt");;

	private BufferedWriter writer;

	private RentalSystem() {
		loadData();
	}

	public boolean addVehicle(Vehicle vehicle) {
		if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
			System.out.println("Vehicle with plate [" + vehicle.getLicensePlate() + "] already exists.");
			return false;
		}
		vehicles.add(vehicle);
		saveVehicle(vehicle);
		return true;
	}

	public boolean addCustomer(Customer customer) {
		if (findCustomerById(customer.getCustomerId()) != null) {
			System.out.println("Customer [" + customer.getCustomerId() + "] already exists.");
			return false;
		}
		customers.add(customer);
		saveCustomer(customer);
		return true;
	}

	public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
		if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
			vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
			rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
			System.out.println("\nVehicle rented to " + customer.getCustomerName() + "\n");
			saveRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
		} else {
			System.out.println("\nVehicle is not available for renting.\n");
		}
	}

	public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
		if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
			vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
			rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
			System.out.println("\nVehicle returned by " + customer.getCustomerName() + "\n");
			saveRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
		} else {
			System.out.println("\nVehicle is not rented.\n");
		}
	}

	public void displayAvailableVehicles() {
		System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
		System.out.println("---------------------------------------------------------------------------------");

		for (Vehicle v : vehicles) {
			if (v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
				System.out.println(
						"|     " + (v instanceof Car ? "Car          " : (v instanceof Motorcycle ? "Motorcycle   " : "Truck        ")) + "|\t" + v.getLicensePlate()
								+ "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
				
			}
		}
		System.out.println();
	}

	public void displayAllVehicles() {
		for (Vehicle v : vehicles) {
			System.out.println("  " + v.getInfo());
		}
	}

	public void displayAllCustomers() {
		for (Customer c : customers) {
			System.out.println("  " + c.toString());
		}
	}

	public void displayRentalHistory() {
		for (RentalRecord record : rentalHistory.getRentalHistory()) {
			System.out.println(record.toString());
		}
	}

	public Vehicle findVehicleByPlate(String plate) {
		for (Vehicle v : vehicles) {
			if (v.getLicensePlate().equalsIgnoreCase(plate)) {
				return v;
			}
		}
		return null;
	}

	public Customer findCustomerById(int id) {
		for (Customer c : customers)
			if (c.getCustomerId() == id)
				return c;
		return null;
	}

	public Customer findCustomerByName(String name) {
		for (Customer c : customers)
			if (c.getCustomerName().equalsIgnoreCase(name))
				return c;
		return null;
	}

	public static RentalSystem getInstance() {
		if (instance == null) {
			instance = new RentalSystem();
		}
		return instance;
	}

	public void saveVehicle(Vehicle vehicle) {
		try {
			writer = new BufferedWriter(new FileWriter(veh, true));
			writer.append(vehicle.getInfo());
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCustomer(Customer customer) {
		try {
			writer = new BufferedWriter(new FileWriter(cus, true));
			writer.append(customer.toString());
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveRecord(RentalRecord record) {
		try {
			writer = new BufferedWriter(new FileWriter(rec, true));
			writer.append(record.toString());
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadData() {
		///// vehicle load start
		try (BufferedReader reader = new BufferedReader(new FileReader(veh))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\|");

				String plate = parts[1].trim();
				String make = parts[2].trim();
				String model = parts[3].trim();
				int year = Integer.parseInt(parts[4].trim());
				//String status = parts[5].trim();
				String attribute = parts[7].trim();

				String[] attrParts = attribute.split(":");

				String attrType = attrParts[0].trim();
				String attrValue = attrParts[1].trim();

				if (attrType.equalsIgnoreCase("Seats")) {
					int seats = Integer.parseInt(attrValue);
					Car car = new Car(make, model, year, seats);
					car.setLicensePlate(plate);
					vehicles.add(car);
				} else if (attrType.equalsIgnoreCase("Sidecar")) {
					boolean sideCar = Boolean.parseBoolean(attrValue);
					Motorcycle moto = new Motorcycle(make, model, year, sideCar);
					moto.setLicensePlate(plate);
					vehicles.add(moto);
				} else if (attrType.equalsIgnoreCase("Cargo Capacity")) {
					double cargoCapacity = Double.parseDouble(attrValue);
					Truck truck = new Truck(make, model, year, cargoCapacity);
					truck.setLicensePlate(plate);
					vehicles.add(truck);
				} else {
					System.err.println("Unknown vehicle type in line: " + line);
				}
			}
		} catch (Exception e) {
			System.err.println("Error loading Vehicles: " + e.getMessage());
			System.err.println("History may not exist yet\n");
		}
		///// Vehicle load end

		///// Customer load start
		try (BufferedReader reader = new BufferedReader(new FileReader(cus))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\|");

				String idSegment = parts[0].trim();
				String nameSegment = parts[1].trim();

				String[] idParts = idSegment.split(":");
				String[] nameParts = nameSegment.split(":");

				int id = Integer.parseInt(idParts[1].trim());
				String name = nameParts[1].trim();

				customers.add(new Customer(id, name));
			}
		} catch (Exception e) {
			System.err.println("Error loading Customers: " + e.getMessage());
			System.err.println("History may not exist yet\n");
		}
		///// Customer load end

		///// Rental_record load start
		try (BufferedReader reader = new BufferedReader(new FileReader(rec))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\|");

				String statusSegment = parts[0].trim();
				String plateSegment = parts[1].trim();
				String cusNameSegment = parts[2].trim();
				String dateSegment = parts[3].trim();
				String feeSegment = parts[4].trim();

				String[] plateParts = plateSegment.split(":");
				String[] cusNameParts = cusNameSegment.split(":");
				String[] dateParts = dateSegment.split(":");
				String[] feeParts = feeSegment.split("\\$");

				Vehicle recVehicle = findVehicleByPlate(plateParts[1].trim());	
				Customer recCustomer = findCustomerByName(cusNameParts[1].trim());
				LocalDate date = LocalDate.parse(dateParts[1].trim());
				Double fee = Double.parseDouble(feeParts[1].trim()) ;
				
				switch(statusSegment) {
					case "RENT" : 
						recVehicle.setStatus(Vehicle.VehicleStatus.RENTED);
						break;
					case "RETURN":
						recVehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
						break;
				}

				rentalHistory.addRecord(new RentalRecord(recVehicle, recCustomer, date, fee, statusSegment));
			}
		} catch (Exception e) {
			System.err.println("Error loading Rental_Record: " + e.getMessage());
			System.err.println("History may not exist yet\n");
		}
		///// Rental_record load end
	}
}