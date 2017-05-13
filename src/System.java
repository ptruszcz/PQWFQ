public class System {
    private double lambda;
    private double mi;

    private double totalServiceTime;
    private int numberOfArrivals;
    private double sumOfArrivalIntervals;

    private int numberOfDelays;
    private double totalDelay;
    private double queueTime;
    private double serverBusyTime;

    private int peakQueueSize;

    private EventList eventList;
    private Server server;

    System() {
        lambda = 1.0;
        mi = 10;
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
        initialize();
    }

    System(double lambda, double mi) {
        this.lambda = lambda;
        this.mi = mi;
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
        initialize();
    }

    private void initialize() {
        scheduleNextArrival();
    }

    private void addEvent(Event event) {
        eventList.addEvent(event);
    }

    public void processNextEvent() {
        Event event = eventList.popNextEvent();

        double previousTime = Clock.getCurrentTime();
        Clock.setTime(event.getTime());
        double timeDelta = Clock.getCurrentTime() - previousTime;

        updateStatistics(timeDelta);

        if (event.getEventType() == EventType.ARRIVAL)
            processArrival();
        else
            processDeparture();
    }

    private void updateStatistics(double timeDelta) {
        if(isServerBusy())
            serverBusyTime += timeDelta;
        queueTime += server.getQueueSize() * timeDelta;

        if (server.getQueueSize() > peakQueueSize)
            peakQueueSize = server.getQueueSize();
    }

    private void processArrival() {
        numberOfArrivals++;
        scheduleNextArrival();
        if(server.isBusy()) {
            server.addClient(Clock.getCurrentTime());
        }
        else {
            server.setIsBusy(true);
            addDelayToStatistics(0.0);
            scheduleNextDeparture();
        }
    }

    private void processDeparture() {
        if(server.isQueueEmpty()) {
            server.setIsBusy(false);
        } else {
            double clientArrivalTime = server.handleNextClient();
            double waitingTime = Clock.getCurrentTime() - clientArrivalTime;
            addDelayToStatistics(waitingTime);
            scheduleNextDeparture();
        }
    }

    private void scheduleNextArrival() {
        double timeToNextArrival = RandomGenerator.getExpRandom(lambda);
        sumOfArrivalIntervals += timeToNextArrival;
        double nextArrivalTime = Clock.getCurrentTime() + timeToNextArrival;
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime));
    }

    private void scheduleNextDeparture(){
        double serviceTime = RandomGenerator.getExpRandom(mi);
        totalServiceTime += serviceTime;
        double nextDepartureTime = Clock.getCurrentTime() + serviceTime;
        addEvent(new Event(EventType.DEPARTURE, nextDepartureTime));
    }

    private void addDelayToStatistics(double delay) {
        totalDelay += delay;
        numberOfDelays++;
    }

    public boolean isEventListEmpty() {
        return eventList.isEmpty();
    }

    public boolean isServerBusy() {
        return server.isBusy();
    }

    public int getNumberOfArrivals() {
        return numberOfArrivals;
    }

    public void resetAllStatistics() {
        totalServiceTime = 0.0;
        numberOfArrivals = 0;
        numberOfDelays = 0;
        totalDelay = 0.0;
        queueTime = 0.0;
        serverBusyTime = 0.0;
        peakQueueSize = 0;
    }

    public void displayAllStatistics() {
        double totalSimTime = Clock.getCurrentTime();

        double expectedRho = lambda/mi;
        double expectedW = (expectedRho/mi)/(1-expectedRho);

        double dn = totalDelay/numberOfDelays;
        double qn = queueTime/totalSimTime;
        double un = serverBusyTime/totalSimTime;

        double avgServiceTime = totalServiceTime/numberOfDelays;
        double avgArrivalInterval = sumOfArrivalIntervals/numberOfArrivals;

        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("------------  SIMULATION RESULTS  -------------");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("lambda:\t" + lambda + "\nmi:\t" + mi);
        java.lang.System.out.println("Total simulation time: " + totalSimTime);
        java.lang.System.out.println("Number of arrivals: " + numberOfArrivals);
        java.lang.System.out.println("Number of delays/serviced customers: " + numberOfDelays);
        java.lang.System.out.println("Total delay: " + totalDelay);
        java.lang.System.out.println("Q(t): " + queueTime);
        java.lang.System.out.println("B(t): " + serverBusyTime);
        java.lang.System.out.println("Peak queue size: " + peakQueueSize);
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Rho - average system load\nW - average waiting time");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Expected Rho:\t" + expectedRho);
        java.lang.System.out.println("Expected W:\t\t" + expectedW);
        java.lang.System.out.println("d(n):\t" + dn + "  (avg waiting time)");
        java.lang.System.out.println("q(n):\t" + qn + "  (avg queue size)");
        java.lang.System.out.println("u(n):\t" + un + "   (avg system load)");
        java.lang.System.out.println("avgServiceTime:\t\t" + avgServiceTime);
        java.lang.System.out.println("avgArrivalInterval:\t" + avgArrivalInterval);
        java.lang.System.out.println("-----------------------------------------------");
    }

    public void displayTrace() {
        double totalSimTime = Clock.getCurrentTime();
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Current time: " + totalSimTime);
        java.lang.System.out.println("Number of delays: " + numberOfDelays);
        java.lang.System.out.println("Total delay: " + totalDelay);
        java.lang.System.out.println("Q(t): " + queueTime);
        java.lang.System.out.println("B(t): " + serverBusyTime);
        java.lang.System.out.println("Is server busy?: " + isServerBusy());
        java.lang.System.out.println("Clients in queue: " + server.getQueueSize());
        java.lang.System.out.println("Next event: " + eventList.peekNextEvent().getEventType());
        java.lang.System.out.println("-----------------------------------------------");
    }
}
