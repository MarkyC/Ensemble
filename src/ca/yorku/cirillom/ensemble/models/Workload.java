package ca.yorku.cirillom.ensemble.models;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 4/1/2014 10:22 PM.
 * <p/>
 * Description
 * <p/>
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 */
public class Workload {

    private String resource;
    private int requests;

    public Workload(String resource, int requests) {
        this.resource = resource;
        this.requests = requests;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    @Override
    public String toString() {
        return "Workload{" +
                "resource='" + resource + '\'' +
                ", requests=" + requests +
                '}';
    }
}
