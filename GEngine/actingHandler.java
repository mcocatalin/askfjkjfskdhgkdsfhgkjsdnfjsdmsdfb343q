package GEngine;

/**
 * Created by Catalin on 3/19/2017.
 */
public class actingHandler {
    public int index;
    public String type = "None";
    public String numeArhiva;
    public String numeFisier;
    public String numeObiect;

    public float xTranslation;
    public float yTranslation;
    public float zTranslation;

    public float xScale;
    public float yScale;
    public float zScale;

    public int xRotation;
    public int yRotation;
    public int zRotation;

    public float masa;
    //Properties

    public actingHandler(String type, String numeArhiva, String numeFisier, String numeObiect,
                         float xTranslation, float yTranslation, float zTranslation,
                         float xScale, float yScale, float zScale,
                         int xRotation, int yRotation, int zRotation,
                         float masa){
        this.type = type;
        this.numeArhiva = numeArhiva;
        this.numeFisier = numeFisier;
        this.numeObiect = numeObiect;
        this.xTranslation = xTranslation;
        this.yTranslation = yTranslation;
        this.zTranslation = zTranslation;
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
        this.xRotation = xRotation;
        this.yRotation = yRotation;
        this.zRotation = zRotation;
        this.masa = masa;
    }
}
