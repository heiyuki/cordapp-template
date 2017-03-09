package com.template.api;

import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.contracts.*;
import net.corda.core.crypto.Party;
import net.corda.core.node.NodeInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;


// This API is accessible from /api/template. The endpoint paths specified below are relative to it.
@Path("template")
public class TemplateApi {

    private final String myLegalName;
    private final CordaRPCOps services;
    private final String NOTARY_NAME = "Controller";
    private List<Party> peers;

    public TemplateApi(CordaRPCOps services) {
        this.myLegalName = services.nodeIdentity().getLegalIdentity().getName();
        this.services = services;
        updatePeers();
    }

    private void updatePeers() {
        peers = new ArrayList<>();

        peers = services.networkMapUpdates().getFirst()
                .stream()
                .map(NodeInfo::getLegalIdentity)
                .filter(name -> !name.equals(myLegalName) && !name.equals(NOTARY_NAME))
                .collect(toList());
    }

    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<String>> getPeers() {
        return singletonMap(
                "peers",
                services.networkMapUpdates().getFirst()
                        .stream()
                        .map(node -> node.getLegalIdentity().getName())
                        .filter(name -> !name.equals(myLegalName) && !name.equals(NOTARY_NAME))
                        .collect(toList()));
    }
}
