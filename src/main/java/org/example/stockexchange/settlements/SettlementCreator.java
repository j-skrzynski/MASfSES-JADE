package org.example.stockexchange.settlements;

import org.example.stockexchange.order.Order;
import org.glassfish.pfl.basic.contain.Pair;

public class SettlementCreator {

    public static Pair<BuyerSettlement, SellerSettlement> createSettlement(Order buyer, Order seller, Long tradedQuantity, Double transactionUnitPrice){
        if( transactionUnitPrice > buyer.getPrice() ){
            throw new RuntimeException("Transaction price is greater than buyer price");
        }
        if( transactionUnitPrice < seller.getPrice() ){
            throw new RuntimeException("Transaction price is less than seller price");
        }

        // BuyerSettlement
        BuyerSettlement buyerSettlement = new BuyerSettlement(
                buyer.getSubmitter(),
                buyer.getSymbol(),
                tradedQuantity,
                transactionUnitPrice
        );

        // SellerSettlement
        SellerSettlement sellerSettlement = new SellerSettlement(
                seller.getSubmitter(),
                seller.getSymbol(),
                tradedQuantity,
                transactionUnitPrice
        );

        return new Pair<>(buyerSettlement, sellerSettlement);
    }
}
