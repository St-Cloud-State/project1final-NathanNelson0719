import java.util.Scanner;
import java.util.Iterator;
import java.util.Map;

public class WarehouseConsole {
    private ClientList clientList = new ClientList();
    private Catalog catalog = Catalog.getInstance();
    private Integer clientCount = 0;
    private Integer productCount = 0;

    public void showMainMenu() {
        Scanner scanner = new Scanner(System.in);
        displayOptions();
        while (true) {
            System.out.print("Select a command, 19 for Help: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 
    
            switch (choice) {
                case 1:
                    manageClients(scanner);
                    break;
                case 2:
                    manageProducts(scanner);
                    break;
                case 3:
                    manageClientWishlist(scanner);
                    break;
                case 4:
                    placeOrderForClient(scanner);
                    break;
                case 5:
                    viewClientWishlist(scanner);
                    break;
                case 6:
                    viewProductWaitlist(scanner);
                    break;
                case 7:
                    recordClientPayment(scanner);
                    break;
                case 8:
                    receiveProductShipment(scanner);
                    break;
                case 9:
                    displayClients();
                    break;
                case 10:
                    displayProducts();
                    break;
                case 19:
                    displayOptions();
                    break;
                case 20:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void displayOptions() {
        System.out.println("Warehouse Management System");
        System.out.println("1. Add Client");
        System.out.println("2. Add Product");
        System.out.println("3. Manage Client Wishlist");
        System.out.println("4. Place Order for Client");
        System.out.println("5. View Client Wishlist");
        System.out.println("6. View Product Waitlist");
        System.out.println("7. Record Client Payment");
        System.out.println("8. Receive Product Shipment");
        System.out.println("9. View All Clients");
        System.out.println("10. View All Products");
        System.out.println("19. Help");
        System.out.println("20. Exit");
    }    

    private void placeOrderForClient(Scanner scanner) {
        System.out.print("Enter client number to place order: ");
        int clientNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        Client client = getClientByIndex(clientNumber);
        if (client != null) {
            placeOrder(client);
            System.out.println("Order placed for client: " + client.getName());
        } else {
            System.out.println("Client not found.");
        }
    }

    private void viewClientWishlist(Scanner scanner) {
        System.out.print("Enter client number to view wishlist: ");
        int clientNumber = scanner.nextInt();
        scanner.nextLine(); 
    
        Client client = getClientByIndex(clientNumber);
        if (client != null) {
            System.out.println(client.getName() + "'s wishlist:");
            displayClientWishlist(client);
        } else {
            System.out.println("Client not found.");
        }
    }

    private void viewProductWaitlist(Scanner scanner) {
        System.out.print("Enter product number to view waitlist: ");
        int productIndex = scanner.nextInt();
        scanner.nextLine(); 
    
        Product product = getProductByIndex(productIndex);
        if (product != null) {
            System.out.println("Waitlist for " + product.getName() + ": " + product.getWaitlist());
        } else {
            System.out.println("Product not found.");
        }
    }

    private void recordClientPayment(Scanner scanner) {
        System.out.print("Enter client number to record payment: ");
        int clientNumber = scanner.nextInt();
        scanner.nextLine(); 
    
        System.out.print("Enter payment amount: ");
        double payment = scanner.nextDouble();
        scanner.nextLine(); 
    
        Client client = getClientByIndex(clientNumber);
        if (client != null) {
            client.addToBalance(payment);
            System.out.println("Recorded payment of $" + payment + " for client: " + client.getName());
        } else {
            System.out.println("Client not found.");
        }
    }

    private void receiveProductShipment(Scanner scanner) {
        System.out.print("Enter product number to receive shipment: ");
        int productIndex = scanner.nextInt();
        scanner.nextLine(); 
    
        System.out.print("Enter quantity received: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); 
    
        Product product = getProductByIndex(productIndex);
        if (product != null) {
            product.addToQuantity(quantity);
            product.fulfillWaitlist(quantity);
            System.out.println("Received shipment of " + quantity + " for product: " + product.getName());
        } else {
            System.out.println("Product not found.");
        }
    }

    private void manageClientWishlist(Scanner scanner) {
        System.out.println("Which client do you want to manage: ");
        displayClients();
        boolean valid = false;
        Integer clientIndex = 0;
    
        while (!valid) {
            System.out.print("Enter client's number you want to manage: ");
            String input = scanner.nextLine();

            try {
                clientIndex = Integer.parseInt(input);
                if(clientIndex > 0 && clientIndex <= clientCount) valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid client number.");
            }
        }

        Iterator<Client> cIterator = clientList.getClients();
        Client client = new Client("temp", "temp", "temp", 0.0);
        for(int i = 0; i < clientIndex; i++) {
            client = cIterator.next();
        }

        System.out.println("Client chosen: " + client + " and their wishlist is: ");
        displayClientWishlist(client);

        System.out.println("Do you want to add or remove items from this wishlist? Enter 0 to return.");
        valid = false;
    
        while (!valid) {
            System.out.print("Enter 'add', 'remove', 'buyall', or 0 to return: ");
            String input = scanner.nextLine();
        
            if(input.equals("0")) return;

            switch(input.toLowerCase()) {
                case "buyall":
                    processWishlistPurchase(client);
                    return;
                
                case "add":
                    addProductToWishlist(scanner, client);
                    valid = true;
                    break;

                case "remove":
                    removeProductFromWishlist(scanner, client);
                    valid = true;
                    break;

                default:
                    System.out.println("Invalid option. Please enter 'add', 'remove', 'buyall', or 0 to return.");
            }
        }
    }

    private void displayClientWishlist(Client client) {
        Map<Product, Integer> wishlist = client.getWishlistWithQuantities();
        int index = 1;
        double totalPrice = 0.0;
    
        System.out.println(client.getName() + "'s Wishlist:");
        for (Map.Entry<Product, Integer> entry : wishlist.entrySet()) {
            Product product = entry.getKey();
            int desiredQuantity = entry.getValue();
            double itemTotal = product.getPrice() * desiredQuantity;
            totalPrice += itemTotal;
    
            System.out.println(index++ + ". " + product.getName() + " - Desired Quantity: " + desiredQuantity +
                               ", Price per unit: $" + product.getPrice() + ", Total for item: $" + itemTotal);
        }
        System.out.println("Total Price of Wishlist: $" + totalPrice);
    }        

    private void addProductToWishlist(Scanner scanner, Client client) {
        System.out.println("Which product would you like to add?");
        displayProducts();

        System.out.print("Enter product number you wish to add: ");
        int productIndex = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Enter quantity desired: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); 

        Product product = getProductByIndex(productIndex);
        if(product != null) {
            if(product.getQuantity() >= quantity) {
                client.addToWishlist(product, quantity);
                product.addToQuantity(-quantity);
                System.out.println("Added " + quantity + " of " + product.getName() + " to " + client.getName() + "'s wishlist.");
            } else {
                client.addToWishlist(product, product.getQuantity());
                product.addToWaitlist(client.getID());
                System.out.println("Only " + product.getQuantity() + " available. Remaining quantity added to waitlist.");
                product.addToQuantity(-product.getQuantity()); // Reduce available quantity to 0
            }
        }
    }

    private void removeProductFromWishlist(Scanner scanner, Client client) {
        displayClientWishlist(client);

        System.out.print("Enter product number you wish to remove: ");
        int productIndex = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Product product = getProductByIndex(productIndex);
        if(product != null) {
            client.removeFromWishlist(product);
            System.out.println("Removed " + product.getName() + " from " + client.getName() + "'s wishlist.");
        } else {
            System.out.println("Invalid product selection.");
        }
    }

    private void processWishlistPurchase(Client client) {
        Map<Product, Integer> wishlist = client.getWishlist().getProductsWithQuantities();
        double totalCost = 0.0;
    
        for (Map.Entry<Product, Integer> entry : wishlist.entrySet()) {
            Product product = entry.getKey();
            int desiredQty = entry.getValue();
    
            if (product.getQuantity() >= desiredQty) {
                totalCost += product.getPrice() * desiredQty;
                product.addToQuantity(-desiredQty);
                System.out.println("Bought " + desiredQty + " of " + product.getName());
            } else {
                int purchasableQty = product.getQuantity();
                totalCost += product.getPrice() * purchasableQty;
                product.addToQuantity(-purchasableQty);
                client.getWishlist().addProduct(product, desiredQty - purchasableQty);
                product.addToWaitlist(client.getID());
                System.out.println("Only " + purchasableQty + " of " + product.getName() + " purchased; added remainder to waitlist.");
            }
        }
    
        client.addToBalance(-totalCost);
        System.out.println(client.getName() + " has been debited $" + totalCost);
    }

    private void displayProducts(){
        Iterator<Product> pIterator = catalog.getAllProducts();
        int inc = 0;
        while (pIterator.hasNext()) { 
            inc++;
            Product product = pIterator.next(); 
            System.out.println(inc + " " + product.getProductInfo());
        }
    }

    private Client getClientByIndex(int index) {
        Iterator<Client> cIterator = clientList.getClients();
        Client client = null;
        int currentIndex = 1;
    
        while (cIterator.hasNext()) {
            client = cIterator.next();
            if (currentIndex == index) {
                return client; // Return the client when index matches
            }
            currentIndex++;
        }
    
        return null; // Return null if index is out of bounds
    }    

    private void displayClients(){
        Iterator<Client> cIterator = clientList.getClients();
        int inc = 0;
        while (cIterator.hasNext()) { 
            inc++;
            Client client = cIterator.next(); 
            System.out.println(inc + " " + client);
        }
    }

    // Method to manage clients
    private void manageClients(Scanner scanner) {
        System.out.println("Manage Clients");
        System.out.print("Enter Client Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Client Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Client Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter starting balance: ");
        boolean valid = false;
        Double bal = 0.0;
        while (!valid) {
            System.out.print("Enter clients balance: ");
            String input = scanner.nextLine();

            try {
                bal = Double.parseDouble(input); // Attempt to parse it as an integer
                valid = true;
                System.out.println("You entered the integer: " + bal);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        Client client = new Client(name, address, phone, bal);
        clientList.insertClient(client);
        System.out.println("Client added successfully!");
        clientCount++;
    }

    // Method to manage products
    private void manageProducts(Scanner scanner) {
        System.out.println("Manage Products");
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Product Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();  // Consume the newline character
        System.out.print("Enter Product Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Product Category: ");
        String category = scanner.nextLine();
        boolean valid = false;
        Integer bal = 0;
        while (!valid) {
            System.out.print("Enter the qunatity: ");
            String input = scanner.nextLine();

            try {
                bal = Integer.parseInt(input); // Attempt to parse it as an integer
                valid = true;
                System.out.println("You entered the integer: " + bal);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        Product product = new Product(name, price, description, category, bal);
        catalog.addProduct(product);
        System.out.println("Product added successfully!");
        productCount++;
    }

    // Method to create an order
    private void createOrder(Scanner scanner) {
        System.out.println("Create Order");
        System.out.print("Enter Client ID: ");
        String clientID = scanner.nextLine();
        System.out.print("Enter Product ID: ");
        String productID = scanner.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();

        Client client = clientList.search(clientID);
        Product product = catalog.searchProduct(productID);

        if (client != null && product != null) {
            Order order = new Order(client.getID(), java.time.LocalDate.now());
            order.addItem(product, quantity);
            System.out.println("Order created successfully!");
        } else {
            System.out.println("Client or Product not found.");
        }
    }

    // Method to view orders (Placeholder)
    private void viewOrders() {
        System.out.println("View Orders");
        // Orders functionality would go here
        System.out.println("No orders to display (order management not yet implemented).");
    }

    private void placeOrder(Client client) {
        Map<Product, Integer> wishlist = client.getWishlist().getProductsWithQuantities();
        double totalCost = 0.0;
    
        for (Map.Entry<Product, Integer> entry : wishlist.entrySet()) {
            Product product = entry.getKey();
            int desiredQty = entry.getValue();
    
            if (product.getQuantity() >= desiredQty) {
                totalCost += product.getPrice() * desiredQty;
                product.addToQuantity(-desiredQty);
                System.out.println("Bought " + desiredQty + " of " + product.getName());
            } else {
                int purchasableQty = product.getQuantity();
                totalCost += product.getPrice() * purchasableQty;
                product.addToQuantity(-purchasableQty);
                client.getWishlist().addProduct(product, desiredQty - purchasableQty); // Remaining quantity goes back to wishlist
                product.addToWaitlist(client.getID());
                System.out.println("Only " + purchasableQty + " of " + product.getName() + " purchased; added remainder to waitlist.");
            }
        }
    
        client.addToBalance(-totalCost);
        System.out.println(client.getName() + " has been debited $" + totalCost);
    }

    private Product getProductByIndex(int index) {
        Iterator<Product> pIterator = catalog.getAllProducts();
        Product product = null;
        for (int i = 0; i < index && pIterator.hasNext(); i++) {
            product = pIterator.next();
        }
        return product;
    }
























    public void testWarehouseConsole() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Testing Warehouse Console...");
    
        // Step 1: Manually add Clients C1 to C3
    System.out.println("\n--- Adding Clients C1 to C3 ---");
    clientList.insertClient(new Client("C1", "Address1", "123-456-7890", 100.0));
    clientList.insertClient(new Client("C2", "Address2", "234-567-8901", 100.0));
    clientList.insertClient(new Client("C3", "Address3", "345-678-9012", 100.0));
    clientCount += 3;

    // Display all clients
    System.out.println("\n--- Displaying All Clients ---");
    displayClients();

    // Step 2: Manually add Products P1 to P3
    System.out.println("\n--- Adding Products P1 to P3 ---");
    catalog.addProduct(new Product("P1", 1.0, "Description1", "Category1", 10));
    catalog.addProduct(new Product("P2", 2.0, "Description2", "Category2", 20));
    catalog.addProduct(new Product("P3", 3.0, "Description3", "Category3", 30));
    productCount += 3;

    // Display all products
    System.out.println("\n--- Displaying All Products ---");
    displayProducts();
    
        // Add items to C1's wishlist
        System.out.println("\n--- Adding Items to C1's Wishlist ---");
        Client c1 = getClientByIndex(1);
        Product p1 = getProductByIndex(1);
        Product p2 = getProductByIndex(2);
        if (c1 != null && p1 != null && p2 != null) {
            c1.addToWishlist(p1, 5);
            c1.addToWishlist(p2, 5);
            System.out.println("C1's Wishlist:");
            displayClientWishlist(c1);
        }
    
        // Place order for C1
        System.out.println("\n--- Placing Order for C1 ---");
        if (c1 != null) {
            placeOrder(c1);
        }
    
        // Display updated clients and products
        System.out.println("\n--- Updated Client Balances ---");
        displayClients();
        System.out.println("\n--- Updated Product Quantities ---");
        displayProducts();
    
        // Record payment for C1 and update balance
        System.out.println("\n--- Recording $50 Payment for C1 ---");
        if (c1 != null) {
            c1.addToBalance(50.0);
        }
    
        // Receive shipment for P1
        System.out.println("\n--- Receiving Shipment of 15 Items for P1 ---");
        if (p1 != null) {
            p1.addToQuantity(15);
            p1.fulfillWaitlist(15);
        }
    
        // Display final state of clients and products
        System.out.println("\n--- Final Client Balances ---");
        displayClients();
        System.out.println("\n--- Final Product Quantities ---");
        displayProducts();
    }
    







    public static void main(String[] args) {
        WarehouseConsole app = new WarehouseConsole();
        app.showMainMenu();





        //app.testWarehouseConsole();
    }


}
