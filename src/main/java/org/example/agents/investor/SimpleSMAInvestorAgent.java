package org.example.agents.investor;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import org.example.datamodels.order.OrderType;

import java.util.*;

public class SimpleSMAInvestorAgent extends InvestorAgent {

    // Przechowywanie historii cen, aby ocenić trendy
    private HashMap<InvestorPriceRecordLabel, List<Double>> priceHistory;

    public SimpleSMAInvestorAgent() {
        super();
        priceHistory = new HashMap<>();
    }

    @Override
    protected void setup() {
        super.setup();
        this.addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                makeDecision();
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
                Double averagePrice = history.stream().mapToDouble(d -> d).average().orElse(0.0);
                Double lastTrend = lastPrice - averagePrice;

                Double brokerBalance = getBalance("Broker1");

                // Strategia: Kup, gdy cena jest poniżej średniej i mamy wystarczająco dużo gotówki
                if (buyPrice != null && lastTrend < 0 && brokerBalance >= buyPrice) {
                    Long quantity = (long) (brokerBalance / buyPrice);
                    // Wpłacamy gotówkę na konto brokera, jeśli nie mamy wystarczających środków
                    if (brokerBalance < buyPrice * quantity) {
                        depositMoney("Broker1", buyPrice * quantity - brokerBalance);
                    }
                    sendMarketOrder(stock.shortName(), OrderType.BUY, buyPrice, quantity, "GPW", "Broker1");
                    brokerBalance -= quantity * buyPrice;
                    System.out.println("Buying " + quantity + " of " + stock.shortName() + " at " + buyPrice);
                }
                // Strategia: Sprzedaj, gdy cena jest powyżej średniej i mamy akcje do sprzedaży
                if (sellPrice != null && lastTrend > 0 && brokerBalance > 0) {
                    Long quantity = (long) (brokerBalance / sellPrice);  // Załóżmy, że mamy akcje do sprzedaży
                    sendMarketOrder(stock.shortName(), OrderType.SELL, sellPrice, quantity, "GPW", "Broker1");
//                    brokerBalance += quantity * sellPrice; // Zwiększamy saldo na koncie brokera
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
