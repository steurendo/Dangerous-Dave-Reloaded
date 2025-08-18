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
    public void commands() {
    }

    @Override
    public void collisions() {
    }

    @Override
    public void update() {
    }

    @Override
    public void render() {
    }
}