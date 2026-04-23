import A.EmailNotification;
import A.Notifiable;
import A.PushNotification;
import A.SmsNotification;
import B.Product;
import C.HistoryStack;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // A
        List<Notifiable> notifs = new ArrayList<>();

        notifs.add(new SmsNotification("0737610710"));
        notifs.add(new PushNotification("dsa3213214-dsa214332ewqd-321"));
        notifs.add(new EmailNotification("sabin.tarba@csie.ase.ro"));

        notifs.forEach(n -> n.send("Salut de la JAVA"));

        System.out.println("\n");

        // B
        List<Product> products = new ArrayList<>();
        final String ELECTRONICS = "ELECTRONICS";
        final String FOOD = "FOOD";
        final String CLOTHING = "CLOTHING";

        products.add(new Product("Apples", FOOD, 10.5f, 100));
        products.add(new Product("T-Shirt", CLOTHING, 23.5f, 50));
        products.add(new Product("Bananas", FOOD, 9.5f, 200));
        products.add(new Product("Phone", ELECTRONICS, 699.9f, 10));
        products.add(new Product("Laptop", ELECTRONICS, 900.0f, 25));
        products.add(new Product("Strawberries", FOOD, 15.5f, 10));

        products.forEach(System.out::println);

        Map<String, Double> stockPerCategory = products
                .stream()
                .collect(Collectors.groupingBy(
                            Product::getCategory,
                            Collectors.summingDouble(p -> p.getStock() * p.getPrice())
                        )
                );

        stockPerCategory.forEach((k, v) -> {
            System.out.println(k + " " + v);
        });

        List<Product> sortedProducts = products
                .stream()
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed())
                .limit(3).collect(Collectors.toList());
        sortedProducts.forEach(System.out::println);

        Optional<String> topCategory = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);

        topCategory.ifPresent(c -> System.out.println("Categoria cu cele mai multe produse: " + c));

        products.stream()
                .sorted(Comparator.comparing(Product::getCategory)
                        .thenComparing(Comparator.comparingDouble(Product::getPrice).reversed())
                )
                .forEach(System.out::println);

        // C
        HistoryStack<String> hs = new HistoryStack<>();
        hs.push("A");
        hs.push("B");
        hs.push("C");
        hs.push("D");

        Optional<String> x = hs.pop();
        Optional<String> y = hs.pop();

        System.out.println("History size: " + hs.historySize());

        if(x.isPresent()){
            System.out.println(x.get());
        } else {
            System.out.println("UNKNOWN");
        }

        if(y.isPresent()){
            System.out.println(y.get());
        } else {
            System.out.println("UNKNOWN");
        }

        Optional<String> undo = hs.undo();
        if(undo.isPresent()){
            System.out.println("Undo: " + undo.get());
        } else {
            System.out.println("UNKNOWN");
        }

        Optional<String> peek = hs.peek();
        if(peek.isPresent()){
            System.out.println("Peek: " + peek.get());
        } else {
            System.out.println("UNKNOWN");
        }

        System.out.println(hs.historySize());
    }
}