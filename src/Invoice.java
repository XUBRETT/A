import java.sql.Date;


public class Invoice {
    private int id;
    private Client client;
    private Date date;
    private double totalAmount;


    public Invoice(int id, Client client, Date date, double totalAmount) {
        this.id = id;
        this.client = client;
        this.date = date;
        this.totalAmount = totalAmount;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public Client getClient() {
        return client;
    }


    public void setClient(Client client) {
        this.client = client;
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public double getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
