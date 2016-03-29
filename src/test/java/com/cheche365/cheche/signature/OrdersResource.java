
package com.cheche365.cheche.signature;


import javax.ws.rs.*;

@Path("/orders")
public class OrdersResource  {

    @POST
    @Produces("text/plain")
    public String handle(@QueryParam("orderNo") String orderNo) {
        return "Success";
    }
}

