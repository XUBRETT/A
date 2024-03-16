public class Service {
    private int id;
    private String name;
    private double ratePerHour;


    public Service() {
    }


    public Service(int id, String name, double ratePerHour) {
        this.id = id;
        this.name = name;
        this.ratePerHour = ratePerHour;
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


    public double getRatePerHour() {
        return ratePerHour;
    }


    public void setRatePerHour(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }


    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ratePerHour=" + ratePerHour +
                '}';
    }
}
