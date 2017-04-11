package com.template.api;

import net.corda.core.contracts.*;
import net.corda.core.crypto.Party;
import net.corda.core.messaging.CordaRPCOps;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.ServiceEntry;
import net.corda.core.transactions.SignedTransaction;
import net.corda.flows.CashFlowCommand;
import net.corda.core.serialization.OpaqueBytes;
import net.corda.flows.IssuerFlow;


// This API is accessible from /api/template. The endpoint paths specified below are relative to it.
@Path("v1")
public class IssuerApi {

    private final CordaRPCOps services;
    private String me;
    private List<Party> peers;
    private List<Party> notaries;
    private List<Party> issuers;
    private List<Party> shops;

    public IssuerApi(CordaRPCOps services) {
        this.services = services;
        this.me = services.nodeIdentity().getLegalIdentity().getName();
        this.updatePeers();
        this.updateNotaries();
        this.updateIssuers();
    }


    public String issueMoney(String peer,String cc,long money){
        try {

            if (notaries.isEmpty()) {
                updateNotaries();
            }

            Party party = services.partyFromName(peer);
            Currency curr = ContractsDSL.USD;
            if (cc.equals("USD")) {
                curr = ContractsDSL.USD;
            } else if (cc.equals("EUR")) {
                curr = ContractsDSL.EUR;
            } else if (cc.equals("CHF")) {
                curr = ContractsDSL.CHF;
            } else if (cc.equals("GBP")) {
                curr = ContractsDSL.GBP;
            }
            CashFlowCommand.IssueCash cash = new CashFlowCommand.IssueCash(new Amount<>((long) money, curr), OpaqueBytes.Companion.of((byte) 1), party, notaries.get(0));
            FlowHandle handle = services.startFlowDynamic(IssuerFlow.IssuanceRequester.class, cash.getAmount(), cash.getRecipient(), cash.getIssueRef(), services.nodeIdentity().getLegalIdentity());

            SignedTransaction signedTransaction = (SignedTransaction) handle.getReturnValue().get();
            return signedTransaction.getId().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @GET
    @Path("issue/{peer}/{money}/{currency}")
    public String testing(@PathParam("peer") String peer, @PathParam("money") long money, @PathParam("currency") String cc) {

        if (this.isIssuer(this.me)) {
            return this.issueMoney(peer,cc,money);
        } else {
            return "Not An Issuer";
        }
    }

    @GET
    @Path("transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<ContractState>> transactions() {
        return services.vaultAndUpdates().getFirst();
    }


    @GET
    @Path("transfer/{peer}/{money}/{currency}")
    public String transfer(@PathParam("peer") String peer, @PathParam("money") long money, @PathParam("currency") String cc) {
        if (!this.isIssuer(peer)) {
            try {
                Party party = services.partyFromName(peer);
                Currency curr = ContractsDSL.USD;
                if (cc.equals("USD")) {
                    curr = ContractsDSL.USD;
                } else if (cc.equals("EUR")) {
                    curr = ContractsDSL.EUR;
                } else if (cc.equals("CHF")) {
                    curr = ContractsDSL.CHF;
                } else if (cc.equals("GBP")) {
                    curr = ContractsDSL.GBP;
                }
                Amount<Issued<Currency>> a = new Amount<Issued<Currency>>((long) money, new Issued<>(new PartyAndReference(issuers.get(0), OpaqueBytes.Companion.of((byte) 1)), curr));
                CashFlowCommand.PayCash cash = new CashFlowCommand.PayCash(a, party);


                FlowHandle handle = cash.startFlow(services);

                SignedTransaction signedTransaction = (SignedTransaction) handle.getReturnValue().get();
                return signedTransaction..getId().toString();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (ExecutionException e) {
                e.printStackTrace();
                return e.getMessage();
            }

        } else {
            return "";
        }
    }



    private void updatePeers() {
        peers = new ArrayList<>();

        peers = services.networkMapUpdates().getFirst()
                .stream()
                .map(NodeInfo::getLegalIdentity)
                .filter(name -> !name.equals(this.me) && !name.equals("Controller"))
                .collect(Collectors.toList());
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

    private void updateIssuers() {
        issuers = new ArrayList<>();
        for (NodeInfo nodeInfo :
                services.networkMapUpdates().getFirst()) {
            for (ServiceEntry serviceEntry :
                    nodeInfo.getAdvertisedServices()) {
                if (serviceEntry.getInfo().getType().getId().contains("corda.issuer.")) {
                    issuers.add(nodeInfo.getLegalIdentity());
                }
            }
        }
    }

    private void updateShops() {
        shops = new ArrayList<>();
        for (NodeInfo nodeInfo :
                services.networkMapUpdates().getFirst()) {
            for (ServiceEntry serviceEntry :
                    nodeInfo.getAdvertisedServices()) {
                if (serviceEntry.getInfo().getType().getId().contains("corda.shop.")) {
                    shops.add(nodeInfo.getLegalIdentity());
                }
            }
        }
    }

    private boolean isIssuer(String name) {
        this.updateIssuers();
        boolean verif = false;
        for (Party p : this.issuers) {
            if (p.getName().equals(name)) {
                verif = true;
                break;
            }
        }
        return verif;
    }

}
