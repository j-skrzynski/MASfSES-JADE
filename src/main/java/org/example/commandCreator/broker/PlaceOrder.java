package org.example.commandCreator.broker;

import org.example.datamodels.StockSymbol;
import org.example.datamodels.order.AwaitingOrder;
import org.example.datamodels.order.Order;
import org.example.datamodels.order.OrderType;
import org.example.global.StockDictionary;

public class PlaceOrder extends BrokerCommand {
    private PlaceOrder(AwaitingOrder order) {
        this.setCommand("PLACE_ORDER");
        this.arguments.add(order);
    }

    public static PlaceOrder market_order(
            String shortName,
            OrderType type,
            Double price,
            Long quantity
    ) {
        StockSymbol symbol = StockDictionary.getStockIdByShortName(shortName);
        Order order = new Order(
                symbol,
                type,
                price,
                quantity
        );
        AwaitingOrder aw_order = new AwaitingOrder(order, false, 0.0);
        return new PlaceOrder(aw_order);
    }

    public static PlaceOrder limitless_order(
            String shortName,
            OrderType type,
            Long quantity
    ) {
        StockSymbol symbol = StockDictionary.getStockIdByShortName(shortName);
        Order order = new Order(
                symbol,
                type,
                null,
                quantity
        );
        AwaitingOrder aw_order = new AwaitingOrder(order, false, 0.0);
        return new PlaceOrder(aw_order);
    }

    public static PlaceOrder awaiting_market_order(
            String shortName,
            OrderType type,
            Double price,
            Long quantity,
            Double activationPrice
    ) {
        StockSymbol symbol = StockDictionary.getStockIdByShortName(shortName);
        Order order = new Order(
                symbol,
                type,
                price,
                quantity
        );
        AwaitingOrder aw_order = new AwaitingOrder(order, true, activationPrice);
        return new PlaceOrder(aw_order);
    }

    public static PlaceOrder awaiting_limitless_market_order(
            String shortName,
            OrderType type,
            Long quantity,
            Double activationPrice
    ) {
        StockSymbol symbol = StockDictionary.getStockIdByShortName(shortName);
        Order order = new Order(symbol, type, null, quantity);
        AwaitingOrder aw_order = new AwaitingOrder(order, true, activationPrice);
        return new PlaceOrder(aw_order);
    }

    public PlaceOrder setExpirationSpecification(String specification) {
        ((AwaitingOrder) this.arguments.getFirst())
                .order()
                .setExpirationSpecification(specification);
        return this;
    }

    public PlaceOrder expD() {
        return this.setExpirationSpecification("D");
    }

    public PlaceOrder expWDD(int sessions) {
        return this.setExpirationSpecification("WDD/" + sessions);
    }

    public PlaceOrder expWDA() {
        return this.setExpirationSpecification("WDA");
    }

    public PlaceOrder expWDC(int sessions, int seconds) {
        return this.setExpirationSpecification("WDC/" + sessions + "/" + seconds);
    }

}
