package edu.byui.apj.storefront.web.service;

import edu.byui.apj.storefront.web.model.TradingCard;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TradingCardClientService {

    private final WebClient webClient;
    private static final String API_BASE_URL = "https://localhost:8081/api/cards";
    private static final Logger logger = LoggerFactory.getLogger(TradingCardClientService.class);

    public TradingCardClientService() {
        // Create SSL Context with trust all certificates strategy for development
        SslContext sslContext;
        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

            this.webClient = WebClient.builder()
                    .baseUrl(API_BASE_URL)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
        } catch (SSLException e) {
            throw new RuntimeException("Failed to create SSL context", e);
        }
    }

    public List<TradingCard> getAllCardsPaginated(int page, int size) {
        logger.info("🔍 Fetching cards from API: {}?page={}&size={}", API_BASE_URL, page, size);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(TradingCard[].class)
                .map(array -> {
                    List<TradingCard> cards = new ArrayList<>();
                    for (TradingCard card : array) {
                        cards.add(card);
                    }
                    return cards;
                })
                .doOnError(e -> logger.error("❌ WebClient request failed: {}", e.getMessage()))  // 记录错误日志
                .onErrorReturn(new ArrayList<>())
                .block();
    }

    public List<TradingCard> filterAndSort(BigDecimal minPrice, BigDecimal maxPrice, String specialty, String sort) {
        return webClient.get()
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
                    return cards;
                })
                .onErrorReturn(new ArrayList<>())
                .block();
    }

    public List<TradingCard> searchByNameOrContribution(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        return webClient.get()
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
                    return cards;
                })
                .onErrorReturn(new ArrayList<>())
                .block();
    }
}