package edu.byui.apj.storefront.api.service;

import edu.byui.apj.storefront.api.model.TradingCard;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;


@Service
public class TradingCardService {
    private List<TradingCard> tradingCards = new ArrayList<>();

    @PostConstruct
    private void loadData() {
        System.out.println("📥 Loading trading card data from CSV...");

        try {
            ClassPathResource resource = new ClassPathResource("pioneers.csv");
            System.out.println("📂 Resource exists: " + resource.exists());

            if (!resource.exists()) {
                System.err.println("⚠️ CSV file not found. Loading default data.");
                loadDefaultData();
                return;
            }

            try (InputStream is = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                String header = reader.readLine(); // 跳过 CSV 头
                System.out.println("📑 CSV Header: " + header);

                String line;
                Long id = 1L;
                while ((line = reader.readLine()) != null) {
                    try {
                        // 修正 CSV 解析逻辑，确保正确读取 `Price`
                        String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                        if (parts.length >= 6) {
                            Long csvId = Long.parseLong(parts[0]);
                            String name = parts[1].trim().replace("\"", "");
                            String specialty = parts[2].trim().replace("\"", "");
                            String contribution = parts[3].trim().replace("\"", "");
                            String priceStr = parts[4].trim().replace("$", "").replace("\"", "").replace(",", "");
                            String imageUrl = parts[5].trim().replace("\"", "");

                            BigDecimal price;
                            try {
                                price = new BigDecimal(priceStr);
                            } catch (NumberFormatException e) {
                                System.err.println("⚠️ Error parsing price '" + priceStr + "'. Using default value.");
                                price = new BigDecimal("9.99");
                            }

                            TradingCard card = new TradingCard(
                                    csvId,
                                    name,
                                    specialty,
                                    contribution,
                                    new BigDecimal(priceStr),
                                    imageUrl
                            );
                            tradingCards.add(card);
                            System.out.println("✅ Loaded card: " + card);
                        } else {
                            System.err.println("⚠️ Invalid CSV line format: " + line);
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Error processing CSV line: " + line);
                        e.printStackTrace();
                    }
                }
                System.out.println("✅ Successfully loaded " + tradingCards.size() + " trading cards");
            }
        } catch (IOException e) {
            System.err.println("❌ Error loading CSV file:");
            e.printStackTrace();
            loadDefaultData();
        }
    }


    private void loadDefaultData() {
        System.out.println("🔄 Loading default trading card data...");
        tradingCards.add(new TradingCard(1L, "Ada Lovelace", "Mathematician", "First Computer Programmer", new BigDecimal("199.99"), "https://example.com/ada.jpg"));
        tradingCards.add(new TradingCard(2L, "Alan Turing", "Computer Scientist", "Father of Computer Science", new BigDecimal("249.99"), "https://example.com/turing.jpg"));
        tradingCards.add(new TradingCard(3L, "Grace Hopper", "Computer Scientist", "Developed COBOL", new BigDecimal("189.99"), "https://example.com/hopper.jpg"));
        System.out.println("✅ Loaded " + tradingCards.size() + " default trading cards");
    }

    public List<TradingCard> getPaginatedCards(int page, int size) {
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, tradingCards.size());

        if (startIndex >= tradingCards.size()) {
            return new ArrayList<>();
        }

        return tradingCards.subList(startIndex, endIndex);
    }

    public List<TradingCard> filterAndSort(BigDecimal minPrice, BigDecimal maxPrice, String specialty, String sort) {
        System.out.println("🔍 Received filter request:");
        System.out.println("   minPrice = " + minPrice);
        System.out.println("   maxPrice = " + maxPrice);
        System.out.println("   specialty = '" + specialty + "'");
        System.out.println("   sort = " + sort);

        // 先映射 specialty 到人物名称
        Map<String, List<String>> specialtyMapping = new HashMap<>();
        specialtyMapping.put("Networking_Internet", Arrays.asList("Tim Berners-Lee", "Bob Metcalfe", "Roy Fielding"));
        specialtyMapping.put("Algorithms_Theory", Arrays.asList("Alan Turing", "Ada Lovelace"));
        specialtyMapping.put("Programming_Languages", Arrays.asList("Bjarne Stroustrup", "James Gosling", "Guido van Rossum"));
        specialtyMapping.put("Software_Engineering", Arrays.asList("Margaret Hamilton", "Barbara Liskov"));

        List<String> mappedSpecialties = specialtyMapping.getOrDefault(specialty, Arrays.asList(specialty));

        System.out.println("📌 Mapped specialty: " + mappedSpecialties);

        List<TradingCard> filteredCards = tradingCards.stream()
                .filter(card -> minPrice == null || card.getPrice().compareTo(minPrice) >= 0)
                .filter(card -> maxPrice == null || card.getPrice().compareTo(maxPrice) <= 0)
                .filter(card -> specialty == null || specialty.isEmpty() ||
                        mappedSpecialties.contains(card.getSpecialty()))
                .collect(Collectors.toList());

        System.out.println("✅ Found " + filteredCards.size() + " matching cards.");

        if (sort != null) {
            if (sort.equalsIgnoreCase("name")) {
                filteredCards.sort(Comparator.comparing(TradingCard::getName));
            } else if (sort.equalsIgnoreCase("price")) {
                filteredCards.sort(Comparator.comparing(TradingCard::getPrice));
            }
        }

        return filteredCards;
    }





    public List<TradingCard> searchByNameOrContribution(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        String searchQuery = query.toLowerCase();
        List<TradingCard> results = tradingCards.stream()
                .filter(card -> card.getName().toLowerCase().contains(searchQuery) || card.getContribution().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());

        System.out.println("🔎 Search results: " + results.size() + " cards found for query '" + query + "'");
        return results;
    }
}
