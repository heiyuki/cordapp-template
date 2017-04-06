package com.template.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("controller")
public class ControllerApi {

    public ControllerApi() {
    }


    @GET
    @Path("me")
    public String index(){
        return "this is Corda's Central Controller";
    }
}
