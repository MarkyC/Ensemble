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

    private final int responseTime;
    private final String resource;
    private final int requests;

    public Workload(String resource, int responseTime) {
        this(resource, 1, responseTime);
    }

    public Workload(String resource, int requests, int responseTime) {
        this.resource = resource;
        this.requests = requests;
        this.responseTime = responseTime;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public String getResource() {
        return resource;
    }

    public int getRequests() {
        return requests;
    }

    /*public double getRequestsPerSecond(Date start, Date end) {
        double time = end.getTime() - start.getTime(); // in seconds; Not pretty, but works
        return ((double) getRequests()/time);
    }*/

    @Override
    public String toString() {
        return "Workload{" +
                "responseTime=" + responseTime +
                ", resource='" + resource + '\'' +
                ", requests=" + requests +
                '}';
    }
}
