package edu.byui.apj.storefront.web.controller;
import edu.byui.apj.storefront.web.model.TradingCard;
import edu.byui.apj.storefront.web.service.TradingCardClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * This controller adapts MongoDB endpoints to use the standard TradingCardClientService
 * It allows the frontend to use /mongo/cards endpoints while still using the regular service
 */
@RestController
@RequestMapping("/mongo/cards")
public class MongoTradingCardController {

    private static final Logger logger = LoggerFactory.getLogger(MongoTradingCardController.class);
    private final TradingCardClientService tradingCardClientService;

    @Autowired
    public MongoTradingCardController(TradingCardClientService tradingCardClientService) {
        this.tradingCardClientService = tradingCardClientService;
        logger.info("MongoTradingCardController initialized as adapter to standard TradingCardClientService");
    }

    @GetMapping
    public List<TradingCard> getCardsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        logger.info("MongoDB adapter: Fetching paginated cards with page={}, size={}", page, size);
        return tradingCardClientService.getAllCardsPaginated(page, size);
    }

    @GetMapping("/filter")
    public List<TradingCard> filterAndSortCards(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String sort) {
        logger.info("MongoDB adapter: Filtering cards with minPrice={}, maxPrice={}, specialty={}, sort={}",
                minPrice, maxPrice, specialty, sort);
        return tradingCardClientService.filterAndSort(minPrice, maxPrice, specialty, sort);
    }

    @GetMapping("/search")
    public List<TradingCard> searchCards(@RequestParam String query) {
        logger.info("MongoDB adapter: Searching cards with query={}", query);
        return tradingCardClientService.searchByNameOrContribution(query);
    }
}
//
//import edu.byui.apj.storefront.web.model.MongoTradingCard;
//import edu.byui.apj.storefront.web.service.MongoTradingCardClientService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@RestController
//@RequestMapping("/mongo/cards")
//public class MongoTradingCardController {
//
//    private final MongoTradingCardClientService tradingCardService;
//
//    public MongoTradingCardController(MongoTradingCardClientService tradingCardService) {
//        this.tradingCardService = tradingCardService;
//    }
//
//    // ✅ Get all trading cards (Paginated & Sorted)
//    @GetMapping
//    public ResponseEntity<List<MongoTradingCard>> getAllTradingCards(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "9") int size,
//            @RequestParam(required = false) String sort){
//
//        List<MongoTradingCard> cards = tradingCardService.getAllTradingCards(page, size, sort);
//        return ResponseEntity.ok(cards);
//    }
//
//    // ✅ Get a single trading card by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<MongoTradingCard> getTradingCardById(@PathVariable String id) {
//        MongoTradingCard card = tradingCardService.getTradingCardById(id);
//        return ResponseEntity.ok(card);
//    }
//
//    // ✅ Create a new trading card
//    @PostMapping
//    public ResponseEntity<MongoTradingCard> createTradingCard(@RequestBody MongoTradingCard tradingCard) {
//        MongoTradingCard createdCard = tradingCardService.createTradingCard(tradingCard);
//        return ResponseEntity.ok(createdCard);
//    }
//
//    // ✅ Update an existing trading card
//    @PutMapping("/{id}")
//    public ResponseEntity<MongoTradingCard> updateTradingCard(
//            @PathVariable String id,
//            @RequestBody MongoTradingCard updatedCard) {
//
//        MongoTradingCard updated = tradingCardService.updateTradingCard(id, updatedCard);
//        return ResponseEntity.ok(updated);
//    }
//
//    // ✅ Delete a trading card
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTradingCard(@PathVariable String id) {
//        tradingCardService.deleteTradingCard(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // ✅ Filter by specialty
//    @GetMapping("/filter/specialty")
//    public ResponseEntity<List<MongoTradingCard>> filterBySpecialty(@RequestParam String specialty) {
//        List<MongoTradingCard> cards = tradingCardService.filterBySpecialty(specialty);
//        return ResponseEntity.ok(cards);
//    }
//
//    // ✅ Filter by price range
//    @GetMapping("/filter/price")
//    public ResponseEntity<List<MongoTradingCard>> filterByPriceRange(
//            @RequestParam BigDecimal minPrice,
//            @RequestParam BigDecimal maxPrice) {
//
//        List<MongoTradingCard> cards = tradingCardService.filterByPriceRange(minPrice, maxPrice);
//        return ResponseEntity.ok(cards);
//    }
//
//    // ✅ Search trading cards by name or contribution
//    @GetMapping("/search")
//    public ResponseEntity<List<MongoTradingCard>> searchTradingCards(@RequestParam String query) {
//        List<MongoTradingCard> cards = tradingCardService.searchTradingCards(query);
//        return ResponseEntity.ok(cards);
//    }
//}
