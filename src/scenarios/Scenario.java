package scenarios;

import game.Model;
import utils.Textures;

public abstract class Scenario {
    protected Textures textures;
    protected Model model;

    public abstract void commands();

    public abstract void collisions();

    public abstract void update();

    public abstract void render();
}