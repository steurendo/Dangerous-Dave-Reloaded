package scenarios;

import entities.Player;
import game.Model;
import utils.Textures;

public class ScenarioTransition extends Scenario {
    private Player player;

    public ScenarioTransition(Model model, Textures textures) {
        this.model = model;
        this.textures = textures;
    }

    @Override
    public void commands(double deltaT) {
    }

    @Override
    public void collisions(double deltaT) {
    }

    @Override
    public void update(double deltaT) {
    }

    @Override
    public void render(double deltaT) {
    }
}