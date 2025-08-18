package scenarios;

import game.CollisionType;
import game.Model;
import utils.Textures;

public abstract class Scenario {
    protected Textures textures;
    protected Model model;

    public abstract void commands();

    public abstract void collisions(CollisionType type);

    public abstract void update();

    public abstract void render();
}