package org.example.agents.investor;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import org.example.datamodels.order.OrderType;

import java.util.*;

public class SimpleSMAInvestorAgent extends InvestorAgent {

    // Przechowywanie historii cen, aby ocenić trendy
    private HashMap<InvestorPriceRecordLabel, List<Double>> priceHistory;

    // Mapa przechowująca ilość posiadanych akcji dla każdego symbolu akcji
    private HashMap<InvestorPriceRecordLabel, Long> ownedStocks;

    public SimpleSMAInvestorAgent() {
        super();
        priceHistory = new HashMap<>();
        ownedStocks = new HashMap<>();
    }

    @Override
    protected void setup() {
        super.setup();


        this.addBehaviour(new WakerBehaviour(this, 0) { // Opóźnienie 3000 ms
            @Override
            protected void onWake() {
                this.getAgent().addBehaviour(new TickerBehaviour(this.getAgent(), 1000) {
                    @Override
                    protected void onTick() {
                        makeDecision();
                    }
                });
            }
        });
    }

    protected void makeDecision() {
        for (InvestorPriceRecordLabel stock : observedStocks) {
            Double buyPrice = bestBuyPrice.get(stock);
            Double sellPrice = bestSellPrice.get(stock);
            Double lastPrice = this.lastPrice.get(stock);

            if (!priceHistory.containsKey(stock)) {
                priceHistory.put(stock, new ArrayList<>());
            }
            priceHistory.get(stock).add(lastPrice);

            if (priceHistory.get(stock).size() > 5) {
                List<Double> history = priceHistory.get(stock);
                Double averagePrice = history.stream().filter(Objects::nonNull).mapToDouble(d -> d).average().orElse(0.0);
                Double lastTrend = lastPrice - averagePrice;

                Double brokerBalance = getBalance("Broker1");

                // Sprawdzamy ilość posiadanych akcji dla tego stocka
                Long ownedQuantity = ownedStocks.getOrDefault(stock, 0L);

                // Strategia: Kup, gdy cena jest poniżej średniej i mamy wystarczająco dużo gotówki
                if (buyPrice != null && buyPrice - averagePrice < 0 && brokerBalance + moneyBalance >= buyPrice) {
                    Long quantity = (long) (((brokerBalance + moneyBalance) * 0.1) / buyPrice);
                    if (brokerBalance < buyPrice*1.05 * quantity) {
                        depositMoney("Broker1", buyPrice*1.05 * quantity - brokerBalance);
                    }
                    sendMarketOrder(stock.shortName(), OrderType.BUY, buyPrice*1.05, quantity, "GPW", "Broker1");
                    ownedStocks.put(stock, ownedQuantity + quantity);
                    brokerBalance -= quantity * buyPrice;
                    System.out.println("Buying " + quantity + " of " + stock.shortName() + " at " + buyPrice);
                }

                // Strategia: Sprzedaj, gdy cena jest powyżej średniej i mamy akcje do sprzedaży
                if (sellPrice != null && sellPrice - averagePrice > 0 && ownedQuantity > 0) {
                    Long quantity = ownedQuantity;
                    sendMarketOrder(stock.shortName(), OrderType.SELL, sellPrice, quantity, "GPW", "Broker1");
                    ownedStocks.put(stock, 0L);
                    System.out.println("Selling " + quantity + " of " + stock.shortName() + " at " + sellPrice);
                }
            }
        }
    }

    @Override
    protected void sendMarketOrder(String shortName, OrderType type, Double price, Long quantity, String exchange, String broker) {
        super.sendMarketOrder(shortName, type, price, quantity, exchange, broker);
        System.out.println("Market Order Sent: " + type + " " + quantity + " of " + shortName + " at " + price);
    }
}
