package outskirts.util;

public class GameTimer {

    public static final long TPS = 60; //ticks pre second, 20 ticks/s
    private static final long TICK_LENGTH = 1000 / TPS; // 1000/20=50(ms)

    private long prevUpdate;

    private long delta;

    private float elapsedTicks;

    public void update() {
        long currentTime = System.currentTimeMillis();

        delta = currentTime - prevUpdate;

        elapsedTicks += Math.min((float)delta / TICK_LENGTH, 10f);

        this.prevUpdate = currentTime;
    }

    public float getDelta() {
        return delta / 1000f;
    }

    public boolean pollFullTick() {
        if (elapsedTicks < 1f) {
            return false;
        } else {
            elapsedTicks -= 1f;
            return true;
        }
    }
}
