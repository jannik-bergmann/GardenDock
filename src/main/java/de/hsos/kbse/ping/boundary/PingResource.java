package de.hsos.kbse.ping.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author airhacks.com
 */
// Funny Funny Funny

@Path("ping")
public class PingResource {

    @GET
    public String ping() {
        return "Enjoy Java EE 8!";
    }

}
