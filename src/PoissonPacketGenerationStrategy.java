public class PoissonPacketGenerationStrategy implements PacketGenerationStrategy {
    private double mean;

    public PoissonPacketGenerationStrategy(double mean) {
        this.mean = mean;
    }

    @Override
    public double getTimeToNextArrival() {
        return RandomGenerator.getPoissonRandom(mean);
    }
}
