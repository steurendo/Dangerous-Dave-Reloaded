package entities;

public class PickupEffect {
    private static final double PULSE_TIME = 0.1;
    private static final double PULSE_SCALE = 0.3;
    private static final double SHRINK_TIME = 0.1;

    private final Entity entity;
    private double time;

    public PickupEffect(Entity entity) {
        this.entity = entity;
        time = 0;
    }

    public Entity getEntity() {
        return entity;
    }

    public void update(double seconds) {
        time += seconds;
    }

    public boolean isFinished() {
        return time >= PULSE_TIME + SHRINK_TIME;
    }

    public double getScale() {
        if (time < PULSE_TIME)
            return 1 + PULSE_SCALE * Math.sin(Math.PI * time / PULSE_TIME);
        double shrink = Math.min(1, (time - PULSE_TIME) / SHRINK_TIME);
        return 1 - shrink * shrink;
    }
}
