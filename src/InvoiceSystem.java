//Brett Rainiel Espiritu
//Kian Porras

import java.sql.*;
import java.util.Date;
import java.util.Scanner;


public class InvoiceSystem {
    private Connection connection;


    public InvoiceSystem() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/brettdump", "root", "Brett#2004");


            PreparedStatement checkStatement = connection.prepareStatement("SHOW COLUMNS FROM clients LIKE 'id'");
            ResultSet resultSet = checkStatement.executeQuery();
            if (!resultSet.next()) {
                PreparedStatement alterStatement = connection.prepareStatement("ALTER TABLE clients ADD COLUMN id INT AUTO_INCREMENT PRIMARY KEY");
                alterStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addClient(Client client) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO clients (name, total_billed, email, phone, address) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, client.getName());
            statement.setDouble(2, client.getTotalBilled());
            statement.setString(3, client.getEmail());
            statement.setString(4, client.getPhone());
            statement.setString(5, client.getAddress());
            statement.executeUpdate();
            System.out.println("Client added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Client getClient(int id) {
        Client client = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM clients WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                client = new Client();
                client.setId(resultSet.getInt("id"));
                client.setName(resultSet.getString("name"));
                client.setTotalBilled(resultSet.getDouble("total_billed"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }


    public void updateClient(Client client) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE clients SET name = ?, total_billed = ?, email = ?, phone = ?, address = ? WHERE id = ?");
            statement.setString(1, client.getName());
            statement.setDouble(2, client.getTotalBilled());
            statement.setString(3, client.getEmail());
            statement.setString(4, client.getPhone());
            statement.setString(5, client.getAddress());
            statement.setInt(6, client.getId());
            statement.executeUpdate();
           
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   

    public void viewAllServices() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM services");
            System.out.println("\n--- All Services ---");
    
            System.out.printf("%-5s | %-20s | %s\n", "ID", "Name", "Rate Per Hour");
            System.out.println(repeatChar('-', 50));
    
            while (resultSet.next()) {
                System.out.printf("%-5s | %-20s | %s\n",
                        resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("rate_per_hour"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   


    public void deleteClient(int clientId) {
        try {
            // Check if naay invoices associated with the client
            String checkQuery = "SELECT COUNT(*) FROM invoices WHERE client_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setInt(1, clientId);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count > 0) {
                System.out.println("Cannot delete client. There are invoices associated with this client.");
                return;
            }
   
            // If no invoices are associated, delete
            String deleteQuery = "DELETE FROM clients WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setInt(1, clientId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Client deleted successfully.");
            } else {
                System.out.println("Client not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   


    // Service Management
    public void addService(Service service) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO services (name, rate_per_hour) VALUES (?, ?)");
            statement.setString(1, service.getName());
            statement.setDouble(2, service.getRatePerHour());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Service getService(int id) {
        Service service = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM services WHERE service_id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                service = new Service();
                service.setId(resultSet.getInt("service_id"));
                service.setName(resultSet.getString("name"));
                service.setRatePerHour(resultSet.getDouble("rate_per_hour"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return service;
    }
   


    public void updateService(Service service) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE services SET name = ?, rate_per_hour = ? WHERE service_id = ?");
            statement.setString(1, service.getName());
            statement.setDouble(2, service.getRatePerHour());
            statement.setInt(3, service.getId());
            statement.executeUpdate();
           
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   


    public void deleteService(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM services WHERE service_id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Invoice Management
    public void createInvoice(Invoice invoice) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO invoices (client_id, date, total_amount) VALUES (?, ?, ?)");
            statement.setInt(1, invoice.getClient().getId());
            statement.setDate(2, new java.sql.Date(invoice.getDate().getTime()));
            statement.setDouble(3, invoice.getTotalAmount());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Invoice getInvoice(int id) {
        Invoice invoice = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM invoices WHERE invoice_id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int clientId = resultSet.getInt("client_id");
                Client client = getClient(clientId);
                java.sql.Date date = resultSet.getDate("date");
                double totalAmount = resultSet.getDouble("total_amount");
                invoice = new Invoice(id, client, date, totalAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoice;
    }
   


    public void updateInvoice(Invoice invoice) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE invoices SET client_id = ?, date = ?, total_amount = ? WHERE invoice_id = ?");
            statement.setInt(1, invoice.getClient().getId());
            statement.setDate(2, new java.sql.Date(invoice.getDate().getTime()));
            statement.setDouble(3, invoice.getTotalAmount());
            statement.setInt(4, invoice.getId());
            statement.executeUpdate();
            System.out.println("Invoice updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   


    public void deleteInvoice(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM invoices WHERE invoice_id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    private String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }




    public void viewAllClients() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM clients");
            System.out.println("\n--- All Clients ---");

            System.out.printf("%-5s | %-20s | %s\n", "ID", "Name", "Total Billed");
            System.out.println(repeatChar('-', 50));

            while (resultSet.next()) {
                System.out.printf("%-5s | %-20s | %s\n",
                        resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("total_billed"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewTotalBilledAmountForEachClient() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, name, total_billed FROM clients");
            System.out.println("\n--- Total Billed Amount for Each Client ---");

            System.out.printf("%-5s | %-20s | %s\n", "ID", "Name", "Total Billed");
            System.out.println(repeatChar('-', 50));

            while (resultSet.next()) {
                System.out.printf("%-5s | %-20s | %s\n",
                        resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("total_billed"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addServiceToInvoice(int invoiceId, int serviceId, double hours) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO invoice_services (invoice_id, service_id, hours) VALUES (?, ?, ?)");
            statement.setInt(1, invoiceId);
            statement.setInt(2, serviceId);
            statement.setDouble(3, hours);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateServiceHoursInInvoice(int invoiceId, int serviceId, double hours) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE invoice_services SET hours = ? WHERE invoice_id = ? AND service_id = ?");
            statement.setDouble(1, hours);
            statement.setInt(2, invoiceId);
            statement.setInt(3, serviceId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewAllInvoicesForClient(int clientId) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM invoices WHERE client_id = ?");
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("\n--- All Invoices for Client ID " + clientId + " ---");

            System.out.printf("\u001B[33m%-10s\u001B[0m | \u001B[34m%-15s\u001B[0m | \u001B[36m%-20s\u001B[0m | \u001B[35m%s\u001B[0m\n",
                    "ID", "Date", "Total Amount", "Client ID");
                    System.out.println(repeatChar('-', 70));


            while (resultSet.next()) {
                System.out.printf("\u001B[33m%-10s\u001B[0m | \u001B[34m%-15s\u001B[0m | \u001B[36m%-20s\u001B[0m | \u001B[35m%s\u001B[0m\n",
                        resultSet.getInt("invoice_id"), resultSet.getDate("date"),
                        resultSet.getDouble("total_amount"), resultSet.getInt("client_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewAllServicesForInvoiceFromInput(Scanner scanner) {
        System.out.print("Enter invoice ID: ");
        int invoiceId = scanner.nextInt();
        scanner.nextLine();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM invoice_services WHERE invoice_id = ?");
            statement.setInt(1, invoiceId);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("\n--- All Services for Invoice ID " + invoiceId + " ---");

            System.out.printf("\u001B[33m%-10s\u001B[0m | \u001B[34m%-15s\u001B[0m | \u001B[36m%-20s\u001B[0m\n",
                    "Service ID", "Invoice ID", "Hours");
                    System.out.println(repeatChar('-', 50));


            while (resultSet.next()) {
                System.out.printf("\u001B[33m%-10s\u001B[0m | \u001B[34m%-15s\u001B[0m | \u001B[36m%-20s\u001B[0m\n",
                        resultSet.getInt("service_id"), resultSet.getInt("invoice_id"), resultSet.getDouble("hours"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewTotalAmountForEachInvoice() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT invoice_id, total_amount FROM invoices");
            System.out.println("\n--- Total Amount for Each Invoice ---");

            System.out.printf("\u001B[33m%-10s\u001B[0m | \u001B[36m%s\u001B[0m\n",
                    "Invoice ID", "Total Amount");
                    System.out.println(repeatChar('-', 30));


            while (resultSet.next()) {
                System.out.printf("\u001B[33m%-10s\u001B[0m | \u001B[36m%s\u001B[0m\n",
                        resultSet.getInt("invoice_id"), resultSet.getDouble("total_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
    public void viewTotalHoursBilledForEachService() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT service_id, SUM(hours) AS total_hours_billed FROM invoice_services GROUP BY service_id");
            System.out.println("\n--- Total Hours Billed for Each Service ---");
    
            System.out.printf("%-5s | %s\n", "Service ID", "Total Hours Billed");
            System.out.println(repeatChar('-', 50));
    
            while (resultSet.next()) {
                System.out.printf("%-5s | %s\n",
                        resultSet.getInt("service_id"), resultSet.getDouble("total_hours_billed"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    // Analytics
    public double getTotalIncome(Date start, Date end) {
        double totalIncome = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(total_amount) AS total_income FROM invoices WHERE date BETWEEN ? AND ?");
            statement.setDate(1, new java.sql.Date(start.getTime()));
            statement.setDate(2, new java.sql.Date(end.getTime()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalIncome = resultSet.getDouble("total_income");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalIncome;
    }


    public Service getMostPopularService(Date start, Date end) {
        Service mostPopularService = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT service_id, SUM(hours) AS total_hours FROM invoice_services JOIN invoices ON invoice_services.invoice_id = invoices.id WHERE invoices.date BETWEEN ? AND ? GROUP BY service_id ORDER BY total_hours DESC LIMIT 1");
            statement.setDate(1, new java.sql.Date(start.getTime()));
            statement.setDate(2, new java.sql.Date(end.getTime()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int serviceId = resultSet.getInt("service_id");
                mostPopularService = getService(serviceId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mostPopularService;
    }


    public Client getTopClient(Date start, Date end) {
        Client topClient = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT client_id, SUM(total_amount) AS total_amount FROM invoices WHERE date BETWEEN ? AND ? GROUP BY client_id ORDER BY total_amount DESC LIMIT 1");
            statement.setDate(1, new java.sql.Date(start.getTime()));
            statement.setDate(2, new java.sql.Date(end.getTime()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int clientId = resultSet.getInt("client_id");
                topClient = getClient(clientId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topClient;
    }
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Display Menu
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Invoice System Menu ---");
            System.out.println("Client Management:");
            System.out.println("1. \u001B[34mAdd Client\u001B[0m");
            System.out.println("2. \u001B[34mUpdate Client\u001B[0m");
            System.out.println("3. \u001B[34mDelete Client\u001B[0m\n");
            System.out.println("Service Management:");
            System.out.println("4. \u001B[34mAdd Service\u001B[0m");
            System.out.println("5. \u001B[34mUpdate Service\u001B[0m");
            System.out.println("6. \u001B[34mDelete Service\u001B[0m\n");
            System.out.println("Invoice Management:");
            System.out.println("7. \u001B[34mCreate Invoice\u001B[0m");
            System.out.println("8. \u001B[34mUpdate Invoice\u001B[0m");
            System.out.println("9. \u001B[34mDelete Invoice\u001B[0m\n");
            System.out.println("View Information:");
            System.out.println("10. \u001B[34mView All Clients\u001B[0m");
            System.out.println("11. \u001B[34mView Total Billed Amount for Each Client\u001B[0m");
            System.out.println("12. \u001B[34mView All Services\u001B[0m");
            System.out.println("13. \u001B[34mView Total Hours Billed for Each Service\u001B[0m");
            System.out.println("14. \u001B[34mView All Invoices for Client\u001B[0m");
            System.out.println("15. \u001B[34mView Total Amount for Each Invoice\u001B[0m\n");
            System.out.println("Financial Analysis:");
            System.out.println("16. \u001B[34mGet Total Income\u001B[0m");
            System.out.println("17. \u001B[34mGet Most Popular Service\u001B[0m");
            System.out.println("18. \u001B[34mGet Top Client\u001B[0m\n");
            System.out.println("Other Options:");
            System.out.println("19. \u001B[34mExit\u001B[0m\n");
            System.out.println("Invoice Services Management:");
            System.out.println("20. \u001B[34mAdd Service to Invoice\u001B[0m");
            System.out.println("21. \u001B[34mUpdate Service Hours in Invoice\u001B[0m");
            System.out.println("22. \u001B[34mView All Services for Invoice\u001B[0m\n");
            System.out.print("\u001B[32m┌─────────────────────────────┐\u001B[0m\n");
            System.out.print("\u001B[32m│\u001B[1m Enter your choice: \u001B[0m");
            System.out.print("\u001B[32m│\u001B[0m ");
           




            int choice = scanner.nextInt();
            scanner.nextLine(); 

           
            switch (choice) {
                case 1:
                    addClientFromInput(scanner);
                    break;
                case 2:
                    updateClientFromInput(scanner);
                    break;
                case 3:
                    deleteClientFromInput(scanner);
                    break;
                case 4:
                    addServiceFromInput(scanner);
                    break;
                case 5:
                    updateServiceFromInput(scanner);
                    break;
                case 6:
                    deleteServiceFromInput(scanner);
                    break;
                case 7:
                    createInvoiceFromInput(scanner);
                    break;
                case 8:
                    updateInvoiceFromInput(scanner);
                    break;
                case 9:
                    deleteInvoiceFromInput(scanner);
                    break;
                case 10:
                    viewAllClients();
                    break;
                case 11:
                    viewTotalBilledAmountForEachClient();
                    break;
                case 12:
                    viewAllServices();
                    break;
                case 13:
                    viewTotalHoursBilledForEachService();
                    break;
                case 14:
                    viewAllInvoicesForClientFromInput(scanner);
                    break;
                case 15:
                    viewTotalAmountForEachInvoice();
                    break;
                case 16:
                    getTotalIncomeFromInput(scanner);
                    break;
                case 17:
                    getMostPopularServiceFromInput(scanner);
                    break;
                case 18:
                    getTopClientFromInput(scanner);
                    break;
                case 19:
                    closeConnection();
                    System.out.println("Exiting... Thank you!");
                    return;
                case 20:
                    addServiceToInvoiceFromInput(scanner);
                    break;
                case 21:
                    updateServiceHoursInInvoiceFromInput(scanner);
                    break;
                case 22:
                    viewAllServicesForInvoiceFromInput(scanner);
                     break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    
    // user input
    private void addClientFromInput(Scanner scanner) {
        System.out.print("Enter client name: ");
        String name = scanner.nextLine();
        System.out.print("Enter total billed amount: ");
        double totalBilled = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
   
        addClient(new Client(0, name, totalBilled, email, phone, address));      
    }
    private void updateClientFromInput(Scanner scanner) {
        System.out.print("Enter client ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        Client client = getClient(id);
        if (client != null) {
            System.out.print("Enter new name (leave blank to keep '" + client.getName() + "'): ");
            String name = scanner.nextLine();
            System.out.print("Enter new total billed amount (leave blank to keep '" + client.getTotalBilled() + "'): ");
            double totalBilled = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter new email (leave blank to keep '" + client.getEmail() + "'): ");
            String email = scanner.nextLine();
            System.out.print("Enter new phone number (leave blank to keep '" + client.getPhone() + "'): ");
            String phone = scanner.nextLine();
            System.out.print("Enter new address (leave blank to keep '" + client.getAddress() + "'): ");
            String address = scanner.nextLine();
   
            // Update client with new values
            client.setName(name.isEmpty() || name.trim().isEmpty() ? client.getName() : name);
            client.setTotalBilled(totalBilled);
            client.setEmail(email.isEmpty() || email.trim().isEmpty() ? client.getEmail() : email);
            client.setPhone(phone.isEmpty() || phone.trim().isEmpty() ? client.getPhone() : phone);
            client.setAddress(address.isEmpty() || address.trim().isEmpty() ? client.getAddress() : address);
            
            // Call updateClient to update the client in the database
            updateClient(client);
            System.out.println("Client updated successfully.");
        } else {
            System.out.println("Client with ID " + id + " not found.");
        }
    }
   
   
    private void deleteClientFromInput(Scanner scanner) {
        System.out.print("Enter client ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        deleteClient(id);
    }


    private void addServiceFromInput(Scanner scanner) {
        System.out.print("Enter service name: ");
        String name = scanner.nextLine();
        System.out.print("Enter rate per hour: ");
        double ratePerHour = scanner.nextDouble();
        scanner.nextLine(); 
        addService(new Service(0, name, ratePerHour));
        System.out.println("Service added successfully.");
    }
 
    private void updateServiceFromInput(Scanner scanner) {
        System.out.print("Enter service ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        Service service = getService(id);
        if (service != null) {
            System.out.print("Enter new name (leave blank to keep '" + service.getName() + "'): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                service.setName(name);
            }
            System.out.print("Enter new rate per hour (leave blank to keep '" + service.getRatePerHour() + "'): ");
            String ratePerHourStr = scanner.nextLine().trim();
            if (!ratePerHourStr.isEmpty()) {
                double ratePerHour = Double.parseDouble(ratePerHourStr);
                service.setRatePerHour(ratePerHour);
            }
            updateService(service);
            System.out.println("Service updated successfully.");
        } else {
            System.out.println("Service with ID " + id + " not found.");
        }
    }


    private void deleteServiceFromInput(Scanner scanner) {
        System.out.print("Enter service ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        deleteService(id);
        System.out.println("Service deleted successfully.");
    }


    private void createInvoiceFromInput(Scanner scanner) {
        System.out.print("Enter client ID for the invoice: ");
        int clientId = scanner.nextInt();
        scanner.nextLine(); 
        Client client = getClient(clientId);
        if (client != null) {
            System.out.print("Enter date (YYYY-MM-DD) for the invoice: ");
            String dateStr = scanner.nextLine();
            java.sql.Date date = java.sql.Date.valueOf(dateStr);
            System.out.print("Enter total amount for the invoice: ");
            double totalAmount = scanner.nextDouble();
            scanner.nextLine(); 
            createInvoice(new Invoice(clientId, client, date, totalAmount));
            System.out.println("Invoice created successfully.");
        } else {
            System.out.println("Client with ID " + clientId + " not found.");
        }
    }


    private void updateInvoiceFromInput(Scanner scanner) {
        System.out.print("Enter invoice ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        Invoice invoice = getInvoice(id);
        if (invoice != null) {
            System.out.print("Enter date (YYYY-MM-DD) (leave blank to keep '" + invoice.getDate() + "'): ");
            String dateStr = scanner.nextLine().trim();
            if (!dateStr.isEmpty()) {
                try {
                    java.sql.Date date = java.sql.Date.valueOf(dateStr);
                    invoice.setDate(date);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid date format. Date not updated.");
                }
            }
            System.out.print("Enter total amount (leave blank to keep '" + invoice.getTotalAmount() + "'): ");
            String totalAmountStr = scanner.nextLine().trim();
            if (!totalAmountStr.isEmpty()) {
                try {
                    double totalAmount = Double.parseDouble(totalAmountStr);
                    invoice.setTotalAmount(totalAmount);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid total amount format. Total amount not updated.");
                }
            }
            updateInvoice(invoice);
            System.out.println("Invoice updated successfully.");
        } else {
            System.out.println("Invoice with ID " + id + " not found.");
        }
    }


    private void deleteInvoiceFromInput(Scanner scanner) {
        System.out.print("Enter invoice ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        deleteInvoice(id);
        System.out.println("Invoice deleted successfully.");
    }


    private void addServiceToInvoiceFromInput(Scanner scanner) {
        System.out.print("Enter invoice ID to add service: ");
        int invoiceId = scanner.nextInt();
        scanner.nextLine(); 
        Invoice invoice = getInvoice(invoiceId);
        if (invoice != null) {
            System.out.print("Enter service ID: ");
            int serviceId = scanner.nextInt();
            scanner.nextLine(); 
            Service service = getService(serviceId);
            if (service != null) {
                System.out.print("Enter hours for this service: ");
                double hours = scanner.nextDouble();
                scanner.nextLine(); 
                addServiceToInvoice(invoiceId, serviceId, hours);
                System.out.println("Service added to invoice successfully.");
            } else {
                System.out.println("Service with ID " + serviceId + " not found.");
            }
        } else {
            System.out.println("Invoice with ID " + invoiceId + " not found.");
        }
    }


    private void updateServiceHoursInInvoiceFromInput(Scanner scanner) {
        System.out.print("Enter invoice ID to update service hours: ");
        int invoiceId = scanner.nextInt();
        scanner.nextLine(); 
        System.out.print("Enter service ID: ");
        int serviceId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new hours: ");
        double hours = scanner.nextDouble();
        scanner.nextLine(); 
        updateServiceHoursInInvoice(invoiceId, serviceId, hours);
        System.out.println("Service hours updated in invoice successfully.");
    }


    private void viewAllInvoicesForClientFromInput(Scanner scanner) {
        System.out.print("Enter client ID to view invoices: ");
        int clientId = scanner.nextInt();
        scanner.nextLine(); 
        viewAllInvoicesForClient(clientId);
    }


    private void getTotalIncomeFromInput(Scanner scanner) {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
        double totalIncome = getTotalIncome(startDate, endDate);
        System.out.println("Total income from " + startDateStr + " to " + endDateStr + ": " + totalIncome);
    }


    private void getMostPopularServiceFromInput(Scanner scanner) {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
        Service mostPopularService = getMostPopularService(startDate, endDate);
        if (mostPopularService != null) {
            System.out.println("Most popular service from " + startDateStr + " to " + endDateStr + ": " + mostPopularService.getName());
        } else {
            System.out.println("No service found within the specified date range.");
        }
    }
   


    private void getTopClientFromInput(Scanner scanner) {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
        Client topClient = getTopClient(startDate, endDate);
        System.out.println("Top client from " + startDateStr + " to " + endDateStr + ": " + topClient.getName());
    }


        public static void main(String[] args) {
            InvoiceSystem invoiceSystem = new InvoiceSystem();
            invoiceSystem.displayMenu();
    }
}
