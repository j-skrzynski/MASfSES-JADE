//package org.example.agents.broker.behaviours;
//
//import jade.core.behaviours.CyclicBehaviour;
//import jade.lang.acl.ACLMessage;
//import org.example.agents.broker.BrokerAgent;
//import org.example.agents.stockexchange.StockExchangeAgent;
//import org.example.datamodels.TransactionResult;
//import org.example.logic.broker.InvestorRequest;
//
//public class BrokerOrderProcessingBehaviour extends CyclicBehaviour {
//
//    private BrokerAgent agent;
//    public BrokerOrderProcessingBehaviour(BrokerAgent agent) {
//        super(agent);
//        this.agent = agent;
//    }
//
//    private void sendReply(ACLMessage msg, String content) {
//        ACLMessage reply = msg.createReply();
//        reply.setPerformative(ACLMessage.INFORM);
//        reply.setContent(content);
//        agent.send(reply);
//    }
//
//    @Override
//    public void action() {
//        ACLMessage msg = agent.receive();
//        if (msg != null) {
//            try {
//                String content = msg.getContent();
//                String[] parts = content.split(";");
//                String command = parts[0];
//                String traderName = parts[1];
//
//                switch (command) {
//                    case "REGISTER":
//                        stockBroker.registerTrader(traderName);
//                        sendReply(msg, "Trader " + traderName + " registered successfully.");
//                        break;
//                    case "DEPOSIT":
//                        double amount = Double.parseDouble(parts[2]);
//                        stockBroker.deposit(traderName, amount);
//                        sendReply(msg, "Deposited " + amount + " to " + traderName + ".");
//                        break;
//                    case "WITHDRAW":
//                        double withdrawAmount = Double.parseDouble(parts[2]);
//                        stockBroker.withdraw(traderName, withdrawAmount);
//                        sendReply(msg, "Withdrew " + withdrawAmount + " from " + traderName + ".");
//                        break;
//                    case "PLACE_ORDER":
//                        InvestorRequest request = parseInvestorRequest(parts);
//                        stockBroker.placeOrder(traderName, request);
//                        sendReply(msg, "Order placed successfully for " + traderName + ".");
//                        break;
//                    case "CANCEL_ORDER":
//                        String orderId = parts[2];
//                        stockBroker.cancelOrder(traderName, orderId);
//                        sendReply(msg, "Order " + orderId + " cancelled for " + traderName + ".");
//                        break;
//                    case "GET_BALANCE":
//                        double balance = stockBroker.getMoneyBalance(traderName);
//                        sendReply(msg, "Balance for " + traderName + ": " + balance);
//                        break;
//                    case "GET_PORTFOLIO":
//                        String portfolio = stockBroker.getInvestorPortfolio(traderName).toString();
//                        sendReply(msg, "Portfolio for " + traderName + ": " + portfolio);
//                        break;
//                    case "SETTLEMENT":
//                        TransactionResult result = parseTransactionResult(parts);
//                        stockBroker.notifyOnSettlement(traderName, result);
//                        sendReply(msg, "Transaction result processed for " + traderName + ".");
//                        break;
//                    default:
//                        sendReply(msg, "Unknown command: " + command);
//                        break;
//                }
//            } catch (Exception e) {
//                sendReply(msg, "Error processing request: " + e.getMessage());
//                e.printStackTrace();
//            }
//        } else {
//            block();
//        }
//    }
//}
