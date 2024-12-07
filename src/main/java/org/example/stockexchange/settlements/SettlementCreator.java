package org.example.stockexchange.settlements;

import org.example.stockexchange.Order;
import org.glassfish.pfl.basic.contain.Pair;

public class SettlementCreator {

    public static Pair<BuyerSettlement, SellerSettlement> createSettlement(Order buyer, Order seller, Double transactionPrice){
        // Znajdujemy najmniejszą ilość akcji możliwą do transakcji
        int tradedQuantity = Math.min(buyer.getQuantity(), seller.getQuantity());

        if( transactionPrice > buyer.getPrice() ){
            throw new RuntimeException("Transaction price is greater than buyer price");
        }
        if( transactionPrice < seller.getPrice() ){
            throw new RuntimeException("Transaction price is less than seller price");
        }

        // BuyerSettlement
        BuyerSettlement buyerSettlement = new BuyerSettlement(
                buyer.getSubmitter(),
                buyer.getSymbol(),
                tradedQuantity,
                transactionPrice
        );

        // SellerSettlement
        SellerSettlement sellerSettlement = new SellerSettlement(
                seller.getSubmitter(),
                seller.getSymbol(),
                tradedQuantity,
                transactionPrice
        );

        return new Pair<>(buyerSettlement, sellerSettlement);
    }
}
