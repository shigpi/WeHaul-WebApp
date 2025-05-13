package com.wehaul.config;

import com.wehaul.model.TruckType;
import com.wehaul.service.TruckTypeService; 

import java.sql.Connection;
import java.sql.Date; 
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom; // More concise random generation

public class DatabaseSetupUtil {

    // Counter to help generate unique-ish license plates during a single run
    private static int licensePlateCounter = 1;

    /**
     * Populates the 'trucks' table with 5 to 7 sample trucks for each existing truck type.
     * WARNING: This method generates sample data and assumes the 'truck_types' table is populated.
     *          License plate uniqueness is based on a simple counter within this run.
     *          Running this multiple times without clearing the trucks table will likely
     *          result in duplicate license plate errors if the counter resets.
     */
    public static void addSampleTrucks() {
        TruckTypeService truckTypeService = new TruckTypeService();
        List<TruckType> truckTypes;
        Connection conn = null;
        PreparedStatement ps = null;
        int totalTrucksAdded = 0;
        int typesProcessed = 0;

        try {
            // 1. Fetch all existing truck types
            truckTypes = truckTypeService.getAllTruckTypes();
            if (truckTypes == null || truckTypes.isEmpty()) {
                System.out.println("No truck types found in the database. Cannot add sample trucks.");
                return;
            }
            System.out.println("Found " + truckTypes.size() + " truck types. Adding sample trucks...");

            // 2. Prepare SQL Insert Statement
            String sql = "INSERT INTO trucks " +
                         "(type_id, license_plate, make, model, year, mileage, last_maintenance_date, status, current_location) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            conn = DbConfig.getDbConnection();
            // 3. Use Transaction for batch insert efficiency and atomicity
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);

            Random random = new Random();

            // 4. Loop through each truck type
            for (TruckType type : truckTypes) {
                typesProcessed++;
                System.out.println("Processing Type: ID=" + type.getTypeId() + ", Name=" + type.getName());

                // 5. Determine how many trucks to add for this type (5, 6, or 7)
                int trucksToAdd = 5 + random.nextInt(3); // Generates 0, 1, or 2, adds to 5
                System.out.println(" -> Adding " + trucksToAdd + " trucks...");

                // 6. Inner loop to create trucks for the current type
                for (int i = 0; i < trucksToAdd; i++) {
                    // 7. Generate Sample Data
                    String licensePlate = generateSampleLicensePlate(type.getTypeId());
                    String[] makeModel = generateSampleMakeModel(type.getName());
                    int year = ThreadLocalRandom.current().nextInt(2015, 2024); // Year between 2015 and 2023
                    int mileage = ThreadLocalRandom.current().nextInt(5000, 150001); // Mileage between 5k and 150k
                    // 10% chance of being 'maintenance', otherwise 'available'
                    String status = (random.nextInt(10) == 0) ? "maintenance" : "available";
                    Date lastMaintenanceDate = (status.equals("maintenance")) ? generateRandomPastDate(1, 6) : null; // Maint date within last 6 months if status is maintenance
                    String currentLocation = getRandomLocation();

                    // 8. Set parameters for the PreparedStatement
                    ps.setInt(1, type.getTypeId());
                    ps.setString(2, licensePlate);
                    ps.setString(3, makeModel[0]); // Make
                    ps.setString(4, makeModel[1]); // Model
                    ps.setInt(5, year);
                    ps.setInt(6, mileage);
                    if (lastMaintenanceDate != null) {
                        ps.setDate(7, lastMaintenanceDate);
                    } else {
                        ps.setNull(7, java.sql.Types.DATE);
                    }
                    ps.setString(8, status);
                    ps.setString(9, currentLocation);

                    // 9. Add to batch
                    ps.addBatch();
                    totalTrucksAdded++;
                } // End inner loop (trucks per type)
            } // End outer loop (truck types)

            // 10. Execute the batch
            int[] results = ps.executeBatch();
            System.out.println("Batch execution results count: " + results.length);

            // 11. Commit transaction
            conn.commit();
            System.out.println("Successfully added " + totalTrucksAdded + " sample trucks for " + typesProcessed + " types.");

        } catch (SQLException e) {
            System.err.println("SQL Error occurred during sample truck insertion: " + e.getMessage());
            e.printStackTrace();
            // 12. Rollback on error
            if (conn != null) {
                try {
                    System.err.println("Rolling back transaction...");
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
        } catch (Exception e) {
             System.err.println("An unexpected error occurred: " + e.getMessage());
             e.printStackTrace();
             if (conn != null) { try { conn.rollback(); } catch (SQLException ignored) {} }
        }
        finally {
            // 13. Clean up resources
            try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
            try { if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                 }
            } catch (SQLException ignored) {}
        }
    }

    // --- Helper Methods for Sample Data Generation ---

    private static String generateSampleLicensePlate(int typeId) {
        // Example Format: BA-01-TT<typeId>-<counter> (Adjust format as needed)
        // Assumes Bagmati province prefix for simplicity
        String provincePrefix = "BA"; // Could randomize or base on type
        int zone = ThreadLocalRandom.current().nextInt(1, 15); // Random zone number
        String formattedCounter = String.format("%04d", licensePlateCounter++); // 4-digit counter

        return String.format("%s-%02d-TT%d-%s", provincePrefix, zone, typeId, formattedCounter);
    }

    private static String[] generateSampleMakeModel(String typeName) {
        typeName = typeName.toLowerCase();
        String make = "Generic";
        String model = "Truck";

        if (typeName.contains("pickup") || typeName.contains("bolero") || typeName.contains("mini")) {
            make = (new Random().nextBoolean()) ? "Mahindra" : "Tata";
            model = (make.equals("Mahindra")) ? "Bolero Pickup" : "Ace";
        } else if (typeName.contains("tata")) {
            make = "Tata";
            model = "LPT " + ThreadLocalRandom.current().nextInt(407, 1110); // Sample Tata LPT models
        } else if (typeName.contains("eicher")) {
            make = "Eicher";
            model = "Pro " + ThreadLocalRandom.current().nextInt(1000, 3001); // Sample Eicher Pro models
        } else if (typeName.contains("heavy") || typeName.contains("container") || typeName.contains("wheeler")) {
             make = (new Random().nextBoolean()) ? "Ashok Leyland" : "BharatBenz";
             model = "Cargo " + ThreadLocalRandom.current().nextInt(16, 32) + "T";
        }
        // Add more specific cases if needed

        return new String[]{make, model};
    }

    private static String getRandomLocation() {
        String[] locations = {"Depot - Teku", "Yard - Balaju", "Lot - Koteshwor", "Garage - Sinamangal", "Parking - Gongabu"};
        return locations[new Random().nextInt(locations.length)];
    }

    private static Date generateRandomPastDate(int minMonthsAgo, int maxMonthsAgo) {
        LocalDate today = LocalDate.now();
        int monthsAgo = ThreadLocalRandom.current().nextInt(minMonthsAgo, maxMonthsAgo + 1);
        LocalDate pastDate = today.minusMonths(monthsAgo).minusDays(ThreadLocalRandom.current().nextInt(0, 28)); // Random day within the month
        return Date.valueOf(pastDate);
    }
    
//    public static void main(String[] args) {
//        System.out.println("Attempting to add sample trucks to the database...");
//        try {
//            // Ensure DB server is running and config is correct before calling
//            addSampleTrucks();
//            System.out.println("--------------------------------------------------");
//            System.out.println("Sample truck population process finished.");
//        } catch (Exception e) {
//            System.err.println("An error occurred in the main execution block: " + e.getMessage());
//            e.printStackTrace();
//        }
//  }
}