package net.bodkasoft.som.test;

public class TestResult {
    private final long consistTime;
    private final long concurrTime;
    private final double speedUp;

    public TestResult(final long consistTime, final long concurrTime, final double speedUp) {
        this.consistTime = consistTime;
        this.concurrTime = concurrTime;
        this.speedUp = speedUp;
    }

    public long getConsistTime() {
        return consistTime;
    }

    public long getConcurrTime() {
        return concurrTime;
    }

    public double getSpeedUp() {
        return speedUp;
    }
}
