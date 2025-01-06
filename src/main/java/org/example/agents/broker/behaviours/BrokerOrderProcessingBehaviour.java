package org.example.agents.broker.behaviours;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.example.agents.broker.BrokerAgent;
import org.example.datamodels.TransactionResult;
import org.example.datamodels.command.Command;
import org.example.datamodels.command.CommandParser;
import org.example.datamodels.order.AwaitingOrder;
import org.example.logic.broker.InvestorRequest;

public class BrokerOrderProcessingBehaviour extends CyclicBehaviour {

    private final BrokerAgent agent;
    private final CommandParser commandParser = new CommandParser();
    private final Gson gson = new Gson();

    public BrokerOrderProcessingBehaviour(BrokerAgent agent) {
        super(agent);
        this.agent = agent;
    }

    private void sendReply(ACLMessage msg, String content) {
        sendReply(msg, content, ACLMessage.INFORM);
    }

    private void sendReply(ACLMessage msg, String content, int type) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(type);
        reply.setContent(content);
        agent.send(reply);
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive();
        if (msg != null && msg.getPerformative() != ACLMessage.FAILURE) {
            try {
                String jsonContent = msg.getContent();

                // Parsowanie JSON-a do obiektu Command
                Command command = commandParser.parseCommand(jsonContent);

                switch (command.getCommand()) {
                    case "REGISTER":
                        agent.getStockBroker().registerTrader(command.getTraderName());
                        sendReply(msg, "Trader " + command.getTraderName() + " registered successfully.");
                        break;
                    case "ADD_MARKET":
                        String marketName = (String) command.getArguments().getFirst();
                        agent.getStockBroker().addStockExchange(marketName);
                        sendReply(msg, "Added");
                        break;
                    case "DEPOSIT":
                        double depositAmount = (double) command.getArguments().getFirst();
                        agent.getStockBroker().deposit(command.getTraderName(), depositAmount);
                        sendReply(msg, "Deposited " + depositAmount + " to " + command.getTraderName() + ".");
                        break;
                    case "WITHDRAW":
                        double withdrawAmount = (double) command.getArguments().getFirst();
                        agent.getStockBroker().withdraw(command.getTraderName(), withdrawAmount);
                        sendReply(msg, "Withdrew " + withdrawAmount + " from " + command.getTraderName() + ".");
                        break;
                    case "PLACE_ORDER":
                        Object orderSpec = command.getArguments().getFirst();
                        if (!(orderSpec instanceof AwaitingOrder)) {
                            throw new IllegalArgumentException("PLACE_ORDER requires an AwaitingOrder");
                        }
                        InvestorRequest req = new InvestorRequest((AwaitingOrder) orderSpec, command.getStockExchangeName());
                        String newOrderId = agent.getStockBroker().placeOrder(command.getTraderName(), req);
                        command.setBrokerOrderId(newOrderId);
                        AID exchangeAddress = agent.getStockBroker().getExchangeAddressee(command.getExchangeName());

                        ACLMessage forwardMsg = new ACLMessage(ACLMessage.REQUEST);
                        forwardMsg.addReceiver(exchangeAddress);
                        forwardMsg.setContent(gson.toJson(command));
                        agent.send(forwardMsg);

                        //wyśli zjsonowany command na adres giełdy
                        sendReply(msg, "Order placed successfully for " + command.getTraderName() + ".");
                        break;

                    case "CANCEL_ORDER":
                        String orderId = command.getBrokerOrderId();
                        agent.getStockBroker().cancelOrder(command.getTraderName(), orderId);
                        break;
                    case "GET_BALANCE":
                        double balance = agent.getStockBroker().getMoneyBalance(command.getTraderName());
                        //sendReply(msg, "Balance for " + command.getTraderName() + ": " + balance);
                        sendReply(msg, "" + balance);
                        break;
                    case "GET_PORTFOLIO":
                        String portfolio = agent.getStockBroker().getInvestorPortfolio(command.getTraderName()).toString();
                        sendReply(msg, "Portfolio for " + command.getTraderName() + ": " + portfolio);
                        break;
                    case "SETTLEMENT":
                        Object settlementDetails = command.getArguments().getFirst();
                        if (settlementDetails instanceof String) {
                            TransactionResult transactionResult = gson.fromJson((String) settlementDetails, TransactionResult.class);
                            agent.getStockBroker().notifyOnSettlement(command.getTraderName(), transactionResult);
                        } else {
                            throw new IllegalArgumentException("SETTLEMENT requires a TransactionResult object as JSON string.");
                        }
                        break;
                    case "INFORM":
                        break;
                    case "ERROR":
                        break;
                    default:
                        sendReply(msg, "Unknown command: " + command.getCommand());
                        break;
                }
            } catch (IllegalArgumentException | JsonSyntaxException e) {
                sendReply(msg, "Error processing request: Invalid JSON command - " + e.getMessage(), ACLMessage.FAILURE);
                e.printStackTrace();
            } catch (Exception e) {
                sendReply(msg, "Error processing request: " + e.getMessage(), ACLMessage.FAILURE);
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
