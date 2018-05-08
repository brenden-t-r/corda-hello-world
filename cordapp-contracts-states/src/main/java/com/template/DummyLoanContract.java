package com.template;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class DummyLoanContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String DUMMYLOAN_CONTRACT_ID = "com.template.DummyLoanContract";

    @Override
    public void verify(LedgerTransaction tx) {
        CommandWithParties<Commands> cmd = requireSingleCommand(tx.getCommands(), Commands.class);

        if (cmd.getValue() instanceof Commands.Create) {

            requireThat(require -> {
                // Generic constraints
                require.using("Transaction should have no inputs.", tx.getInputs().isEmpty());
                require.using("Only one output state should be created.",tx.getOutputs().size() == 1);
                final DummyLoanState out = tx.outputsOfType(DummyLoanState.class).get(0);
                require.using("The lender and the borrower cannot be the same entity.",out.getLender() != out.getBorrower());
                require.using("All of the participants must be signers.",
                        cmd.getSigners().containsAll(out.getParticipants().stream()
                                        .map(AbstractParty::getOwningKey)
                                        .collect(Collectors.toList())));

                return null;
            });
        }
        else if (cmd.getValue() instanceof Commands.ChangeInterestRate) {
            System.out.println("CHANGE INTEREST RATE");
        }
        else if (cmd.getValue() instanceof Commands.Terminate) {
            System.out.println("TERMINATE");
        }
        else if (cmd.getValue() instanceof Commands.LenderSwap) {

            requireThat(require -> {
                // Generic constraints
                require.using("Transaction should have only one input.", tx.getInputs().size() == 1);
                require.using("Only one output state should be created.",tx.getOutputs().size() == 1);
                final DummyLoanState in = tx.inputsOfType(DummyLoanState.class).get(0);
                final DummyLoanState out = tx.outputsOfType(DummyLoanState.class).get(0);
                require.using("The borrower must not change.",in.getBorrower().equals(out.getBorrower()));
                require.using("The loan amount must not change.",in.getLoanAmount() == out.getLoanAmount());
                require.using("The interest rate must not change.",in.getInterestRate() == out.getInterestRate());
                require.using("The original lender and new lender cannot be the same entity.", !in.getLender().equals(out.getLender()));
                require.using("The new lender and borrower cannot be the same entity.", !out.getLender().equals(out.getBorrower()));
                require.using("There should be three signers.", cmd.getSigners().size() == 3);
                require.using("All of the participants must be signers.",
                        cmd.getSigners().containsAll(out.getParticipants().stream()
                                .map(AbstractParty::getOwningKey)
                                .collect(Collectors.toList())));

                return null;
            });
        } else {
            throw new IllegalArgumentException("Unrecognised command");
        }
    }

    /**
     * Contract commands
     */
    public interface Commands extends CommandData {
        class Create implements Commands {}
        class ChangeInterestRate implements Commands {}
        class Terminate implements Commands {}
        class LenderSwap implements Commands {}
    }
}