package resource;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import entity.Message;

public class MessagingResource {
     @Incoming("vaidate-content")
    @Outgoing("validation-response")
    public Message validateContent(Message payload) {

        if (!payload.getContent().toUpperCase().contains("YOLO")) {
            payload.setValid(true); 
        } 

        return payload;
    }
}
