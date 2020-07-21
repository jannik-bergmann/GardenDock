package de.hsos.kbse.ping.boundary;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author airhacks.com
 */
// Funny Funny Funny

@Path("ping")
@ManagedBean(eager=true)
@ApplicationScoped
public class PingResource {
    
    @Inject
    IotGateway iotgateway;
    
    @GET
    public String ping() {
        return "Enjoy Java EE 8!";
    }

    public PingResource() {
        System.out.println("PingResource erstellt");
        
        try {
            // Setup jms connection as receiver
            Context context = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup("jms/TopicFactory");
            Connection connection = topicFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("jms.Topic");
            
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(new ConsumerMessageListener("consumer 1"));
            connection.start();
        } catch (NamingException | JMSException ex) {
            Logger.getLogger(PingResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
