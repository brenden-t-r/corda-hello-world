package com.template;

import static com.template.DummyLoanContract.DUMMYLOAN_CONTRACT_ID;
import static com.template.IOUContract.IOU_CONTRACT_ID;
import static net.corda.testing.node.NodeTestUtils.ledger;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;


public class ContractTests {
    static private final MockServices ledgerServices = new MockServices();
    static private TestIdentity megaCorp = new TestIdentity(
            new CordaX500Name("MegaCorp", "London", "GB"));
    static private TestIdentity miniCorp = new TestIdentity(
            new CordaX500Name("MiniCorp", "London", "GB"));
    static private TestIdentity bankABC = new TestIdentity(
            new CordaX500Name("BankABC", "London", "GB"));
    static private TestIdentity bankDEF = new TestIdentity(
            new CordaX500Name("BankDEF", "London", "GB"));

    @Test
    public void dummyTest() {

    }

    @Test
    public void dummyLoanCreateShouldHaveNoInputs() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(100.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new DummyLoanContract.Commands.Create());
                tx.failsWith("Transaction should have no inputs.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanCreateShouldHaveOneOutput() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(100.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new DummyLoanContract.Commands.Create());
                tx.failsWith("Only one output state should be created.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanCreateLenderAndBorrowerCannotBeSameEntity() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(100.00, 13.5, megaCorp.getParty(), megaCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new DummyLoanContract.Commands.Create());
                tx.failsWith("The lender and the borrower cannot be the same entity.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanLenderSwapBorrowerMustNotChange() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, bankABC.getParty(), megaCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey(), bankABC.getPublicKey()),
                        new DummyLoanContract.Commands.LenderSwap());
                tx.failsWith("The borrower must not change.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanLenderSwapInterestRateMustNotChange() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 5, bankABC.getParty(), miniCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey(), bankABC.getPublicKey()),
                        new DummyLoanContract.Commands.LenderSwap());
                tx.failsWith("The interest rate must not change.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanLenderSwapLoanAmountMustNotChange() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(10000.00, 13.5, bankABC.getParty(), miniCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey(), bankABC.getPublicKey()),
                        new DummyLoanContract.Commands.LenderSwap());
                tx.failsWith("The loan amount must not change.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanLenderSwapLenderAndBorrowerCannotBeSameEntity() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, miniCorp.getParty(), miniCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey(), bankABC.getPublicKey()),
                        new DummyLoanContract.Commands.LenderSwap());
                tx.failsWith("The new lender and borrower cannot be the same entity.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanLenderSwapOriginalLenderAndNewLenderCannotBeSameEntity() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey(), bankABC.getPublicKey()),
                        new DummyLoanContract.Commands.LenderSwap());
                tx.failsWith("The original lender and new lender cannot be the same entity.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanLenderSwapThreeSigners() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, bankABC.getParty(), miniCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()),
                        new DummyLoanContract.Commands.LenderSwap());
                tx.failsWith("There should be three signers.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void dummyLoanLenderSwapAllParticipantsMustSign() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, megaCorp.getParty(), miniCorp.getParty()));
                tx.output(DUMMYLOAN_CONTRACT_ID, new DummyLoanState(50.00, 13.5, bankABC.getParty(), miniCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey(), bankDEF.getPublicKey()),
                        new DummyLoanContract.Commands.LenderSwap());
                tx.failsWith("All of the participants must be signers.");
                return null;
            });
            return null;
        }));
    }
}