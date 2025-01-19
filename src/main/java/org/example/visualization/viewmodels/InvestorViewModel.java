package org.example.visualization.viewmodels;

public class InvestorViewModel implements Comparable<InvestorViewModel> {
    private double moneyBalance;

    public InvestorViewModel(double moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public double getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(double moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    @Override
    public int compareTo(InvestorViewModel otherModel) {
        if (otherModel == null) {
            return -1;
        }

        return Double.compare(moneyBalance, otherModel.moneyBalance);
    }
}
