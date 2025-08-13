package scenarios;

import game.Model;
import utils.Textures;

public abstract class Scenario {
    protected Textures textures;
    protected Model model;

    public abstract void render();

    public abstract void commands();
}