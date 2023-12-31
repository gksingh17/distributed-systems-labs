package service.core;

import java.io.Serializable;

public class QuotationRequestMessage implements Serializable {
    public long id;
    public ClientInfo info;

    public QuotationRequestMessage(long id, ClientInfo info) {
        this.id = id;
        this.info = info;
    }
}
