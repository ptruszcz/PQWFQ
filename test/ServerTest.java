import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    private Server server;
    private int packetSize;

    @BeforeEach
    void setUp() {
        server = new Server();
    }

    @AfterEach
    void tearDown() {
        server = null;
        Clock.reset();
    }

    @Test
    void addClient() throws InvalidQueueParametersException {
        server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 0.4, packetSize);
        server.addClient(1, new Packet(0.0, 0.0, packetSize));
        assertFalse(server.areAllQueuesEmpty());
    }

    @Test
    void addQueuesWithTheSameId() {
        assertThrows(InvalidQueueParametersException.class, () -> {
            server.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 0.4, packetSize);
            server.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 0.4, packetSize);
        });
    }

    @Test
    void addTwoHighPriorityQueues() {
        assertThrows(InvalidQueueParametersException.class, () -> {
            server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 0.4, packetSize);
            server.addQueue(2, QueuePQWFQ.HIGH_PRIORITY, 0.4, packetSize);
        });
    }

    @Test
    void handleNextClient() throws InvalidQueueParametersException {
        server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 0.4, packetSize);
        server.addClient(1, new Packet(0.0, 0.0, packetSize));
        server.addClient(1, new Packet(1.0, 1.0, packetSize));
        server.addClient(1, new Packet(2.0, 2.0, packetSize));
        server.addClient(1, new Packet(3.0, 3.0, packetSize));
        server.addClient(1, new Packet(4.0, 4.0, packetSize));
        server.addClient(1, new Packet(5.0, 5.0, packetSize));

        assertFalse(server.areAllQueuesEmpty());
        for(int i = 0; i < 6; i++) {
            assertEquals((double)i, server.handleNextClient(1).getArrivalTime());
        }
        assertTrue(server.areAllQueuesEmpty());
    }

    @Test
    void isHighPriorityQueueHandledFirst() throws InvalidQueueParametersException {
        server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1.0, packetSize);
        server.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 1.0, packetSize);
        server.addClient(2, new Packet(0.0, 1.0, packetSize));
        server.addClient(2, new Packet(1.0, 0.0, packetSize));
        server.addClient(2, new Packet(2.0, 2.0, packetSize));
        server.addClient(1, new Packet(3.0, 4.0, packetSize));
        server.addClient(1, new Packet(4.0, 3.0, packetSize));
        server.addClient(1, new Packet(5.0, 5.0, packetSize));

        double[] expectedOrder = new double[] {4.0, 3.0, 5.0, 1.0, 0.0, 2.0};
        for (double anExpectedOrder : expectedOrder) {
            double actual = server.handleNextClient(server.pqwfqDepartureAlgorithm()).getArrivalTime();
            assertEquals(anExpectedOrder, actual);
        }

    }
}