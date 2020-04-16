package eu.ioannidis.speedometer;

public class SpeedConverter {

    // Convert meter/sec to kilometers/hour
    public static int mPerSecToKmPerHr(float meters) {
        return (int)(3.6 * meters);
    }
}
