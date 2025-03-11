package edu.byui.apj.storefront.web.controller;

import edu.byui.apj.storefront.web.model.TradingCard;
import edu.byui.apj.storefront.web.service.TradingCardClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class TradingCardController {

    private final TradingCardClientService tradingCardClientService;

    @Autowired
    public TradingCardController(TradingCardClientService tradingCardClientService) {
        this.tradingCardClientService = tradingCardClientService;
    }

    @GetMapping
    public List<TradingCard> getCardsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return tradingCardClientService.getAllCardsPaginated(page, size);
    }

    @GetMapping("/filter")
    public List<TradingCard> filterAndSortCards(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String sort) {
        return tradingCardClientService.filterAndSort(minPrice, maxPrice, specialty, sort);
    }

    @GetMapping("/search")
    public List<TradingCard> searchCards(@RequestParam String query) {
        return tradingCardClientService.searchByNameOrContribution(query);
    }
}