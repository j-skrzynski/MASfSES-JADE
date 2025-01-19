package org.example;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import org.example.agents.DummyAgent;
import org.example.agents.broker.BrokerAgent;
import org.example.agents.investor.InvestorAgent;
import org.example.agents.investor.InvestorPriceRecordLabel;
import org.example.agents.investor.SimpleSMAInvestorAgent;
import org.example.agents.stockexchange.StockExchangeAgent;
import org.example.datamodels.EnvRecordQueueCreator;
import org.example.datamodels.StockSymbol;
import org.example.logic.stockexchange.utils.EnvRecord;
import org.example.visualization.AgentWindowManager;
import org.example.visualization.viewmodels.InvestorViewModel;
import org.example.visualization.viewmodels.StockExchangeViewModel;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");

        AgentContainer mainContainer = runtime.createMainContainer(profile);
        try {
            mainContainer.start();
            System.out.println("JADE container started!");

            // Tworzenie i uruchamianie agentów
            Double doubleArray[] = new Double[]{148.5, 150.47, 152.59, 152.99, 155.85, 155.0, 157.4, 159.28, 157.83, 158.93, 160.25, 158.28, 157.65, 160.77, 162.36, 164.9, 166.17, 165.63, 163.76, 164.66, 162.03, 160.8, 160.1, 165.56, 165.21, 165.23, 166.47, 167.63, 166.65, 165.02, 165.33, 163.77, 163.76, 168.41, 169.68, 169.59, 168.54, 167.45, 165.79, 173.57, 173.5, 171.77, 173.56, 173.75, 172.57, 172.07, 172.07, 172.69, 175.05, 175.16, 174.2, 171.56, 171.84, 172.99, 175.43, 177.3, 177.25, 180.09, 180.95, 179.58, 179.21, 177.82, 180.57, 180.96, 183.79, 183.31, 183.95, 186.01, 184.92, 185.01, 183.96, 187.0, 186.68, 185.27, 188.06, 189.25, 189.59, 193.97, 192.46, 191.33, 191.81, 190.68, 188.61, 188.08, 189.77, 190.54, 190.69, 193.99, 193.73, 195.1, 193.13, 191.94, 192.75, 193.62, 194.5, 193.22, 195.83, 196.45, 195.61, 192.58, 191.17, 181.99, 178.85, 179.8, 178.19, 177.97, 177.79, 179.46, 177.45, 176.57, 174.0, 174.49, 175.84, 177.23, 181.12, 176.38, 178.61, 180.19, 184.12, 187.65, 187.87, 189.46, 189.7, 182.91, 177.56, 178.18, 179.36, 176.3, 174.21, 175.74, 175.01, 177.97, 179.07, 175.49, 173.93, 174.79, 176.08, 171.96, 170.43, 170.69, 171.21, 173.75, 172.4, 173.66, 174.91, 177.49, 178.99, 178.39, 179.8, 180.71, 178.85, 178.72, 177.15, 175.84, 175.46, 172.88, 173.0, 173.44, 171.1, 166.89, 168.22, 170.29, 170.77, 173.97, 177.57, 176.65, 179.23, 181.82, 182.89, 182.41, 186.4, 184.8, 187.44, 188.01, 189.71, 189.69, 191.45, 190.64, 191.31, 189.97, 189.79, 190.4, 189.37, 189.95, 191.24, 189.43, 193.42, 192.32, 194.27, 195.71, 193.18, 194.71, 197.96, 198.11, 197.57, 195.89, 196.94, 194.83, 194.68, 193.6, 193.05, 193.15, 193.58, 192.53, 185.64, 184.25, 181.91, 181.18, 185.56, 185.14, 186.19, 185.59, 185.92, 183.63, 182.68, 188.63, 191.56, 193.89, 195.18, 194.5, 194.17, 192.42, 191.73, 188.04, 184.4, 186.86, 185.85, 187.68, 189.3, 189.41, 188.32, 188.85, 187.15, 185.04, 184.15, 183.86, 182.31, 181.56, 182.32, 184.37, 182.52, 181.16, 182.63, 181.42, 180.75, 179.66, 175.1, 170.12, 169.12, 169.0, 170.73, 172.75, 173.23, 171.13, 173.0, 172.62, 173.72, 176.08, 178.67, 171.37, 172.28, 170.85, 169.71, 173.31, 171.48, 170.03, 168.84, 169.65, 168.82, 169.58, 168.45, 169.67, 167.78, 175.04, 176.55, 172.69, 169.38, 168.0, 167.04, 165.0, 165.84, 166.9, 169.02, 169.89, 169.3, 173.5, 170.33, 169.3, 173.03, 183.38, 181.71, 182.4, 182.74, 184.57, 183.05, 186.28, 187.43, 189.72, 189.84, 189.87, 191.04, 192.35, 190.9, 186.88, 189.98, 189.99, 190.29, 191.29, 192.25, 194.03, 194.35, 195.87, 194.48, 196.89, 193.12, 207.15, 213.07, 214.24, 212.49, 216.67, 214.29, 209.68, 207.49, 208.14, 209.07, 213.25, 214.1, 210.62, 216.75, 220.27, 221.55, 226.34, 227.82, 228.68, 232.98, 227.57, 230.54, 234.4, 234.82, 228.88, 224.18, 224.31, 223.96, 225.01, 218.54, 217.49, 217.96, 218.24, 218.8, 222.08, 218.36, 219.86, 209.27, 207.23, 209.82, 213.31, 216.24, 217.53, 221.27, 221.72, 224.72, 226.05, 225.89, 226.51, 226.4, 224.53, 226.84, 227.18, 228.03, 226.49, 229.79, 229.0, 222.77, 220.85, 222.38, 220.82, 220.91, 220.11, 222.66, 222.77, 222.5, 216.32, 216.79, 220.69, 228.87, 228.2, 226.47, 227.37, 226.37, 227.52, 227.79, 233.0, 226.21, 226.78, 225.67, 226.8, 221.69, 225.77, 229.54, 229.04, 227.55, 231.3, 233.85, 231.78, 232.15, 235.0, 236.48, 235.86, 230.76, 230.57, 231.41, 233.4, 233.67, 230.1, 225.91, 222.91, 222.01, 223.45, 222.72, 227.48, 226.96, 224.23, 224.23, 225.12, 228.22, 225.0, 228.02, 228.28, 229.0, 228.52, 229.87, 232.87, 235.06, 234.93, 237.33, 239.59, 242.65, 243.01, 243.04, 242.84, 246.75, 247.77, 246.49, 247.96, 248.13, 251.04, 253.48, 248.05, 249.79, 254.49, 255.27, 258.2, 259.02, 255.59, 252.2, 250.42, 243.85};
            Queue<Double> prices = new LinkedList<>(Arrays.asList(doubleArray));
            Queue<EnvRecord> rec = EnvRecordQueueCreator.getQueueFromPriceList("AAPL",prices,500L);
//            Queue<EnvRecord> rec = new LinkedList<EnvRecord>();
//            rec.add(new EnvRecord("AAPL",2.0,10000L));
//            rec.add(new EnvRecord("AAPL",2.0,10000L));
//            rec.add(new EnvRecord("AAPL",1.0,10000L));
            HashMap<String,Queue<EnvRecord>> baseline = new HashMap<>();
            baseline.put("AAPL",rec);

            List<StockSymbol> supportedStocks = new ArrayList<>();
            supportedStocks.add(new StockSymbol("Apple","AAPL",148.5,10000L));

            AgentController gpwAgent = createAgent(mainContainer,
                    "GPW",
                    StockExchangeAgent.class.getName(),
                    new Object[]{"GPW", supportedStocks, baseline},
                    new StockExchangeViewModel());

            Object[] agentArgs2 = {};
            AgentController brokerAgent = mainContainer.createNewAgent(
                    "Broker1",
                    BrokerAgent.class.getName(),
                    agentArgs2
            );
            brokerAgent.start();
            gpwAgent.start();

            createAgent(mainContainer,
                    "DummyAgent",
                    DummyAgent.class.getName(),
                    null,
                    new InvestorViewModel(0));

            List<InvestorPriceRecordLabel> observedStocks = new ArrayList<>();
            observedStocks.add(new InvestorPriceRecordLabel("AAPL","GPW"));

            Object[] investorAgentArgs = new Object[] {observedStocks, 10000.0};
            createAgent(mainContainer,
                    "InvestorAgent",
                    InvestorAgent.class.getName(),
                    investorAgentArgs,
                    new InvestorViewModel((double) investorAgentArgs[1]));

            Object[] ssmaInvestorAgentArgs = new Object[]{observedStocks, 10000.0};
            createAgent(mainContainer,
                    "SSMAInvestorAgent",
                    SimpleSMAInvestorAgent.class.getName(),
                    ssmaInvestorAgentArgs,
                    new InvestorViewModel((double) ssmaInvestorAgentArgs[1]));

            // Tworzenie agenta Sniffer
            String[] snifferTargets = {"GPW", "Broker1", "DummyAgent","InvestorAgent","SSMAInvestorAgent"};
            String snifferTargetArgs = String.join(";", snifferTargets);

            AgentController snifferAgent = mainContainer.createNewAgent(
                    "SnifferAgent",
                    "jade.tools.sniffer.Sniffer", // Klasa agenta Sniffer
                    new Object[]{snifferTargetArgs} // Przekazanie nazw agentów
            );
            snifferAgent.start();

            System.out.println("SnifferAgent started and listening to messages!");
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    private static AgentController createAgent(AgentContainer mainContainer,
                                    String name,
                                    String className,
                                    Object[] args,
                                    Object initialViewModelValue) throws StaleProxyException {
        AgentController newAgent = mainContainer.createNewAgent(name, className, args);
        newAgent.start();

        System.out.printf("%s agent started!%n", name);

        AgentWindowManager.getInstance().addAgentWindow(name, initialViewModelValue);

        return newAgent;
    }
}
