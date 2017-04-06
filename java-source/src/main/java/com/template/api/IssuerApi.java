package com.template.api;

import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.ContractsDSL;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.Party;
import net.corda.core.messaging.CordaRPCOps;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.ServiceEntry;
import net.corda.core.transactions.SignedTransaction;
import net.corda.flows.CashFlowCommand;
import net.corda.core.serialization.OpaqueBytes;
import net.corda.core.contracts.Amount;
import net.corda.flows.IssuerFlow;


// This API is accessible from /api/template. The endpoint paths specified below are relative to it.
@Path("v1")
public class IssuerApi {
    private final CordaRPCOps services;
    private List<Party> notaries;

    public IssuerApi(CordaRPCOps services) {
        this.services = services;
        this.updateNotaries();
    }


    public void updateNotaries() {
        notaries = new ArrayList<>();
        for (NodeInfo nf : services.networkMapUpdates().getFirst()) {
            for (ServiceEntry se : nf.getAdvertisedServices()) {
                if (se.getInfo().getType().isNotary()) {
                    notaries.add(nf.getNotaryIdentity());
                }
            }
        }
    }

    @GET
    @Path("issue/{peer}/{money}/{currency}")
    public String testing(@PathParam("peer") String peer, @PathParam("money") long money, @PathParam("currency") String cc) {
        try {

            if (notaries.isEmpty()) {
                updateNotaries();
            }

            Party party = services.partyFromName(peer);

            CashFlowCommand.IssueCash cash = new CashFlowCommand.IssueCash(new Amount<>((long) money, ContractsDSL.currency(cc)), OpaqueBytes.Companion.of((byte) 1), party, notaries.get(0));
            FlowHandle handle = services.startFlowDynamic(IssuerFlow.IssuanceRequester.class, cash.getAmount(), cash.getRecipient(), cash.getIssueRef(), services.nodeIdentity().getLegalIdentity());
            SignedTransaction signedTransaction = (SignedTransaction) handle.getReturnValue().get();
            return signedTransaction.getId().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    @GET
    @Path("transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<ContractState>> transactions() {
        return services.vaultAndUpdates().getFirst();
    }


}
