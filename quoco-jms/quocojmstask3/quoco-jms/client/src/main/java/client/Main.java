package client;

import org.apache.activemq.ActiveMQConnectionFactory;
import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuotationRequestMessage;
import service.core.QuotationResponseMessage;

import javax.jms.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class Main {


    /**
     * Test Data
     */
    public static final ClientInfo[] clients = {
            new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1"),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 0, 2, "ABC123/4"),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 16, 10, 0, "HMA304/9"),
            new ClientInfo("Rem Collier", ClientInfo.MALE, 44, 5, 3, "COL123/3"),
            new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 4, 7, "QUN987/4"),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 5, 2, "XYZ567/9")
    };
    static long SEED_ID = 0;
    static Map<Long, ClientInfo> cache = new HashMap<>();


//        // Create the broker and run the test data
//        for (ClientInfo info : clients) {
//            displayProfile(info);
//
//            // Retrieve quotations from the broker and display them...
//            for (Quotation quotation : brokerService.getQuotations(info)) {
//                displayQuotation(quotation);
//            }
//
//            // Print a couple of lines between each client
//            System.out.println("\n");
//        }

    /**
     * This is the starting point for the application. Here, we must
     * get a reference to the Broker Service and then invoke the
     * getQuotations() method on that service.
     * <p>
     * Finally, you should print out all quotations returned
     * by the service.
     *
     * @param args
     */
    public static void main(String[] args) throws JMSException {


        String host = "localhost";
        if (args.length > 0) {
            host = args[0];
        }

        ConnectionFactory factory =
                new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
        Connection connection = factory.createConnection();
        connection.setClientID("client");
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Queue queue = session.createQueue("QUOTATIONS");
        Topic topic = session.createTopic("APPLICATIONS");
        MessageProducer producer = session.createProducer(topic);
        MessageConsumer consumer = session.createConsumer(queue);

        // Create the broker and run the test data
        for (int i=1;i< clients.length;i++) {
            QuotationRequestMessage quotationRequest =
                    new QuotationRequestMessage(SEED_ID++, clients[i]);
            Message request = session.createObjectMessage(quotationRequest);
            cache.put(quotationRequest.id, quotationRequest.info);
            producer.send(request);
        }
        connection.start();
        while (true) {
            Message message = consumer.receive();
            if (message instanceof ObjectMessage) {
                Object content = ((ObjectMessage) message).getObject();
                if (content instanceof QuotationResponseMessage) {
                    QuotationResponseMessage response = (QuotationResponseMessage) content;
                    ClientInfo info = cache.get(response.id);
                    displayProfile(info);
                    displayQuotation(response.quotation);
                    System.out.println("\n");
                }
                message.acknowledge();
            } else {
                System.out.println("Unknown message type: " +
                        message.getClass().getCanonicalName());

            }
        }
    }

    /**
     * Display the client info nicely.
     *
     * @param info
     */
    public static void displayProfile(ClientInfo info) {
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.name) +
                        " | Gender: " + String.format("%1$-27s", (info.gender == ClientInfo.MALE ? "Male" : "Female")) +
                        " | Age: " + String.format("%1$-30s", info.age) + " |");
        System.out.println(
                "| License Number: " + String.format("%1$-19s", info.licenseNumber) +
                        " | No Claims: " + String.format("%1$-24s", info.noClaims + " years") +
                        " | Penalty Points: " + String.format("%1$-19s", info.points) + " |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    /**
     * Display a quotation nicely - note that the assumption is that the quotation will follow
     * immediately after the profile (so the top of the quotation box is missing).
     *
     * @param quotation
     */
    public static void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.company) +
                        " | Reference: " + String.format("%1$-24s", quotation.reference) +
                        " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price)) + " |");
        System.out.println("|=================================================================================================================|");
    }
}


