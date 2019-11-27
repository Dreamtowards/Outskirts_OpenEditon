package outskirts.client.material;

public class Material {

    private Model model;
    private Texture texture;


    public Model getModel() {
        return model;
    }

    public Material setModel(Model model) {
        this.model = model;
        return this;
    }

    public Texture getTexture() {
        return texture;
    }

    public Material setTexture(Texture texture) {
        this.texture = texture;
        return this;
    }
}
