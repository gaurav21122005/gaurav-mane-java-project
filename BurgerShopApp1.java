import java.util.Scanner;
import java.util.Vector;

class Burger {
    private String name;
    private double price;
    private Vector<String> toppings;
    private boolean extraCheese;

    public Burger(String name, double price) {
        this.name = name;
        this.price = price;
        this.toppings = new Vector<>();
        this.extraCheese = false;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        // Adding the cost of extra cheese
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

    @Override
    public String toString() {
        StringBuilder burgerDescription = new StringBuilder(name + " (₹" + price + " Rupees)");
        if (extraCheese) {
            burgerDescription.append(", Extra Cheese");
        }
        if (!toppings.isEmpty()) {
            burgerDescription.append(", Toppings: " + String.join(", ", toppings));
        }
        return burgerDescription.toString();
    }
}

class Menu {
    private Vector<Burger> burgers = new Vector<>();

    public Menu() {
        burgers.add(new Burger("Cheeseburger", 415.00));  // ₹415
        burgers.add(new Burger("Veggie Burger", 374.00));  // ₹374
        burgers.add(new Burger("Chicken Burger", 457.00)); // ₹457
    }

    public void displayMenu() {
        System.out.println("----- Burger Menu -----");
        for (int i = 0; i < burgers.size(); i++) {
            System.out.println((i + 1) + ". " + burgers.get(i));
        }
    }

    public Burger getBurger(int index) {
        return burgers.get(index - 1); // index - 1 to match the menu numbering
    }
}

class Order {
    private Vector<Burger> orderItems = new Vector<>();

    public void addBurger(Burger burger) {
        orderItems.add(burger);
        System.out.println(burger.getName() + " added to your order.");
    }

    public void viewOrder() {
        System.out.println("----- Your Order -----");
        double total = 0;
        for (Burger burger : orderItems) {
            System.out.println(burger);
            total += burger.getPrice();
        }
        System.out.println("Total: ₹" + total + " Rupees");
    }
}

public class BurgerShopApp1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu();
        Order order = new Order();
        boolean ordering = true;

        while (ordering) {
            menu.displayMenu();
            System.out.println("Enter the number of the burger you'd like to order (or 0 to finish): ");
            int choice = scanner.nextInt();

            if (choice == 0) {
                ordering = false;
                order.viewOrder();
            } else if (choice > 0 && choice <= 3) {
                Burger selectedBurger = menu.getBurger(choice);

                // Ask for extra cheese
                System.out.println("Would you like to add extra cheese for ₹50? (yes/no)");
                String extraCheeseChoice = scanner.next();
                if (extraCheeseChoice.equalsIgnoreCase("yes")) {
                    selectedBurger.addExtraCheese();
                }

                // Ask for toppings
                System.out.println("Would you like to add toppings? (yes/no)");
                String toppingChoice = scanner.next();
                if (toppingChoice.equalsIgnoreCase("yes")) {
                    boolean addingToppings = true;
                    while (addingToppings) {
                        System.out.println("Enter a topping to add (type 'done' when finished): ");
                        String topping = scanner.next();
                        if (topping.equalsIgnoreCase("done")) {
                            addingToppings = false;
                        } else {
                            selectedBurger.addTopping(topping);
                        }
                    }
                }

                order.addBurger(selectedBurger);
            } else {
                System.out.println("Invalid choice, please try again.");
            }
        }

        scanner.close();
    }
}