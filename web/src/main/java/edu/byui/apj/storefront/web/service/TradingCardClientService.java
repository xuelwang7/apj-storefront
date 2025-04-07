package edu.byui.apj.storefront.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.byui.apj.storefront.web.model.TradingCard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TradingCardClientService {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(TradingCardClientService.class);

    public TradingCardClientService(@Value("${tradingcard.service.url}/api/cards}") String apiBaseUrl) {
        logger.info("🔌 Initializing Trading Card Client Service with API URL: {}", apiBaseUrl);

        this.webClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .build();

        logger.info("✅ WebClient successfully initialized");
    }


    public List<TradingCard> getAllCardsPaginated(int page, int size) {
        try {

            String rawJson = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("🔵 [WebClient raw JSON]: " + rawJson);

            // 2) 再用 Jackson 手动反序列化
            ObjectMapper mapper = new ObjectMapper();
            TradingCard[] cardsArray = mapper.readValue(rawJson, TradingCard[].class);

            // 3) 打印看看解析后的对象
            System.out.println("🟢 [Parsed objects]: " + Arrays.toString(cardsArray));

            // 4) 转成 List
            return Arrays.asList(cardsArray);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }




//    public List<TradingCard> getAllCardsPaginated(int page, int size) {
//        logger.info("🔍 Fetching cards from API with page={} and size={}", page, size);
//        try {
//            // Just get the data as a TradingCard array and convert to list
//            TradingCard[] cardsArray = webClient.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .queryParam("page", page)
//                            .queryParam("size", size)
//                            .build())
//                    .retrieve()
//                    .bodyToMono(TradingCard[].class)
//                    .doOnError(e -> {
//                        logger.error(" WebClient request failed: {}", e.getMessage(), e);
//                    })
//                    .onErrorReturn(new TradingCard[0])
//                    .block();
//            System.out.println("🔵 [WebClient raw JSON]: " + cardsArray);
//
//            List<TradingCard> cards = cardsArray != null ? Arrays.asList(cardsArray) : new ArrayList<>();
//            logger.info("✅ Successfully fetched {} cards", cards.size());
//            return cards;
//        } catch (Exception e) {
//            logger.error(" Unexpected error fetching paginated cards", e);
//            return new ArrayList<>();
//        }
//    }

    public List<TradingCard> filterAndSort(BigDecimal minPrice, BigDecimal maxPrice, String specialty, String sort) {
        logger.info("🔍 Filtering and sorting cards with minPrice={}, maxPrice={}, specialty={}, sort={}",
                minPrice, maxPrice, specialty, sort);

        try {
            List<TradingCard> result = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder = uriBuilder.path("/filter");

                        if (minPrice != null) {
                            uriBuilder = uriBuilder.queryParam("minPrice", minPrice);
                        }
                        if (maxPrice != null) {
                            uriBuilder = uriBuilder.queryParam("maxPrice", maxPrice);
                        }
                        if (specialty != null && !specialty.isEmpty()) {
                            uriBuilder = uriBuilder.queryParam("specialty", specialty);
                        }
                        if (sort != null && !sort.isEmpty()) {
                            uriBuilder = uriBuilder.queryParam("sort", sort);
                        }

                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(TradingCard[].class)
                    .map(array -> {
                        List<TradingCard> cards = new ArrayList<>();
                        for (TradingCard card : array) {
                            cards.add(card);
                        }
                        logger.info("✅ Successfully filtered and found {} cards", cards.size());
                        return cards;
                    })
                    .doOnError(e -> {
                        logger.error(" Filter request failed: {}", e.getMessage(), e);
                    })
                    .onErrorReturn(new ArrayList<>())
                    .block();

            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            logger.error(" Unexpected error during filter and sort", e);
            return new ArrayList<>();
        }
    }

    public List<TradingCard> searchByNameOrContribution(String query) {
        if (query == null || query.isEmpty()) {
            logger.info("⚠️ Empty search query provided");
            return new ArrayList<>();
        }

        logger.info("🔍 Searching cards with query: '{}'", query);

        try {
            List<TradingCard> result = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("query", query)
                            .build())
                    .retrieve()
                    .bodyToMono(TradingCard[].class)
                    .map(array -> {
                        List<TradingCard> cards = new ArrayList<>();
                        for (TradingCard card : array) {
                            cards.add(card);
                        }
                        logger.info("✅ Search found {} cards for query '{}'", cards.size(), query);
                        return cards;
                    })
                    .doOnError(e -> {
                        logger.error("❌ Search request failed: {}", e.getMessage(), e);
                    })
                    .onErrorReturn(new ArrayList<>())
                    .block();

            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            logger.error("❌ Unexpected error during search", e);
            return new ArrayList<>();
        }
    }
}