public class Client {
    private int id;
    private String name;
    private double totalBilled;
    private String email;
    private String phone;
    private String address;


    public Client() {
    }


    public Client(int id, String name, double totalBilled, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.totalBilled = totalBilled;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public double getTotalBilled() {
        return totalBilled;
    }


     public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }




    public void setTotalBilled(double totalBilled) {
        this.totalBilled = totalBilled;


   
         
       
    }
}


