package com.template;

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

    @Test
    public void dummyTest() {

    }

    @Test
    public void emptyLedger() {
        ledger(ledgerServices, l -> {
            return null;
        });
    }


    @Test
    public void transactionMustIncludeCreateCommand() {
        int iou = 1;
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOU_CONTRACT_ID, new IOUState(iou, miniCorp.getParty(), megaCorp.getParty()));
                tx.fails();
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

    @Test
    public void transactionMustHaveNoInputs() {
        int iou = 1;
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(IOU_CONTRACT_ID, new IOUState(iou, miniCorp.getParty(), megaCorp.getParty()));
                tx.output(IOU_CONTRACT_ID, new IOUState(iou, miniCorp.getParty(), megaCorp.getParty()));
                tx.command(ImmutableList.of(megaCorp.getPublicKey(), miniCorp.getPublicKey()), new IOUContract.Create());
                tx.failsWith("No inputs should be consumed when issuing an IOU.");
                return null;
            });
            return null;
        }));
    }

}