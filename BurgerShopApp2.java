import java.sql.*;
import java.util.Scanner;
import java.util.Vector;

class Burger {
    private int id;
    private String name;
    private double price;
    private Vector<String> toppings;
    private boolean extraCheese;

    public Burger(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.toppings = new Vector<>();
        this.extraCheese = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        double totalPrice = price;
        if (extraCheese) {
            totalPrice += 50; // Extra cheese costs ₹50
        }
        return totalPrice;
    }

    public void addTopping(String topping) {
        toppings.add(topping);
    }

    public void addExtraCheese() {
        this.extraCheese = true;
    }

    public String getToppingsAsString() {
        return String.join(", ", toppings);
    }

    @Override
    public String toString() {
        StringBuilder burgerDescription = new StringBuilder(name + " (₹" + price + ")");
        if (extraCheese) {
            burgerDescription.append(", Extra Cheese");
        }
        if (!toppings.isEmpty()) {
            burgerDescription.append(", Toppings: " + getToppingsAsString());
        }
        return burgerDescription.toString();
    }
}

class Menu {
    private Connection connection;

    public Menu(Connection connection) {
        this.connection = connection;
    }

    public void displayMenu() {
        System.out.println("----- Burger Menu -----");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM burgers")) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("name") + " (₹" + rs.getDouble("price") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Burger getBurger(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM burgers WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Burger(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class Order {
    private Connection connection;

    public Order(Connection connection) {
        this.connection = connection;
    }

    public void addBurger(Burger burger) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO orders (burger_id, toppings, extra_cheese, total_price) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, burger.getId());
            pstmt.setString(2, burger.getToppingsAsString());
            pstmt.setBoolean(3, burger.extraCheese);
            pstmt.setDouble(4, burger.getPrice());
            pstmt.executeUpdate();
            System.out.println(burger.getName() + " added to your order.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewOrder() {
        System.out.println("----- Your Order -----");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM orders JOIN burgers ON orders.burger_id = burgers.id")) {
            double total = 0;
            while (rs.next()) {
                String toppings = rs.getString("toppings");
                boolean extraCheese = rs.getBoolean("extra_cheese");
                double price = rs.getDouble("total_price");
                total += price;

                System.out.println(rs.getString("name") + " (₹" + rs.getDouble("price") + ")" +
                        (extraCheese ? ", Extra Cheese" : "") +
                        (!toppings.isEmpty() ? ", Toppings: " + toppings : ""));
            }
            System.out.println("Total: ₹" + total + " Rupees");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class BurgerShopApp2 {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/burger_shop", "root", "")) {
            Scanner scanner = new Scanner(System.in);
            Menu menu = new Menu(connection);
            Order order = new Order(connection);

            boolean ordering = true;
            while (ordering) {
                menu.displayMenu();
                System.out.println("Enter the number of the burger you'd like to order (or 0 to finish): ");
                int choice = scanner.nextInt();

                if (choice == 0) {
                    ordering = false;
                    order.viewOrder();
                } else {
                    Burger selectedBurger = menu.getBurger(choice);
                    if (selectedBurger != null) {
                        System.out.println("Would you like to add extra cheese for ₹50? (yes/no)");
                        if (scanner.next().equalsIgnoreCase("yes")) {
                            selectedBurger.addExtraCheese();
                        }

                        System.out.println("Would you like to add toppings? (yes/no)");
                        if (scanner.next().equalsIgnoreCase("yes")) {
                            System.out.println("Enter a topping to add (type 'done' when finished): ");
                            while (true) {
                                String topping = scanner.next();
                                if (topping.equalsIgnoreCase("done")) break;
                                selectedBurger.addTopping(topping);
                            }
                        }

                        order.addBurger(selectedBurger);
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }
            }
            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}