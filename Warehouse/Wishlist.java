import java.util.HashMap;
import java.util.Map;

public class Wishlist {
    private String clientID;
    private Map<Product, Integer> products; // Stores Product objects and desired quantities.

    public Wishlist(String clientID) {
        this.clientID = clientID;
        this.products = new HashMap<>();
    }

    public boolean addProduct(Product product, int quantity) {
        if (product == null || quantity <= 0) return false;
        products.put(product, quantity);
        return true;
    }

    public boolean removeProduct(Product product) {
        return products.remove(product) != null;
    }

    public Map<Product, Integer> getProductsWithQuantities() {
        return new HashMap<>(products);
    }

    public String getClientID() {
        return clientID;
    }
}
