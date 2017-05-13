import java.util.LinkedList;
import java.util.Queue;

public class Server {
    private boolean isBusy;
    private Queue<Double> queue; //TODO: add more queues later!

    Server() {
        queue = new LinkedList<>();
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void addClient(double arrivalTime) {
        queue.add(arrivalTime);
    }

    public double handleNextClient() {
        return queue.poll();
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public int getQueueSize() {
        return queue.size();
    }
}
