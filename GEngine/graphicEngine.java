package GEngine;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionTrack;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.*;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.controls.slider.builder.SliderBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import jme3tools.optimize.GeometryBatchFactory;
import org.bushe.swing.event.EventTopicSubscriber;
import sun.plugin2.util.ColorUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static jade.tools.sniffer.Message.offset;

public class graphicEngine extends SimpleApplication implements ActionListener {

    public BitmapText hudText;
    public CharacterControl player;
    public BulletAppState bulletAppState;
    public Nifty nifty;
    public boolean left = false, right = false, up = false, down = false, camera = false, tp = false;
    public Vector3f camDir = new Vector3f();
    public Vector3f camLeft = new Vector3f();
    public Vector3f walkDirection = new Vector3f();
    public RigidBodyControl traffic_light;

    // Vehicle members
    public VehicleControl vehicle;
    public VehicleWheel fr, fl, br, bl;
    public Node node_fr, node_fl, node_br, node_bl;
    public float wheelRadius;
    public float steeringValue = 0;
    public float accelerationValue = 0;
    public float decelerationValue = 0;
    public Node carNode;
    public Node trafficLight;

    public LinkedList<BoundingSphere> bounds = new LinkedList<BoundingSphere>();

    public LinkedList<Ray> rays = new LinkedList<Ray>();

    public static int noOfCars = 0;

    public Vector3f trafficLightLocations[] =
            {
                    new Vector3f(33f, 18.257574f, -97f),
                    new Vector3f(50f, 18.257574f, -97f),
                    new Vector3f(50f, 18.257574f, -114f),
                    new Vector3f(33f, 18.257574f, -114f),
                    new Vector3f(33f, 18.257574f, -97f),
            };


    // Engine const variables
    final String[] modelPaths = {"Models/Ferrari/Car.scene", "src/assets/Models/Ford.zip"};

    final Vector3f[] valideLocations = {
            new Vector3f(46, 18.2f, 97),
            new Vector3f(37, 18.2f, -119),
            new Vector3f(46, 18.2f, -119),
            new Vector3f(-74, 18.2f, -122),
            new Vector3f(186, 18.2f, 79),
            new Vector3f(-67, 18.2f, 100),
            new Vector3f(46.1024f, 18.30425f, -58.33871f),
            new Vector3f(46.557808f, 19.38934f, -57.164307f),
            new Vector3f(46.557808f, 19.38934f, -108.164307f),
            new Vector3f(140.557808f, 19.38934f, -108.164307f),
            new Vector3f(38f, 21f, -66f)
    };
    final Matrix3f[] valideRotations = {
            new Matrix3f(0, 0, -1, 0, 1, 0, -1, 0, 0),
            new Matrix3f(0, 0, -1, 0, 1, 0, 1, 0, 0),
            new Matrix3f(1, 0, 0, 0, 0, 1, 0, -1, 0),
            new Matrix3f(0, 1, 0, -1, 0, 0, 0, 0, 1)
    };

    final Vector3f[] trafficLightsLocation = {
            new Vector3f(0, 10, -64)
    };


    // Communication members
    public static List<requestHandler> request = new ArrayList<requestHandler>();

    // Test Motion Path
    private MotionPath path;
    private MotionTrack motionControl;
    private boolean runCars;

    private Vector3f vector1 = new Vector3f();
    private Vector3f vector2 = new Vector3f();
    private Vector3f vector3 = new Vector3f();//valideLocations[3];
    private Vector3f vector4 = new Vector3f();//valideLocations[4];

    static final Quaternion ROTATE_RIGHT = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
    private Plane plane = new Plane();

    private float speed = 800f;

    Spatial map;

    public static void main(String[] args) {

    }

    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        load_interfata();

        setMap();
        setHudText();
        setSun();
        load_sky();
        buildVehicle(modelPaths[0], valideLocations[5], valideRotations[3]);
        Load_traffic_lights();

        flyCam.setEnabled(true);
        setDisplayFps(false);
        setDisplayStatView(false);



        cameraSetup();
        load_player();

        //SetTrafficLights(1);

        setUpKeys();



    }

    private void load_sky(){
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false);
        sky.setLocalScale(1000);
        rootNode.attachChild(sky);
        rootNode.setShadowMode(RenderQueue.ShadowMode.Off);
    }

    private void Load_traffic_lights() {

        assetManager.registerLocator("src\\assets\\Models\\semafor.zip", ZipLocator.class);
        trafficLight = (Node) assetManager.loadModel("semafor_v7.mesh.j3o");
        trafficLight.setLocalTranslation(trafficLightLocations[0]);

        //Geometry chasis = findGeom(trafficLight, "trafficLight");
       // BoundingBox box = (BoundingBox) chasis.getModelBound();
//Create a hull collision shape for the chassis
        //CollisionShape trafficLightHull = CollisionShapeFactory.createDynamicMeshShape(chasis); !!! Cannot find geometry - LOSS!!!S
        traffic_light = new RigidBodyControl( 0);

        trafficLight.addControl(traffic_light);
        traffic_light.setPhysicsLocation(trafficLightLocations[0]); //new Vector3f(199,10,-64));
        traffic_light.setPhysicsRotation(valideRotations[0]);// new Matrix3f(0,0,1,0,1,0,-1,0,0)); // rotate with 270 degrees on Y

        getPhysicsSpace().add(traffic_light);

        rootNode.attachChild(trafficLight);


    }

    public void CarMoveAt(Vector3f targetLocation) {
        vehicle.getPhysicsLocation(vector1);
        vector2.set(targetLocation);
        vector2.subtractLocal(vector1);
        float distance = vector2.length();
        float checkRadius = 4;
        if (distance <= checkRadius) {
            //moving = false;
            vehicle.accelerate(0);
            vehicle.brake(10);
        } else {
            vector2.set(targetLocation);
            vector2.normalizeLocal();
            vehicle.getForwardVector(vector3);
            vector4.set(vector3);
            ROTATE_RIGHT.multLocal(vector4);
            plane.setOriginNormal(map.getWorldTranslation(), vector4);

            float dot = 1 - vector3.dot(vector2);
            float angle = vector3.angleBetween(vector2);

            float anglemult = 1;//FastMath.PI / 4.0f;
            float speedmult = 0.3f;//0.3f;

            if (angle > FastMath.QUARTER_PI) {
                angle = FastMath.QUARTER_PI;
            }
            //left or right
            if (plane.whichSide(targetLocation) == Plane.Side.Negative) {
                anglemult *= -1;
            }
            //backwards
            if (dot > 1) {
                speedmult *= -1;
                anglemult *= -1;
            }
            vehicle.steer(angle * anglemult);
            vehicle.accelerate(speed * speedmult);
            vehicle.brake(0);
        }
    }

    public void CarGoTo(Vector3f destination) {

        vehicle.getPhysicsLocation(vector1);
        vehicle.getForwardVector(vector3);
        vector2.set(destination);
        float distance = vector1.distance(destination);
        float checkRadius = 10;
//        if (distance <= checkRadius) {
//            //moving = false;
//            vehicle.accelerate(0);
//            vehicle.brake(100);
//        } else {
            System.out.println("Rotatie :" + vehicle.getPhysicsRotationMatrix());
            System.out.println("Locatie :" + vehicle.getPhysicsLocation());
            plane.setOriginNormal(destination, vector1);
            // plane.setOriginNormal(map.getWorldTranslation(), vector4);
            float dot = 1 - vector3.dot(vector2);
            float angle = vector3.angleBetween(destination);
            if (angle > FastMath.QUARTER_PI) {
                angle = FastMath.QUARTER_PI;
            }
            float anglemult = 1;//FastMath.PI / 4.0f;
            float speedmult = 0.3f;//0.3f;
            ROTATE_RIGHT.multLocal(vector3);
            if (plane.whichSide(vector3) == Plane.Side.Negative) {
                anglemult *= -1;
            }

            if (dot > 1) {
                speedmult *= -1;
                anglemult *= -1;
            }

            vehicle.steer(angle * anglemult);
            vehicle.accelerate(speed * speedmult);
            vehicle.brake(0);

        //}
    }

    @Override
    public void simpleUpdate(float tpf) {

        hudText.setText("GPS: " + (int) cam.getLocation().getX() + "x" + " " + (int) cam.getLocation().getY() + "y" + " " + (int) cam.getLocation().getZ() + "z");

        if (!camera) {
            camDir.set(cam.getDirection()).multLocal(0.6f);
            camLeft.set(cam.getLeft()).multLocal(0.4f);
            walkDirection.set(0, 0, 0);
            if (left) {
                walkDirection.addLocal(camLeft);
            }
            if (right) {
                walkDirection.addLocal(camLeft.negate());
            }
            if (up) {
                walkDirection.addLocal(camDir);
            }
            if (down) {
                walkDirection.addLocal(camDir.negate());
            }
            player.setWalkDirection(walkDirection);
            cam.setLocation(new Vector3f(player.getPhysicsLocation().getX(), player.getPhysicsLocation().getY() - 4, player.getPhysicsLocation().getZ()));
        }

        if(!request.isEmpty()){
            requestHandler x = request.get(0);
            if(x.type == "vehicleMovement"){
                vehicle.accelerate(40);
            }
        }


        Vector3f destination = valideLocations[8];
        //CarGoTo(destination);
        //CarMoveAt(destination);

    }

    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler1 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            if (runCars) {
                accelerationValue = -1;
            } else {
                accelerationValue = 0;
            }
        }
    };

    private void load_interfata() {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        int h = settings.getHeight();
        int w = settings.getWidth();
        int panel1_w = (w / 2) - 300;

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        nifty.addScreen("test", new ScreenBuilder("Hello Nifty Screen") {{
            controller(new DefaultScreenController()); // Screen properties

            // <layer>
            layer(new LayerBuilder("Layer_ID") {{
                childLayoutHorizontal(); // layer properties, add more...

                width(w + "px");
                height("300px");

//                panel(new PanelBuilder("gol") {{
//                    childLayoutVertical(); // panel properties, add more...
//                    width("205px");
//                    height("400px");
//                    alignLeft();
//                    valignBottom();
//                    style("nifty-panel-no-shadow");
//
//                    control(new ButtonBuilder("info", "Informatii") {{
//                        height("40px");
//                        width("100%");
//                        focusable(false);
//                    }});
//                }});
//
//                panel(new PanelBuilder("lala") {
//                    {
//                        childLayoutHorizontal(); // panel properties, add more...
//                        width(((w / 2) - 300) + "px");
//                        style("nifty-panel-no-shadow");
//                        height("200px");
//                        alignCenter();
//                        valignBottom();
//
//                        panel(new PanelBuilder("manual") {
//                            {
//                                childLayoutVertical(); // panel properties, add more...
//                                if (w > 1366)
//                                    width("200px");
//                                else
//                                    width("50%");
//                                height("100%");
//                                alignCenter();
//                                valignBottom();
//
//                                panel(new PanelBuilder("manual_check") {
//                                    {
//                                        childLayoutHorizontal();
//                                        width("100%");
//                                        height("14%");
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("5%");
//                                        }});
//
//                                        control(new ButtonBuilder("manual", "Comanda Manuala") {{
//
//                                            alignLeft();
//                                            valignCenter();
//                                            height("80%");
//                                            width("60%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("10%");
//                                        }});
//
//                                        control(new CheckboxBuilder("manual_activated") {{
//                                            alignRight();
//                                            valignCenter();
//                                            this.focusable(false);
//                                            width("20px");
//                                            height("20px");
//                                        }});
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("5%");
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("Electricitate") {
//                                    {
//                                        childLayoutHorizontal();
//                                        width("100%");
//                                        height("14%");
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("5%");
//                                        }});
//
//                                        control(new ButtonBuilder("manual1", "Electricitate") {{
//
//                                            alignLeft();
//                                            valignCenter();
//                                            height("80%");
//                                            width("60%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("10%");
//                                        }});
//
//                                        control(new CheckboxBuilder("electricitate_activated") {{
//                                            alignRight();
//                                            valignCenter();
//                                            this.focusable(false);
//                                            this.checked(true);
//                                            width("20px");
//                                            height("20px");
//                                        }});
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("5%");
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("lumini_urgenta") {
//                                    {
//                                        childLayoutHorizontal();
//                                        width("100%");
//                                        height("14%");
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("5%");
//                                        }});
//
//                                        control(new ButtonBuilder("manual2", "Lumini urgenta") {{
//
//                                            alignLeft();
//                                            valignCenter();
//                                            height("80%");
//                                            width("60%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("10%");
//                                        }});
//
//                                        control(new CheckboxBuilder("lumini_urgenta_activated") {{
//                                            alignRight();
//                                            valignCenter();
//                                            this.focusable(false);
//                                            width("20px");
//                                            height("20px");
//                                        }});
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("5%");
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("sprinklers") {
//                                    {
//                                        childLayoutHorizontal();
//                                        width("100%");
//                                        height("14%");
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("5%");
//                                        }});
//
//                                        control(new ButtonBuilder("manual3", "Sprinklere") {{
//
//                                            alignLeft();
//                                            valignCenter();
//                                            height("80%");
//                                            width("60%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("10%");
//                                        }});
//
//                                        control(new CheckboxBuilder("sprinklers_activated") {{
//                                            alignRight();
//                                            valignCenter();
//                                            this.focusable(false);
//                                            width("20px");
//                                            height("20px");
//                                        }});
//
//                                        panel(new PanelBuilder("goll") {{
//                                            width("5%");
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("umiditate") {
//                                    {
//                                        childLayoutVertical();
//                                        width("100%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual4", "Comanda umidificator") {{
//                                            alignLeft();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("comanda_umidificator", false) {{
//                                            alignLeft();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(5f);
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("ventilatie") {
//                                    {
//                                        childLayoutVertical();
//                                        width("100%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual5", "Comanda ventilatie") {{
//                                            alignLeft();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("comanda_ventilatie", false) {{
//                                            alignLeft();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(5f);
//                                        }});
//                                    }
//                                });
//
//                            }
//                        });
//
//                        if (w > 1366) {
//                            panel(new PanelBuilder("lumina") {{
//                                height("100%");
//                                width(panel1_w - 420 + "px");
//                                childLayoutVertical();
//
//                                panel(new PanelBuilder("degeaba") {{
//                                    height("5%");
//                                }});
//
//                                control(new ButtonBuilder("lumina", "Lumina") {{
//                                    alignCenter();
//                                    width("80%");
//                                    focusable(false);
//                                    height("15%");
//                                }});
//
//                                panel(new PanelBuilder("degeaba") {{
//                                    height("5%");
//                                }});
//
//                                control(new SliderBuilder("luminozitate", true) {{
//                                    height("75%");
//                                    buttonStepSize(1f);
//                                    focusable(false);
//                                }});
//                            }});
//                        }
//
//                        panel(new PanelBuilder("manual_2") {
//                            {
//                                childLayoutVertical(); // panel properties, add more...
//                                if (w > 1366)
//                                    width("200px");
//                                else
//                                    width("50%");
//                                height("100%");
//                                alignCenter();
//                                valignBottom();
//
//                                panel(new PanelBuilder("goll") {{
//                                    height("4%");
//                                }});
//
//                                panel(new PanelBuilder("incalzire") {
//                                    {
//                                        childLayoutVertical();
//                                        width("100%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual6", "Comanda incalzire") {{
//                                            alignRight();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("comanda_incalzire", false) {{
//                                            alignRight();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(5f);
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("racire") {
//                                    {
//                                        childLayoutVertical();
//                                        width("100%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual7", "Comanda racire") {{
//                                            alignRight();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("comanda_racire", false) {{
//                                            alignRight();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(5f);
//                                        }});
//                                    }
//                                });
//                            }
//                        });
//                    }
//                });

//                // <panel>
//                panel(new PanelBuilder("Referinte") {{
//                    childLayoutVertical(); // panel properties, add more...
//                    width("200px");
//                    style("nifty-panel-no-shadow");
//                    height("250px");
//                    alignCenter();
//                    valignBottom();
//                    x("400px");
//
//                    // GUI elements
//                    control(new ButtonBuilder("nimic", "Referinte (Control Automat)") {{
//                        alignCenter();
//                        valignCenter();
//                        height("15%");
//                        width("100%");
//                        this.onActiveEffect(new EffectBuilder("nimic"));
//                    }});
//                    panel(new PanelBuilder("degeaba") {{
//                        height("20px");
//                    }});
//
//                    control(new ButtonBuilder("buton1", "Temperatura: ") {{
//                        alignCenter();
//                        valignCenter();
//                        height("12%");
//                        width("100%");
//                        this.focusable(false);
//                    }});
//
//                    control(new SliderBuilder("slider1", false) {{
//                        alignCenter();
//                        valignCenter();
//                        height("15%");
//                        width("100%");
//                        this.buttonStepSize(0.5f);
//                        this.min(10f);
//                        this.max(30f);
//                        this.focusable(false);
//                    }});
//
//                    control(new ButtonBuilder("buton2", "Umiditate: ") {{
//                        alignCenter();
//                        valignBottom();
//                        height("12%");
//                        width("100%");
//                        this.focusable(false);
//                    }});
//
//                    control(new SliderBuilder("slider2", false) {{
//                        alignCenter();
//                        valignBottom();
//                        height("15%");
//                        width("100%");
//                        this.buttonStepSize(1f);
//                        this.min(30f);
//                        this.max(60f);
//                        this.focusable(false);
//                    }});
//
//                    control(new ButtonBuilder("buton3", "CO2: ") {{
//                        alignCenter();
//                        valignTop();
//                        height("12%");
//                        width("100%");
//                        this.focusable(false);
//                    }});
//
//                    control(new SliderBuilder("slider3", false) {{
//                        alignCenter();
//                        valignTop();
//                        height("15%");
//                        width("100%");
//                        this.buttonStepSize(25f);
//                        this.min(300f);
//                        this.max(1000f);
//                        this.focusable(false);
//                    }});
//                    //.. add more GUI elements here
//
//
//                }});
//                // </panel>
//
//                panel(new PanelBuilder("dadas") {
//                    {
//                        childLayoutHorizontal(); // panel properties, add more...
//                        width(((w / 2) - 225) + "px");
//                        style("nifty-panel-no-shadow");
//                        height("200px");
//                        alignLeft();
//                        valignBottom();
//                        panel(new PanelBuilder("setari") {
//                            {
//                                childLayoutVertical(); // panel properties, add more...
//                                width("220px");
//                                alignLeft();
//                                valignBottom();
//
//                                panel(new PanelBuilder("goll") {{
//                                    height("4%");
//                                }});
//
//                                panel(new PanelBuilder("temperatura_ext") {
//                                    {
//                                        childLayoutVertical();
//                                        width("90%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual8", "Temp. exterior") {{
//                                            alignLeft();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("temp_ext", false) {{
//                                            alignLeft();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(0.5f);
//                                            min(-20);
//                                            max(40);
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("umiditate_ext") {
//                                    {
//                                        childLayoutVertical();
//                                        width("90%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual18", "U.R. exterior") {{
//                                            alignLeft();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("umiditate_exterior", false) {{
//                                            alignLeft();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(0.5f);
//                                            min(10);
//                                            max(90);
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("oameni") {
//                                    {
//                                        childLayoutVertical();
//                                        width("90%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual9", "Numar oameni") {{
//                                            alignLeft();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("numar_oameni", false) {{
//                                            alignLeft();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(1f);
//                                            max(50);
//                                        }});
//                                    }
//                                });
//                            }
//                        });
//
//
//                        panel(new PanelBuilder("setari2") {
//                            {
//                                childLayoutVertical(); // panel properties, add more...
//                                width("220px");
//                                alignLeft();
//                                valignBottom();
//
//                                panel(new PanelBuilder("golll") {{
//                                    height("4%");
//                                }});
//
//
//                                panel(new PanelBuilder("Iesire1") {
//                                    {
//                                        childLayoutVertical();
//                                        width("90%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual180", "Nr. oameni sector A") {{
//                                            alignLeft();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("sectorA", false) {{
//                                            alignLeft();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(0.5f);
//                                            min(1);
//                                            max(99);
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("Iesire2") {
//                                    {
//                                        childLayoutVertical();
//                                        width("90%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual9", "Nr. oameni sector B") {{
//                                            alignLeft();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("sectorB", false) {{
//                                            alignLeft();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(1f);
//                                            min(1);
//                                            max(99);
//                                        }});
//                                    }
//                                });
//
//                                panel(new PanelBuilder("Iesire3") {
//                                    {
//                                        childLayoutVertical();
//                                        width("90%");
//                                        height("25%");
//
//                                        control(new ButtonBuilder("manual9", "Nr. oameni sector C") {{
//                                            alignLeft();
//                                            height("40%");
//                                            width("90%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//
//                                        control(new SliderBuilder("sectorC", false) {{
//                                            alignLeft();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("30%");
//                                            buttonStepSize(1f);
//                                            min(1);
//                                            max(99);
//                                        }});
//                                    }
//                                });
//                            }
//                        });
//                    }
//                });

                panel(new PanelBuilder("onlines") {
                    {
                        childLayoutVertical(); // panel properties, add more...
                        width("120px");
                        style("nifty-panel-no-shadow");
                        height("400px");
                        alignCenter();
                        valignBottom();
                        control(new ButtonBuilder("Online", "Online") {{
                            width("100%");
                            height("40px");
                            focusable(false);
                        }});

                        panel(new PanelBuilder("Iesire3") {
                            {
                                childLayoutVertical();
                                width("90%");
                                height("25%");

                                control(new ButtonBuilder("output", "Nr. oameni sector C") {{
                                    alignLeft();
                                    height("40%");
                                    width("90%");
                                    this.onActiveEffect(new EffectBuilder("nimic"));
                                    this.focusable(false);
                                }});


                                control(new SliderBuilder("sectorC", false) {{
                                    alignLeft();
                                    this.focusable(false);
                                    width("90%");
                                    height("30%");
                                    buttonStepSize(1f);
                                    min(1);
                                    max(99);
                                }});
                            }
                        });


                    }
                });
            }});
            // </layer>

        }}.build(nifty));

        nifty.subscribe(nifty.getCurrentScreen(), "manual_activated", CheckBoxStateChangedEvent.class, eventHandler11);
        nifty.subscribe(nifty.getCurrentScreen(), "electricitate_activated", CheckBoxStateChangedEvent.class, eventHandler12);
        nifty.subscribe(nifty.getCurrentScreen(), "lumini_urgenta_activated", CheckBoxStateChangedEvent.class, eventHandler13);
        nifty.subscribe(nifty.getCurrentScreen(), "sprinklers_activated", CheckBoxStateChangedEvent.class, eventHandler14);
        nifty.subscribe(nifty.getCurrentScreen(), "sectorA", SliderChangedEvent.class, eventHandler15);
        nifty.subscribe(nifty.getCurrentScreen(), "sectorB", SliderChangedEvent.class, eventHandler16);
        nifty.subscribe(nifty.getCurrentScreen(), "sectorC", SliderChangedEvent.class, eventHandler17);
        nifty.subscribe(nifty.getCurrentScreen(), "luminozitate", SliderChangedEvent.class, eventHandler18);

        nifty.subscribe(nifty.getCurrentScreen(), "output", SliderChangedEvent.class, eventHandler28);

        nifty.gotoScreen("test");

    }

    EventTopicSubscriber<SliderChangedEvent> eventHandler28 = new EventTopicSubscriber<SliderChangedEvent>() {
        @Override
        public void onEvent(final String topic, final SliderChangedEvent event) {
//            for(int i=0;i<20;i++)
//            {
//                if(light[i]!=null)
//                {
//                    light[i].setColor(ColorRGBA.White.mult(nifty.getCurrentScreen().findNiftyControl("luminozitate", Slider.class).getValue()/50));
//                }
//            }
        }
    };

    EventTopicSubscriber<SliderChangedEvent> eventHandler18 = new EventTopicSubscriber<SliderChangedEvent>() {
        @Override
        public void onEvent(final String topic, final SliderChangedEvent event) {

            System.out.println("Hello World!");
//            for(int i=0;i<20;i++)
//            {
//                if(light[i]!=null)
//                {
//                    light[i].setColor(ColorRGBA.White.mult(nifty.getCurrentScreen().findNiftyControl("luminozitate", Slider.class).getValue()/50));
//                }
//            }
        }
    };

    EventTopicSubscriber<SliderChangedEvent> eventHandler15 = new EventTopicSubscriber<SliderChangedEvent>() {
        @Override
        public void onEvent(final String topic, final SliderChangedEvent event) {
//            if(!environment_hol.alarma_incendiu)
//                nr_oameni_setor_A = (int)nifty.getCurrentScreen().findNiftyControl("sectorA", Slider.class).getValue();
        }
    };

    EventTopicSubscriber<SliderChangedEvent> eventHandler16 = new EventTopicSubscriber<SliderChangedEvent>() {
        @Override
        public void onEvent(final String topic, final SliderChangedEvent event) {
//            if(!environment_hol.alarma_incendiu)
//                nr_oameni_setor_B = (int) nifty.getCurrentScreen().findNiftyControl("sectorB", Slider.class).getValue();
        }
    };

    EventTopicSubscriber<SliderChangedEvent> eventHandler17 = new EventTopicSubscriber<SliderChangedEvent>() {
        @Override
        public void onEvent(final String topic, final SliderChangedEvent event) {
//            if(!environment_hol.alarma_incendiu)
//                nr_oameni_setor_C = (int) nifty.getCurrentScreen().findNiftyControl("sectorC", Slider.class).getValue();
        }
    };

    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler14 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
//            if(sprinklers_activated)
//            {
//                environment.sprinkler=false;
//                sprinklers_activated=false;
//            }
//            else
//            {
//                environment.sprinkler=true;
//                sprinklers_activated=true;
//            }
        }
    };

    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler13 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
//            if(lumini_urgenta_activated)
//            {
//                environment.lumini_urgenta=false;
//                lumini_urgenta_activated=false;
//            }
//            else
//            {
//                environment.lumini_urgenta=true;
//                lumini_urgenta_activated=true;
//            }
        }
    };

    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler12 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
//            if(electricitate_activated)
//            {
//                environment.curent_electric=true;
//                electricitate_activated=false;
//            }
//            else
//            {
//                environment.curent_electric=false;
//                electricitate_activated=true;
//            }
        }
    };

    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler11 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
//            if(!controller.disabled){
//                controller.disabled = true;
//                System.out.println("Controller disabled");
//                nifty.getCurrentScreen().findNiftyControl("comanda_racire", Slider.class).enable();
//                nifty.getCurrentScreen().findNiftyControl("comanda_incalzire", Slider.class).enable();
//                nifty.getCurrentScreen().findNiftyControl("comanda_ventilatie", Slider.class).enable();
//                nifty.getCurrentScreen().findNiftyControl("comanda_umidificator", Slider.class).enable();
//                nifty.getCurrentScreen().findNiftyControl("electricitate_activated", CheckBox.class).enable();
//                nifty.getCurrentScreen().findNiftyControl("lumini_urgenta_activated", CheckBox.class).enable();
//                nifty.getCurrentScreen().findNiftyControl("sprinklers_activated", CheckBox.class).enable();
//            }else
//            {
//                controller.disabled = false;
//                nifty.getCurrentScreen().findNiftyControl("comanda_racire", Slider.class).disable();
//                nifty.getCurrentScreen().findNiftyControl("comanda_incalzire", Slider.class).disable();
//                nifty.getCurrentScreen().findNiftyControl("comanda_ventilatie", Slider.class).disable();
//                nifty.getCurrentScreen().findNiftyControl("comanda_umidificator", Slider.class).disable();
//                nifty.getCurrentScreen().findNiftyControl("electricitate_activated", CheckBox.class).disable();
//                nifty.getCurrentScreen().findNiftyControl("lumini_urgenta_activated", CheckBox.class).disable();
//                nifty.getCurrentScreen().findNiftyControl("sprinklers_activated", CheckBox.class).disable();
//                System.out.println("Controller enabled");
//            }
//        }
        }
    };



        EventTopicSubscriber<SliderChangedEvent> eventHandler2 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
//            referinta_umiditate = nifty.getCurrentScreen().findNiftyControl("slider2", Slider.class).getValue();
//            String value = String.valueOf(referinta_umiditate);
//                nifty.getCurrentScreen().findNiftyControl("buton2", Button.class).setText("Umiditate: " + value + "% UR");
            }
        };

        EventTopicSubscriber<SliderChangedEvent> eventHandler3 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
//            referinta_CO2 = nifty.getCurrentScreen().findNiftyControl("slider3", Slider.class).getValue();
//            String value = String.valueOf(referinta_CO2);
//                nifty.getCurrentScreen().findNiftyControl("buton3", Button.class).setText("CO2: " + value + " PPM");
            }
        };

        EventTopicSubscriber<SliderChangedEvent> eventHandler4 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
//            numar_oameni = nifty.getCurrentScreen().findNiftyControl("numar_oameni", Slider.class).getValue();
//            String value = String.valueOf(numar_oameni);
//                nifty.getCurrentScreen().findNiftyControl("manual9", Button.class).setText("Numar oameni: " + value);
            }
        };

        EventTopicSubscriber<SliderChangedEvent> eventHandler5 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
//            umiditate_exterior = nifty.getCurrentScreen().findNiftyControl("umiditate_exterior", Slider.class).getValue();
//            String value = String.valueOf(umiditate_exterior);
//                nifty.getCurrentScreen().findNiftyControl("manual18", Button.class).setText("U.R. exterior: " + value);
            }
        };

        EventTopicSubscriber<SliderChangedEvent> eventHandler6 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
//            temp_exterior = nifty.getCurrentScreen().findNiftyControl("temp_ext", Slider.class).getValue();
//            String value = String.valueOf(temp_exterior);
//                nifty.getCurrentScreen().findNiftyControl("manual8", Button.class).setText("Temp. exterior: " + value);
            }
        };

        EventTopicSubscriber<SliderChangedEvent> eventHandler7 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
//            comanda_racire = nifty.getCurrentScreen().findNiftyControl("comanda_racire", Slider.class).getValue();
//            String value = String.valueOf(comanda_racire/100);
//                nifty.getCurrentScreen().findNiftyControl("manual7", Button.class).setText("Comanda racire: " + value);
            }
        };

        EventTopicSubscriber<SliderChangedEvent> eventHandler8 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
                accelerationValue = nifty.getCurrentScreen().findNiftyControl("comanda_incalzire", Slider.class).getValue();
                String value = String.valueOf(accelerationValue / 100);
                nifty.getCurrentScreen().findNiftyControl("manual6", Button.class).setText("Comanda incalzire: " + value);
            }
        };

        EventTopicSubscriber<SliderChangedEvent> eventHandler9 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
                accelerationValue = nifty.getCurrentScreen().findNiftyControl("comanda_ventilatie", Slider.class).getValue();
                String value = String.valueOf(accelerationValue / 100);
                nifty.getCurrentScreen().findNiftyControl("manual5", Button.class).setText("Comanda ventilatie: " + value);
            }
        };

        EventTopicSubscriber<SliderChangedEvent> eventHandler10 = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent event) {
                accelerationValue = nifty.getCurrentScreen().findNiftyControl("comanda_umidificator", Slider.class).getValue();
                String value = String.valueOf(accelerationValue / 100);
                nifty.getCurrentScreen().findNiftyControl("manual4", Button.class).setText("Comanda umidificator: " + value);
            }
        };


        public void cameraSetup() {
            cam.setFrustumFar(4000);
            cam.onFrameChange();
            flyCam.setMoveSpeed(10);
        }

        public void setMap() {
            assetManager.registerLocator("src/assets/Models/simpleMap_v7.zip", ZipLocator.class);
            map = assetManager.loadModel("simpleMap_v7.mesh.j3o");
            map.center();
            RigidBodyControl map_PhysX = new RigidBodyControl(0);
            map.addControl(map_PhysX);
            bulletAppState.getPhysicsSpace().add(map_PhysX);
           // map.setLocalRotation();
            rootNode.attachChild(map);
        }

        public void setHudText() {
            BitmapFont myFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            hudText = new BitmapText(myFont, false);
            hudText.setSize(15);
            hudText.setColor(ColorRGBA.Green);           // the text
            hudText.setLocalTranslation(25, offset, 300); // position
            guiNode.attachChild(hudText);
        }

        public void setSun() {

        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.Red);
        lamp_light.setRadius(100);
        lamp_light.setPosition(new Vector3f(-10f, 21, 0));
        rootNode.addLight(lamp_light);

            ColorRGBA sun_central_color = new ColorRGBA();
            sun_central_color.set(255 / 255f, 255 / 255f, 251 / 255f, 0.5f);
            PointLight sun_central = new PointLight();
            sun_central.setColor(sun_central_color);
            sun_central.setRadius(10000f);
            sun_central.setPosition(new Vector3f(0.0f, 400.0f, 0.0f));

            PointLight sun_up = new PointLight();
            sun_central.setColor(sun_central_color);
            sun_central.setRadius(10000f);
            sun_central.setPosition(new Vector3f(0.0f, 1000.0f, 0.0f));

            rootNode.addLight(sun_central);
            rootNode.addLight(sun_up);
        }

        public void load_player() {
            CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 12f, 1);
            player = new CharacterControl(capsuleShape, 2.5f);
            player.setJumpSpeed(20);
            player.setFallSpeed(50);
            player.setGravity(40);
            player.setPhysicsLocation(new Vector3f(0, 100.0f, 0));
            bulletAppState.getPhysicsSpace().add(player);
            BoundingSphere bs = new BoundingSphere(20, player.getPhysicsLocation());

            bounds.add(bs);

            rays.add(new Ray(new Vector3f(), player.getPhysicsLocation()));
            rays.get(noOfCars++).setLimit(20);
        }

        public void SetTrafficLights(int state){
            SpotLight sl = new SpotLight();
            SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, 1000);
            SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager, 1000);

            sl.setColor(ColorRGBA.Red);
            sl.setSpotRange(20);
            sl.setPosition(valideLocations[1]);
            sl.setSpotInnerAngle(10f * FastMath.DEG_TO_RAD);
            sl.setSpotOuterAngle(85f * FastMath.DEG_TO_RAD);
            slsr.setLight(sl);
            slsr.setShadowIntensity(0.8f);
            slsf.setLight(sl);

            rootNode.addLight(sl);
            viewPort.addProcessor(slsr);
            slsf.setEnabled(true);

        }

        private void setUpKeys() {
            inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping("Change_camera", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
            inputManager.addMapping("Teleport", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
            inputManager.addMapping("fire1", new KeyTrigger(KeyInput.KEY_1));
            inputManager.addMapping("fire2", new KeyTrigger(KeyInput.KEY_2));
            inputManager.addMapping("fire3", new KeyTrigger(KeyInput.KEY_3));
            inputManager.addMapping("fire4", new KeyTrigger(KeyInput.KEY_4));
            inputManager.addMapping("fire5", new KeyTrigger(KeyInput.KEY_5));
            inputManager.addMapping("fire6", new KeyTrigger(KeyInput.KEY_6));
            inputManager.addMapping("fire", new KeyTrigger(KeyInput.KEY_H));
            inputManager.addMapping("gui", new KeyTrigger(KeyInput.KEY_F1));
            inputManager.addMapping("reset", new KeyTrigger(KeyInput.KEY_TAB));

            inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));
            inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));
            inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));
            inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));
            inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_I));
            inputManager.addListener(this, "Lefts");
            inputManager.addListener(this, "Rights");
            inputManager.addListener(this, "Ups");
            inputManager.addListener(this, "Downs");
            inputManager.addListener(this, "Space");
            inputManager.addListener(this, "Reset");

            inputManager.addListener(this, "Left");
            inputManager.addListener(this, "Right");
            inputManager.addListener(this, "Up");
            inputManager.addListener(this, "Down");
            inputManager.addListener(this, "Jump");
            inputManager.addListener(this, "Lefts");
            inputManager.addListener(this, "Rights");
            inputManager.addListener(this, "Ups");
            inputManager.addListener(this, "Downs");
            inputManager.addListener(this, "Space");
            inputManager.addListener(this, "Reset");
        }

        public void attachChildNodes(Node node) {
            this.rootNode.attachChild(node);
        }

        public void onAction(String binding, boolean isPressed, float tpf) {


            if (binding.equals("Lefts")) {
                if (isPressed) {
                    steeringValue += .5f;
                } else {
                    steeringValue += -.5f;
                }
                vehicle.steer(steeringValue);
            } else if (binding.equals("Rights")) {
                if (isPressed) {
                    steeringValue += -.5f;
                } else {
                    steeringValue += .5f;
                }
                vehicle.steer(steeringValue);
            } //note that our fancy car actually goes backwards..
            else if (binding.equals("Ups")) {
                if (isPressed) {
                    accelerationValue -= 800;
                } else {
                    accelerationValue += 800;
                }
                vehicle.accelerate(accelerationValue);
                vehicle.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(findGeom(carNode, "Car")));
            } else if (binding.equals("Downs")) {
                if (isPressed) {
                    vehicle.brake(40f);
                } else {
                    vehicle.brake(0f);
                }
            } else if (binding.equals("Reset")) {
                if (isPressed) {
                    System.out.println("Reset");
                    carNode.addControl(vehicle);
                    vehicle.setPhysicsLocation(new Vector3f(0, 100, 0));
                    vehicle.setPhysicsRotation(new Matrix3f());
                    vehicle.setLinearVelocity(Vector3f.ZERO);
                    vehicle.setAngularVelocity(Vector3f.ZERO);
                    vehicle.resetSuspension();
                    getPhysicsSpace().add(vehicle);
                    rootNode.attachChild(carNode);
                } else {
                }
            }

            if (binding.equals("Left")) {
                left = isPressed;
            } else if (binding.equals("Right")) {
                right = isPressed;
            } else if (binding.equals("Up")) {
                up = isPressed;
            } else if (binding.equals("Down")) {
                down = isPressed;
            } else if (binding.equals("Change_camera")) {
                camera = !camera;
            } else if (binding.equals("Teleport")) {
                player.setPhysicsLocation(cam.getLocation());
            } else if (binding.equals("Jump")) {
                if (isPressed) {
                    player.jump();
                }
            }
        }

        private void buildVehicle(String modelPath, Vector3f physLocatcation, Matrix3f rotation) {
            float stiffness = 120.0f;//200=f1 car
            float compValue = 0.2f; //(lower than damp!)
            float dampValue = 0.3f;
            final float mass = 400;

            //Load model and get chassis Geometry
//        if (modelPath.contains(".zip"))
//        {
//            assetManager.registerLocator(modelPath, ZipLocator.class);
//            carNode = (Node)assetManager.loadModel("Fordtest1957.obj");
//        }
//        else
            carNode = (Node) assetManager.loadModel(modelPath);//"Models/Ferrari/Car.scene");
            //carNode.setShadowMode(RenderQueue.ShadowMode.Cast);
            Geometry chasis = findGeom(carNode, "Car");
            BoundingBox box = (BoundingBox) chasis.getModelBound();

            //Create a hull collision shape for the chassis
            CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

            //Create a vehicle control
            vehicle = new VehicleControl(carHull, mass);

            carNode.addControl(vehicle);
            vehicle.setPhysicsLocation(physLocatcation); //new Vector3f(199,10,-64));
            vehicle.setPhysicsRotation(rotation);// new Matrix3f(0,0,1,0,1,0,-1,0,0)); // rotate with 270 degrees on Y

            //Setting default values for wheels
            vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
            vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
            vehicle.setSuspensionStiffness(stiffness);
            vehicle.setMaxSuspensionForce(10000);

            //Create four wheels and add them at their locations
            //note that our fancy car actually goes backwards..
            Vector3f wheelDirection = new Vector3f(0, -1, 0);
            Vector3f wheelAxle = new Vector3f(-1, 0, 0);

            Geometry wheel_fr = findGeom(carNode, "WheelFrontRight");
            wheel_fr.center();
            box = (BoundingBox) wheel_fr.getModelBound();
            wheelRadius = box.getYExtent();
            float back_wheel_h = (wheelRadius * 1.7f) - 1f;
            float front_wheel_h = (wheelRadius * 1.9f) - 1f;
            vehicle.addWheel(wheel_fr.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                    wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

            Geometry wheel_fl = findGeom(carNode, "WheelFrontLeft");
            wheel_fl.center();
            box = (BoundingBox) wheel_fl.getModelBound();
            vehicle.addWheel(wheel_fl.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                    wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

            Geometry wheel_br = findGeom(carNode, "WheelBackRight");
            wheel_br.center();
            box = (BoundingBox) wheel_br.getModelBound();
            vehicle.addWheel(wheel_br.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                    wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

            Geometry wheel_bl = findGeom(carNode, "WheelBackLeft");
            wheel_bl.center();
            box = (BoundingBox) wheel_bl.getModelBound();
            vehicle.addWheel(wheel_bl.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                    wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

            vehicle.getWheel(2).setFrictionSlip(4);
            vehicle.getWheel(3).setFrictionSlip(4);


            getPhysicsSpace().add(vehicle);

            rootNode.attachChild(carNode);

            BoundingSphere bs = new BoundingSphere(20, vehicle.getPhysicsLocation());

            bounds.add(bs);

            rays.add(new Ray(new Vector3f(), vehicle.getPhysicsLocation()));
            rays.get(noOfCars++).setLimit(20);

        }

        private PhysicsSpace getPhysicsSpace() {
            return bulletAppState.getPhysicsSpace();
        }

        private Geometry findGeom(Spatial spatial, String name) {
            if (spatial instanceof Node) {
                Node node = (Node) spatial;
                for (int i = 0; i < node.getQuantity(); i++) {
                    Spatial child = node.getChild(i);
                    Geometry result = findGeom(child, name);
                    if (result != null) {
                        return result;
                    }
                }
            } else if (spatial instanceof Geometry) {
                if (spatial.getName().startsWith(name)) {
                    return (Geometry) spatial;
                }
            }
            return null;
        }


    }
