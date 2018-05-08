package com.template;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.List;

public class DummyLoanState implements ContractState {

    private final double loanAmount;
    private final double interestRate;
    private final Party lender;
    private final Party borrower;

    public DummyLoanState(double loanAmount, double interestRate, Party lender, Party borrower) {
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.lender = lender;
        this.borrower = borrower;
    }

    public Party getLender() {
        return lender;
    }

    public Party getBorrower() {
        return borrower;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(lender, borrower);
    }
}