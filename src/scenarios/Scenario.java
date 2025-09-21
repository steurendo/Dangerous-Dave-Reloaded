package scenarios;

import game.Model;
import utils.Textures;

public abstract class Scenario {
    protected Textures textures;
    protected Model model;

    public abstract void commands(double deltaT);

    public abstract void collisions(double deltaT);

    public abstract void update(double deltaT);

    public abstract void render(double deltaT);
}