package client;


import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import service.core.ClientApplication;
import service.core.ClientInfo;
import service.core.Quotation;

import java.text.NumberFormat;


public class Client {


    public static final ClientInfo[] clients = {
            new ClientInfo("Niki Collier", ClientInfo.getFEMALE(), 43, 0, 5, "PQR254/1"),
            new ClientInfo("Old Geeza", ClientInfo.getMALE(), 65, 0, 2, "ABC123/4"),
            new ClientInfo("Hannah Montana", ClientInfo.getFEMALE(), 16, 10, 0, "HMA304/9"),
            new ClientInfo("Rem Collier", ClientInfo.getMALE(), 44, 5, 3, "COL123/3"),
            new ClientInfo("Jim Quinn", ClientInfo.getMALE(), 55, 4, 7, "QUN987/4"),
            new ClientInfo("Donald Duck", ClientInfo.getMALE(), 35, 5, 2, "XYZ567/9")};

//        Quotation quotation =
//                restTemplate.postForObject("http://localhost:8080/quotations",
//                        requests, Quotation.class);
//
//        assert quotation != null;

    public static void main(String[] args) {
        for (int i = 0; i < clients.length; i++) { //loop quotations 4e
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<ClientInfo> requests = new HttpEntity<>(clients[i]);
            ClientApplication clientApplication = restTemplate.postForObject("http://localhost:8080/application", requests, ClientApplication.class);
            displayProfile(clients[i]);
            for (Quotation quotation : clientApplication.getQuotationList())
                displayQuotation(quotation);

        }

    }

    public static void displayProfile(ClientInfo info) {
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("| Name: " + String.format("%1$-29s", info.getName()) + " | Gender: " + String.format("%1$-27s", (info.getGender() == ClientInfo.getMALE() ? "Male" : "Female")) + " | Age: " + String.format("%1$-30s", info.getAge()) + " |");
        System.out.println("| License Number: " + String.format("%1$-19s", info.getLicenseNumber()) + " | No Claims: " + String.format("%1$-24s", info.getNoClaims() + " years") + " | Penalty Points: " + String.format("%1$-19s", info.getPoints()) + " |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayQuotation(Quotation quotation) {
        System.out.println("| Company: " + String.format("%1$-26s", quotation.getCompany()) + " | Reference: " + String.format("%1$-24s", quotation.getReference()) + " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.getPrice())) + " |");
        System.out.println("|=================================================================================================================|");
    }
}