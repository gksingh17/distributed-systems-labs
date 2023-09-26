import service.auldfellas.AFQService;
import service.broker.LocalBrokerService;
import service.core.*;
import service.dodgydrivers.DDQService;
import service.girlpower.GPQService;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;

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
    public static void main(String[] args) {
        Registry registry = null;
        try {
            if (args.length == 0) {
                registry = LocateRegistry.createRegistry(1099);
            } else {
                String host = args[0];
                registry = LocateRegistry.getRegistry(host, 1099);
            }
        } catch (RemoteException e) {
            System.out.println("Failed to initialize the registry instance, the program can't continue.");
            throw new RuntimeException(e);
        }

        BrokerService brokerService = null;

        try {
            BrokerService localBrokerService = new LocalBrokerService();
            brokerService = (BrokerService) UnicastRemoteObject.exportObject(localBrokerService, 0);
            registry.bind(Constants.BROKER_SERVICE, brokerService);
            System.out.println("bye");


            // Bind AFQService
            QuotationService afqService = new AFQService();
            QuotationService quotationService1 = (QuotationService) UnicastRemoteObject.exportObject(afqService, 0);
            registry.bind(Constants.AULD_FELLAS_SERVICE, quotationService1);

            // Bind DDQService
            QuotationService ddqService = new DDQService();
            QuotationService quotationService2 = (QuotationService) UnicastRemoteObject.exportObject(ddqService, 0);
            registry.bind(Constants.DODGY_DRIVERS_SERVICE, quotationService2);

            // Bind GPQService
            QuotationService gpqService = new GPQService();
            QuotationService quotationService3 = (QuotationService) UnicastRemoteObject.exportObject(gpqService, 0);
            registry.bind(Constants.GIRL_POWER_SERVICE, quotationService3);
        } catch (RemoteException e) {
            System.out.println("Could not register one or more services, the program can't continue.");
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            System.out.println("Service already bound, ignoring.");
        }


        try {
            brokerService = (BrokerService) registry.lookup(Constants.BROKER_SERVICE);
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Could not lookup the Broker service, the program can't continue.");
            throw new RuntimeException(e);
        }

        // Create the broker and run the test data
        for (ClientInfo info : clients) {
            displayProfile(info);

            // Retrieve quotations from the broker and display them...
            try {
                for (Quotation quotation : brokerService.getQuotations(info)) {
                    displayQuotation(quotation);
                }
            } catch (RemoteException e) {
                System.out.println("Could not get Quotations.");
                System.out.println(e.getMessage());
            }

            // Print a couple of lines between each client
            System.out.println("\n");
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