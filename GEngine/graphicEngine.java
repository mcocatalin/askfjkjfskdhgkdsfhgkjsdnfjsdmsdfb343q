package GEngine;

import Nucleus.CoreAgent;
import Utility.*;
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
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.jogamp.common.util.InterruptSource;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.*;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.controls.listbox.builder.ListBoxBuilder;
import de.lessvoid.nifty.controls.scrollpanel.builder.ScrollPanelBuilder;
import de.lessvoid.nifty.controls.slider.builder.SliderBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import org.bushe.swing.event.EventTopicSubscriber;

import java.util.*;

import static Controlling.IntersectionController.cicleInterval;
import static Utility.Helper.*;
import static com.jme3.math.ColorRGBA.Green;
import static com.jme3.math.ColorRGBA.Red;
import static jade.tools.sniffer.Message.offset;

public class graphicEngine extends SimpleApplication implements ActionListener {

    // Type of negociation
    public static boolean uncontrolledNegociation = true;

    // Plot variables
    private double timeVector[];
    private int simulationTime;
    private double intersectionDensityPlotVector[][];
    private double intersectionLanePlotVector[][][];
    private int plotIndex;

    // Mem er used for vehicle flow simulation
    private int cycleTimeforLaneDecreasing;

    // Intersection members
    public static int numberOfIntersections = 5;
    public static int numberOfSensorperLane = 3;
    public static int maxCarsPerSensingArea = 3;
    public static IntersectionActing[] intersectionActings= new IntersectionActing[numberOfIntersections]; // surpressed in LocationGraph


    //  TIMER TASK VARIABLES
    public static int carWidth = 5;
    public static int carSpeed = 13; // Km/h
    public static int carDecrementDelay = 0;
    public static boolean changedUISpeed = false;
    Timer timerdelay[] = new Timer[numberOfIntersections];

    Timer timerdelayNormal[] = new Timer[numberOfIntersections];

    // Graphic UI members
    public static boolean startApplication = false;
    public static int numberOfCars;
    private boolean gui = false;
    public static boolean controllerPairedState = false;
    public static boolean[] ActiveIntersectionControllers = new boolean[numberOfIntersections];
    public static boolean[] ActiveIntersectionSensors = new boolean[numberOfIntersections];
    public static boolean[] ActiveIntersectionActuators = new boolean[numberOfIntersections];


    public BitmapText hudText;
    public CharacterControl player;
    public BulletAppState bulletAppState;
    public static Nifty nifty;
    public boolean left = false, right = false, up = false, down = false, camera = false, tp = false;
    public Vector3f camDir = new Vector3f();
    public Vector3f camLeft = new Vector3f();
    public Vector3f walkDirection = new Vector3f();
    public RigidBodyControl traffic_light;
    public RigidBodyControl drone_light;


    // Vehicle members
    public VehicleControl vehicle;
    public VehicleWheel fr, fl, br, bl;
    public Node node_fr, node_fl, node_br, node_bl;
    public float wheelRadius;
    public float steeringValue = 0;
    public float accelerationValue = 0;
    public float decelerationValue = 0;
    public Node carNode;
    public Node[] trafficLights;
    public Node[] droneControllers;
    public Node boxNode;

    public boolean doneCreatingWorldNet = false;

    public LinkedList<BoundingSphere> bounds = new LinkedList<BoundingSphere>();

    public LinkedList<Ray> rays = new LinkedList<Ray>();

    public static LinkedList<WorldDetector> worldDetectors = new LinkedList<WorldDetector>();

    public Vector3f trafficLightPointSimulation[] =
            {
                    // MIDDLE intersection
                    new Vector3f(-58f, -4f, -13f), // UP
            };

    public SpotLight[][] trafficLightSpots = new SpotLight[numberOfIntersections][4];

    public Vector3f trafficLightLocations[][] ={
                    {
                            new Vector3f(-62.5f, -5.5f, -17.2f), // MIDDLE intersection
                            new Vector3f(-62.5f, -5.5f, -1.2f),
                            new Vector3f(-79.5f, -5.5f, -1.2f),
                            new Vector3f(-79.5f, -5.5f, -17.2f)
                    },

                    {       new Vector3f(-156.8f, -5.5f, -17.2f), // UP intersection
                            new Vector3f(-156.8f, -5.5f, -1.2f),
                            new Vector3f(-173.8f, -5.5f, -1.2f),
                            new Vector3f(-173.8f, -5.5f, -17.2f)
                    },

                    {
                            new Vector3f(-62.5f, -5.5f, 77.2f), // LEFT intersection
                            new Vector3f(-62.5f, -5.5f, 93.4f),
                            new Vector3f(-79.5f, -5.5f, 93.3f),
                            new Vector3f(-79.5f, -5.5f, 77.3f)
                    },

                    {
                            new Vector3f(32.1f, -5.5f, -17.2f), // DOWN intersection
                            new Vector3f(32.1f, -5.5f, -1.2f),
                            new Vector3f(15.1f, -5.5f, -1.2f),
                            new Vector3f(15.1f, -5.5f, -17.2f)
                    },

                    {
                            new Vector3f(-62.5f, -5.5f, -111.5f), // RIGHT intersection
                            new Vector3f(-62.5f, -5.5f, -95.4f),
                            new Vector3f(-79.5f, -5.5f, -95.4f),
                            new Vector3f(-79.5f, -5.5f, -111.4f)
                    }
            };

    public Vector3f droneControllerLocations[] = {
            new Vector3f(-71f, 5f, -9f),
            new Vector3f(-167f, 5f, -9f),
            new Vector3f(-71f, 5f, 86f),
            new Vector3f(23f, 5f, -9f),
            new Vector3f(-71f, 5f, -103f),
    };

    // intersection density pe intersection
    public IntersectionSensing intersectionSensingDensity[];

    // Engine const variables
    final String[] modelPaths = {"Models/Ferrari/Car.scene", "src/assets/Models/Ford.zip"};

    final Vector3f[] valideLocations = {
            new Vector3f(-67, 0, 6),
            new Vector3f(-67, 0, 14),
            new Vector3f(27, 0, 66),
            new Vector3f(98, 0, 81),
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
            new Matrix3f(-1, 0, 0, 0, 1, 0, 0, 0,-1),
            new Matrix3f(0, 0, 1, 0, 1, 0, -1, 0,0),
    };

    // Cycle time for intersection states
    public static boolean expiredCycleTime[] = new boolean[numberOfIntersections];
    public static boolean normalCycleTimer[] = new boolean[numberOfIntersections];

    public LinkedList<IntersectionItem> Intersections;

    // Communication members
    public static List<actingHandler> request = Collections.synchronizedList(new ArrayList<actingHandler>());
    public static List<sensingHandler> response = Collections.synchronizedList(new ArrayList<sensingHandler>());

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

    private float speed = -800f;

    Spatial map;

    //VehicleController vc;

    private Vector3f posDir = new Vector3f();
    private Vector3f upDirVehicle = new Vector3f();
    private Vector3f dir = new Vector3f();
    private Vector3f targetPosDir = new Vector3f();

    public static LinkedList<String> EventLogEntries = new LinkedList<>();

    public static void main(String[] args) {

    }

    public int GetIntersectionLocationDensity(Vector3f Location) { // not quite nice
        // !!!!!!!!!!!!!!!!!! Check number of cars in range!!!

        Vector<Integer> resultVec = new Vector<Integer>();

        int result[] = new int[numberOfSensorperLane];
        int aux;

        //note a single Random object is reused here
        Random randomGenerator = new Random();
        for(int i=0; i<numberOfSensorperLane;i++)
            result[i] = randomGenerator.nextInt(4); // maximum 4 cars to detect on a zone of sensors

         Arrays.sort(result);
        for( int i = 0; i < result.length/2; ++i )
        {
            aux = result[i];
            result[i] = result[result.length - i - 1];
            result[result.length - i - 1] = aux;
        }



        return 0;
    }

    public static TextBuilder consoleWindowText = new TextBuilder("myTextBuilder") {{
        text("qwe");
        style("base-font");
        color("#eeef");
        textHAlign(Align.Left);
        width("100%");
    }};

    public static ScrollPanelBuilder consoleWindow = new ScrollPanelBuilder("myScrollPanel")//("myWindow", "Title of Window")

    {{
        alignCenter();

        //closeable(false);  // you can't close this window
        width("100%"); // windows will need a size
        height("100%");
        //control(consoleWindow);
        text(consoleWindowText);
        text("asd1");
    }};

    ConsoleBuilder cs = new ConsoleBuilder("console") {{
        width("100%");
        height("100%");
        lines(17);
        style("base-font");
        color("#eeef");
        this.focusable(false);

        alignCenter();
        valignCenter();
        this.text(consoleWindowText);

    }};

    PanelBuilder ConsoleApp = new PanelBuilder("Left Panel with Console Application") {
        {
            childLayoutVertical();
            valignBottom();
            alignLeft();
            width("350px");
            style("nifty-panel-no-shadow");
            height("100%");

//                                assetManager.registerLoader(AWTLoader.class, "jpg");
//
//                                assetManager.registerLocator("/", FileLocator.class);
//
//                                image(new ImageBuilder() {{
//                                    alignLeft();
//                                    valignBottom();
//                                    filename("acs.logo.jpg");
//                                }});

            control(new ButtonBuilder("EventLogConsole", "Event Log:") {{
                alignCenter();
                height("5%");
                width("100%");
                this.onActiveEffect(new EffectBuilder("nimic"));
                this.focusable(false);
            }});


            // this creates a simple console with 25 lines that is 80% width (of the parent element) and for demonstration purpose there is an effect added
            // create a window
            //control(consoleWindow);
            //control(cs);
            // Using the builder pattern
            control(new ListBoxBuilder("myListBox") {{
                displayItems(35); // 43 for fullHD!!! 35 for 1600x900!!!!
                selectionModeDisabled();
                height("100%");
                width("100%"); // standard nifty width attribute
            }});

        }
    };

    static int indexIntesection = 0;
    private IntersectionSensing GetIntersectionState(IntersectionItem intersectionItem) {
//        indexIntesection = indexIntesection%2;
        return new IntersectionSensing();
//                GetIntersectionLocationDensity(intersectionItem.getItemLocation()[0]) + indexIntesection, // 0-1 alternate
//                GetIntersectionLocationDensity(intersectionItem.getItemLocation()[1]) + indexIntesection,
//                GetIntersectionLocationDensity(intersectionItem.getItemLocation()[2]) + indexIntesection,
//                GetIntersectionLocationDensity(intersectionItem.getItemLocation()[3]) + indexIntesection
//        );

    }



    public void SetIntersections() {
        int index = 0;
        Intersections = new LinkedList<IntersectionItem>();
        for(int i = 0; i< numberOfIntersections; i++) {
            Intersections.add(new IntersectionItem(trafficLightLocations[i][0],  // UP
                    trafficLightLocations[i][1],  // DOWN
                    trafficLightLocations[i][2],  // RIGHT
                    trafficLightLocations[i][3]));// LEFT
            worldDetectors.add(new WorldDetector("IntersectionDetect", i, Intersections.get(i)));

        }

        trafficLights = new Node[trafficLightLocations.length]; // numar de semafoare

        droneControllers = new Node[numberOfIntersections]; // numar de Controllere

        for (int i = 0; i < trafficLightLocations.length; i++) {
            for(int j=0; j<4;j++) {
                LoadIntersectionlights(trafficLightLocations[i][j], valideRotations[j], trafficLights[i]);
            }
        }

        for (int i = 0; i < numberOfIntersections; i++) {
            LoadDroneControllers(droneControllerLocations[i], droneControllers[i]);
        }

        doneCreatingWorldNet = true;

    }

    void GenerateWorldRandomIncrement(){ // 0 - to go NORD, 1 - TO GO WEST, 2 - TO GO SOUTH, 3 - TO GO EAST

        int randomIntersection = (new Random()).nextInt(numberOfIntersections-1) +1; // peripheryc intersection to generatate a new presence of a car
        int randomLane = (new Random()).nextInt(4);
                switch (randomIntersection){
                    case 1:
                            if(CoreAgent.LocationGraph.get(randomIntersection).isDownNeighbour() != null)
                                CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(2, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(2) + 1);

                            else
                                CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(randomLane, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(randomLane) + 1);
                        break;
                    case 2:
                            if(CoreAgent.LocationGraph.get(randomIntersection).isRightNeighbour() != null)
                                CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(1, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(1) + 1);

                            else
                                CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(randomLane, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(randomLane) + 1);
                        break;
                    case 3:
                            if(CoreAgent.LocationGraph.get(randomIntersection).isUpNeighbour() != null)
                                CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(0, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(0) + 1);

                            else
                                CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(randomLane, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(randomLane) + 1);
                        break;
                    case 4:
                            if(CoreAgent.LocationGraph.get(randomIntersection).isLeftNeighbour() != null)
                                CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(3, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(3) + 1);

                            else
                                CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(randomLane, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(randomLane) + 1);
                        break;
                }

//        CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().setLaneDensity(randomLane, CoreAgent.LocationGraph.get(randomIntersection).getIntersectionSensing().getDensity(randomLane) + 1);

    }


boolean doneInitCreatingCarSimulation = false;

    private void UpdateSimulationCars() {
             // Update number of cars for the Core Global Net of intersections with their states.

                if (doneInitCreatingCarSimulation) {

                    for (int i = 0; i < CoreAgent.LocationGraph.size(); i++) {
                        if (CoreAgent.LocationGraph.get(i).getIntersectionActing() != null) {
                            boolean UpState = CoreAgent.LocationGraph.get(i).getIntersectionActing().getIntersectionState()[0];
                            boolean RightState = CoreAgent.LocationGraph.get(i).getIntersectionActing().getIntersectionState()[1];
                            boolean DownState = CoreAgent.LocationGraph.get(i).getIntersectionActing().getIntersectionState()[2];
                            boolean LeftState = CoreAgent.LocationGraph.get(i).getIntersectionActing().getIntersectionState()[3];

                            if (UpState ) {
                                if(CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0) > 0) {

                                    if (CoreAgent.LocationGraph.get(i).isUpNeighbour() != null) {
                                        if (!CoreAgent.LocationGraph.get(i).isUpNeighbour().getIntersectionSensing().IntersectionLaneDensityISFULL(0)) { // check if neighbour lane is full

                                            CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(0, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0) - 1); // fill up neighbour with cars on up lane

                                            CoreAgent.LocationGraph.get(i).isUpNeighbour().getIntersectionSensing().setLaneDensity(0, CoreAgent.LocationGraph.get(i).isUpNeighbour().getIntersectionSensing().getDensity(0) + 1); // fill up neighbour with cars on up lane
                                        }
                                    }
                                    else{
                                        CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(0, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0) - 1); // fill up neighbour with cars on up lane
                                        GenerateWorldRandomIncrement();
                                     }
                                }
                                Helper.LogDebugUseData(i, (System.currentTimeMillis() - tStart) / 1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3),  expiredCycleTime[1], 0);
                                Helper.LogFileData(i, (System.currentTimeMillis() - Helper.tStart) / 1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3));

//                                int randomLaneGenerator = (new Random()).nextInt(4);
//                                CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(randomLaneGenerator, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(randomLaneGenerator) + 1);
                            }

                            if (RightState) {
                                if(CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1) > 0) {

                                    if (CoreAgent.LocationGraph.get(i).isRightNeighbour() != null) {
                                        if (!CoreAgent.LocationGraph.get(i).isRightNeighbour().getIntersectionSensing().IntersectionLaneDensityISFULL(3)) {

                                            CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(1, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1) - 1);

                                            CoreAgent.LocationGraph.get(i).isRightNeighbour().getIntersectionSensing().setLaneDensity(1, CoreAgent.LocationGraph.get(i).isRightNeighbour().getIntersectionSensing().getDensity(1) + 1); // fill up neighbour with cars on up lane
                                        }
                                    } else {
                                        CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(1, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1) - 1); // fill up neighbour with cars on up lane
                                        GenerateWorldRandomIncrement();
                                    }
                                }
                                Helper.LogDebugUseData(i, (System.currentTimeMillis() - tStart)/1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3),  expiredCycleTime[1], 1);
                                Helper.LogFileData(i, (System.currentTimeMillis() - Helper.tStart) / 1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3));

//                                int randomLaneGenerator = (new Random()).nextInt(4);
//                                CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(randomLaneGenerator, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(randomLaneGenerator) + 1);

                            }

                            if (DownState) {
                                if(CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2) > 0) {

                                    if (CoreAgent.LocationGraph.get(i).isDownNeighbour() != null) {
                                        {
                                            if (!CoreAgent.LocationGraph.get(i).isDownNeighbour().getIntersectionSensing().IntersectionLaneDensityISFULL(2)) {

                                                CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(2, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2) - 1); // fill up neighbour with cars on up lane

                                                CoreAgent.LocationGraph.get(i).isDownNeighbour().getIntersectionSensing().setLaneDensity(2, CoreAgent.LocationGraph.get(i).isDownNeighbour().getIntersectionSensing().getDensity(2) + 1); // fill up neighbour with cars on up lane
                                            }
                                        }
                                    } else {
                                        CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(2, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2) - 1); // fill up neighbour with cars on up lane
                                        GenerateWorldRandomIncrement();
                                    }
                                }

                                Helper.LogDebugUseData(i, (System.currentTimeMillis() - tStart)/1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3),  expiredCycleTime[1], 2);
                                Helper.LogFileData(i, (System.currentTimeMillis() - Helper.tStart) / 1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3));

//                                int randomLaneGenerator = (new Random()).nextInt(4);
//                                CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(randomLaneGenerator, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(randomLaneGenerator) + 1);

                            }

                            if (LeftState) {
                                if(CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3) > 0) {

                                    if (CoreAgent.LocationGraph.get(i).isLeftNeighbour() != null) {
                                        if (!CoreAgent.LocationGraph.get(i).isLeftNeighbour().getIntersectionSensing().IntersectionLaneDensityISFULL(3)) {

                                            CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(3, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3) - 1); // fill up neighbour with cars on up lane

                                            CoreAgent.LocationGraph.get(i).isLeftNeighbour().getIntersectionSensing().setLaneDensity(3, CoreAgent.LocationGraph.get(i).isLeftNeighbour().getIntersectionSensing().getDensity(3) + 1); // fill up neighbour with cars on up lane
                                        }
                                    } else {
                                        CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(3, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3) - 1); // fill up neighbour with cars on up lane
                                        GenerateWorldRandomIncrement();
                                    }
                                }
                                Helper.LogDebugUseData(i, (System.currentTimeMillis() - tStart)/1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3),  expiredCycleTime[1], 3);
                                Helper.LogFileData(i, (System.currentTimeMillis() - Helper.tStart) / 1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3));

//                                int randomLaneGenerator = (new Random()).nextInt(4);
//                                CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(randomLaneGenerator, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(randomLaneGenerator) + 1);

                            }

                        }

//                        int randomLaneGenerator = (new Random()).nextInt(4);
//                        CoreAgent.LocationGraph.get(i).getIntersectionSensing().setLaneDensity(randomLaneGenerator, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(randomLaneGenerator) + 1);

                        //                       Helper.LogFileData(i, (System.currentTimeMillis() - Helper.tStart) / 1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3));
                    }

                    expiredCycleTime[0] = false;
                }
        Helper.go = false;
    }





    @Override
    public void simpleInitApp() {

        for(int i=0; i<4; i++) {
            timerdelay[i] = new Timer();
        }

        for(int i=0; i<numberOfIntersections; i++){

            timerdelayNormal[i] = new Timer();
        }

//        Box box = new Box(27, 0, 66);

        // Create red transparent material
        Material mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.5f)); // 0.5f is the alpha value

        // Activate the use of the alpha channel
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        // Create rectangle of size 10x10
        Geometry mouseRect = new Geometry("MouseRect", new Quad(10, 10));
        mouseRect.setMaterial(mat);
        mouseRect.setLocalTranslation(valideLocations[2]);
        rootNode.attachChild(mouseRect);


        Box b = new Box(Vector3f.ZERO, 1, 1, 1); // take a cube shape

        Geometry geom = new Geometry("Box", b); // create a Spatial from it

//        Material mat1 = new Material(assetManager,
//
//                "Common/MatDefs/Misc/SolidColor.j3md"); // load a solid color material
//
//        mat1.setColor("m_Color", ColorRGBA.Blue); // make the solid color blue
//
//        geom.setMaterial(mat1); // give the box the solid blue material

        Material myMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        myMaterial.setColor("Color", ColorRGBA.Black);


        geom.setMaterial(myMaterial);

        rootNode.attachChild(geom); // make the blue box appear in the scene

        viewPort.setBackgroundColor(ColorRGBA.Gray);




        // Plot variables
        plotIndex = 0;

        simulationTime = 100;
        EventLogEntries.add("Timpul de analiza este: " + simulationTime + " secunde.");

//        timeVector = new double[simulationTime];
//        intersectionDensityPlotVector = new double[numberOfIntersections][simulationTime];
//
//        for(int i=0;i<numberOfIntersections;i++){
//            for(int j=0;j<simulationTime;j++){
//                intersectionDensityPlotVector[i][j] = 0;
//            }
//        }
//
//        intersectionLanePlotVector = new double[numberOfIntersections][4][simulationTime];
//        for(int i=0;i<numberOfIntersections;i++){
//            for(int j=0;j<4;j++){ // lanes
//                for(int k=0;k<simulationTime;k++) { // values
//                    intersectionLanePlotVector[i][j][k] = 0;
//                }
//            }
//        }

        // Create some sample data
        double[] x = new double[100]; x[0] = 1;
        double[] y1 = new double[100]; y1[0] = 200;
        double[] y2 = new double[100]; y2[0] = 300;
        for(int i = 1; i < x.length; i++){
            x[i] = i+1;
            y1[i] = y1[i-1] + Math.random()*10 - 4;
            y2[i] = y2[i-1] + Math.random()*10 - 6;
        }

        // JAVA:                             // MATLAB:
        JavaPlotter fig = new JavaPlotter(); // figure('Position',[100 100 640 480]);
        fig.plot(x, y1, "-r", 2.0f, "AAPL"); // plot(x,y1,'-r','LineWidth',2);
        fig.plot(x, y2, ":k", 3.0f, "BAC");  // plot(x,y2,':k','LineWidth',3);
        fig.RenderPlot();                    // First render plot before modifying
        fig.title("Stock 1 vs. Stock 2");    // title('Stock 1 vs. Stock 2');
        fig.xlim(10, 100);                   // xlim([10 100]);
        fig.ylim(200, 300);                  // ylim([200 300]);
        fig.xlabel("Days");                  // xlabel('Days');
        fig.ylabel("Price");                 // ylabel('Price');
        fig.grid("on","on");                 // grid on;
        fig.legend("northeast");             // legend('AAPL','BAC','Location','northeast')
        fig.font("Helvetica",15);            // .. 'FontName','Helvetica','FontSize',15
        fig.saveas("MyPlot1.jpeg",800,600);   // saveas(gcf,'MyPlot','jpeg');

        intersectionSensingDensity = new IntersectionSensing[numberOfIntersections];

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        load_interfata();

        setMap();
        setHudText();
        setSun();
        load_sky();
        buildVehicle(modelPaths[0], valideLocations[0], map.getWorldRotation().toRotationMatrix());
        buildVehicle(modelPaths[0], valideLocations[1], map.getWorldRotation().toRotationMatrix());
        //LoadIntersectionlights();

        flyCam.setEnabled(true);
        setDisplayFps(false);
        setDisplayStatView(false);


        cameraSetup();
        //load_player();

        SetIntersections();

        setUpKeys();

        // INIT COUNTER!!!
        Timer tm[] = new Timer[4];
        for(int i=0; i<4;i++) {
            tm[i] = new Timer();
        }

//        tmtsk[0] = new TimerTask() {
//            @Override
//            public void run() {
//                expiredCycleTime[0] = true;
////                if(changedUISpeed){
////                    this.cancel();
//////                    this = new TimerTask();
////
////                }
//            }
//        };
//
//        tmtsk[1] = new TimerTask() {
//            @Override
//            public void run() {
//                expiredCycleTime[1] = true;
//            }
//        };
//
//        tmtsk[2] = new TimerTask() {
//            @Override
//            public void run() {
//                expiredCycleTime[2] = true;
//            }
//        };
//
//        tmtsk[3]= new TimerTask() {
//            @Override
//            public void run() {
//                expiredCycleTime[3] = true;
//            }
//        };


        // INIT COUNTER!!!
        Timer normalCycle[] = new Timer[numberOfIntersections];
        for(int i=0; i<numberOfIntersections;i++) {
            normalCycle[i] = new Timer();
        }

        tmtskNormal[0] = new TimerTask() {
            @Override
            public void run() {
                normalCycleTimer[0] = true;
            }
        };

        tmtskNormal[1] = new TimerTask() {
            @Override
            public void run() {
                normalCycleTimer[1] = true;
            }
        };

        tmtskNormal[2] = new TimerTask() {
            @Override
            public void run() {
                normalCycleTimer[2] = true;
            }
        };

        tmtskNormal[3]= new TimerTask() {
            @Override
            public void run() {
                normalCycleTimer[3] = true;
            }
        };

        tmtskNormal[4]= new TimerTask() {
            @Override
            public void run() {
                normalCycleTimer[4] = true;
            }
        };
//
//        for(int i=0; i<4;i++) {
//            tm[i].schedule(Helper.tmtsk[i], 0, cicleInterval);
//        }





        // INIT COUNTER!!!
        Timer tm1 = new Timer();
        tm1.schedule(Helper.globalSimulator, 0, cicleInterval); // decrementing delay for one car per lane

        // DEBUG
        //response.add(new sensingHandler("Intersection", index, intersectionLaneValues));



    }



    private void load_sky() {
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false);
        sky.setLocalScale(1000);
        rootNode.attachChild(sky);
        rootNode.setShadowMode(RenderQueue.ShadowMode.Off);
    }

    private void LoadDroneControllers(Vector3f Location, Node droneController){
        assetManager.registerLocator("src\\assets\\Models\\dronaController.zip", ZipLocator.class);
        droneController = (Node) assetManager.loadModel("dronaController.mesh.j3o");
        droneController.setLocalTranslation(Location);

        //Geometry chasis = findGeom(trafficLights, "trafficLights");
        // BoundingBox box = (BoundingBox) chasis.getModelBound();
//Create a hull collision shape for the chassis
        //CollisionShape trafficLightHull = CollisionShapeFactory.createDynamicMeshShape(chasis); !!! Cannot find geometry - LOSS!!!S
        drone_light = new RigidBodyControl(0);

        droneController.addControl(drone_light);
        drone_light.setPhysicsLocation(Location); //new Vector3f(199,10,-64));
       // drone_light.setPhysicsRotation(Rotation);// new Matrix3f(0,0,1,0,1,0,-1,0,0)); // rotate with 270 degrees on Y

        getPhysicsSpace().add(drone_light);

        rootNode.attachChild(droneController);
    }

    private void LoadIntersectionlights(Vector3f Location, Matrix3f Rotation, Node trafficLight) {

        assetManager.registerLocator("src\\assets\\Models\\semafor.zip", ZipLocator.class);
        trafficLight = (Node) assetManager.loadModel("semafor_v7.mesh.j3o");
        trafficLight.setLocalTranslation(Location);

        //Geometry chasis = findGeom(trafficLights, "trafficLights");
        // BoundingBox box = (BoundingBox) chasis.getModelBound();
//Create a hull collision shape for the chassis
        //CollisionShape trafficLightHull = CollisionShapeFactory.createDynamicMeshShape(chasis); !!! Cannot find geometry - LOSS!!!S
        traffic_light = new RigidBodyControl(0);

        trafficLight.addControl(traffic_light);
        traffic_light.setPhysicsLocation(Location); //new Vector3f(199,10,-64));
        traffic_light.setPhysicsRotation(Rotation);// new Matrix3f(0,0,1,0,1,0,-1,0,0)); // rotate with 270 degrees on Y

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

                float anglemult = FastMath.PI / 4.0f;
                float speedmult = 0.3f;//0.3f;

                if (angle > FastMath.QUARTER_PI) {
                    angle = FastMath.QUARTER_PI/2;
                }
                //left or right
                if (plane.whichSide(targetLocation) == Plane.Side.Negative) {
                    anglemult *= -1;
                }
                //backwards
                if (dot > 1) {
                    anglemult *= -1;
                    speedmult *=-1;
                }
                vehicle.steer(angle * anglemult );
                vehicle.accelerate(speed * speedmult);
                vehicle.brake(0);
            }
    }

    public void CarGoTo(Vector3f destination) {
         Vector3f targetLocation = new Vector3f();
         Vector3f vector1 = new Vector3f();
         Vector3f vector2 = new Vector3f();
         Vector3f vector3 = new Vector3f();
         Vector3f vector4 = new Vector3f();

         Vector3f pos = new Vector3f();
         Vector3f up = new Vector3f();
         Vector3f dir = new Vector3f();
         Vector3f targetPos = new Vector3f();

        vehicle.getPhysicsLocation(vector1);
        vector2.set(targetLocation);
        vector2.subtractLocal(vector1);

        vector2.normalizeLocal();

        pos.set(vehicle.getPhysicsLocation());
        up.set(new Vector3f(0,1,0));
        dir.set(vector2);
        targetPos.set(targetLocation);



        Vector3f left = up.cross(dir);  // might be dir.cross(up)

        Vector3f targetRelative = targetPos.subtract(pos).normalizeLocal();

        float steer = left.dot(targetRelative);
        float forward = dir.dot(targetRelative);

        if( forward < 0 ) steer *= -1;

        vehicle.steer(-200 * steer);
        vehicle.accelerate(200 * forward);

    }

    private void StartSimulationTimer(){
        if(changedUISpeed){

            cycleTimeforLaneDecreasing = 3600 * carWidth  / carSpeed; // ms for car to run for it's width size
//            for(int i=0; i<3;i++) {
            timerdelay[0].scheduleAtFixedRate(Helper.tmtskNormalPhase, 0, cycleTimeforLaneDecreasing);
//            }

            EventLogEntries.add("Viteza medie este de " + carSpeed + " km/h.");
            EventLogEntries.add("Faza minima este de " + cicleInterval/1000 + " secunde");


            for(int i=0; i<numberOfIntersections; i++){
                timerdelayNormal[i].schedule(tmtskNormal[i], cycleTimeforLaneDecreasing, cicleInterval);
            }



            int samples = 0;
            if((float)cycleTimeforLaneDecreasing/1000 <= 1)
                samples = 1000*simulationTime/cycleTimeforLaneDecreasing;
            else
                samples = simulationTime;
            timeVector = new double[samples];

            intersectionDensityPlotVector = new double[numberOfIntersections][samples];

            for(int i=0;i<numberOfIntersections;i++){
                for(int j=0;j<samples;j++){
                    intersectionDensityPlotVector[i][j] = 0;
                }
            }

            intersectionLanePlotVector = new double[numberOfIntersections][4][samples];
            for(int i=0;i<numberOfIntersections;i++){
                for(int j=0;j<4;j++){ // lanes
                    for(int k=0;k<samples;k++) { // values
                        intersectionLanePlotVector[i][j][k] = 0;
                    }
                }
            }

            Helper.tStart = System.currentTimeMillis();

            EventLogEntries.add("Perioada de esantionare a fluxului de trafic este\n de " + (float)cycleTimeforLaneDecreasing/1000+" secunde.");

            changedUISpeed = false;
        }
    }

    private void changeBoxColor(){

        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);

        rootNode.getChild("Box").setMaterial(mat2);
    }


    @Override
    public void simpleUpdate(float tpf) {

        try {
            InterruptSource.Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StartSimulationTimer();

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
            if(gui)
            {
                flyCam.setDragToRotate(true);
            }
            else
            {
                flyCam.setDragToRotate(false);
            }

//            player.setWalkDirection(walkDirection); !!!!!!!
//            cam.setLocation(new Vector3f(player.getPhysicsLocation().getX(), player.getPhysicsLocation().getY() - 4, player.getPhysicsLocation().getZ()));
        }

        ///!!! Sensing on the Graphic Engine !!!
        SenseOnTrafficFlow();

        ///!!! Acting on the Graphic Engine !!!
        ActOnTrafficPhase();


        if(normalPhaseTick) {

            ///!!! Traffic Flow Simulations !!!
            SimulateTrafficFlow();

            normalPhaseTick = false;
        }

        ///!!! Autonomous Car Simulation !!!
        if(expiredCycleTime[3])
        {
            CarMoveAt(valideLocations[0].add(-100,0,0));
            expiredCycleTime[3] = false;
        }

        createEventLogEntry();



        if(timeVector != null) {
            // Create stats
            if (plotIndex >= timeVector.length && !donePlotting) {
                // JAVA:                             // MATLAB:
                JavaPlotter fig = new JavaPlotter(); // figure('Position',[100 100 640 480]);
                fig.plot(timeVector, intersectionDensityPlotVector[0], "-r", 2.0f, "Intersectia 1"); // plot(x,y1,'-r','LineWidth',2);
                fig.plot(timeVector, intersectionDensityPlotVector[1], "-k", 3.0f, "Intersectia 2");  // plot(x,y2,':k','LineWidth',3);
                fig.plot(timeVector, intersectionDensityPlotVector[2], "-y", 3.0f, "Intersectia 3");  // plot(x,y2,':k','LineWidth',3);
                fig.plot(timeVector, intersectionDensityPlotVector[3], "-m", 3.0f, "Intersectia 4");  // plot(x,y2,':k','LineWidth',3);
                fig.plot(timeVector, intersectionDensityPlotVector[4], "-g", 3.0f, "Intersectia 5");  // plot(x,y2,':k','LineWidth',3);
                fig.RenderPlot();                    // First render plot before modifying
                fig.title("Evolutia in timp a prioritatilor");    // title('Stock 1 vs. Stock 2');
                fig.xlim(0, simulationTime);                   // xlim([10 100]);
                fig.ylim(0, 40);                  // ylim([200 300]);
                fig.xlabel("Timp");                  // xlabel('Days');
                fig.ylabel("Prioritate");                 // ylabel('Price');
                fig.grid("on", "on");                 // grid on;
                fig.legend("northeast");             // legend('AAPL','BAC','Location','northeast')
                fig.font("Helvetica", 23);            // .. 'FontName','Helvetica','FontSize',15
                fig.saveas("nonControlOneStateWorldPriority.PNG", 850, 600);   // saveas(gcf,'MyPlot','jpeg');

//            for(int j=0;j<4;j++) {

                // JAVA:                             // MATLAB:
                JavaPlotter fig1 = new JavaPlotter(); // figure('Position',[100 100 640 480]);
                fig1.plot(timeVector, intersectionLanePlotVector[0][0], "-r", 2.0f, "Tronson Sud"); // plot(x,y1,'-r','LineWidth',2);
                fig1.plot(timeVector, intersectionLanePlotVector[0][1], "-k", 3.0f, "Tronson Vest");  // plot(x,y2,':k','LineWidth',3);
                fig1.plot(timeVector, intersectionLanePlotVector[0][2], "-y", 3.0f, "Tronson Nord");  // plot(x,y2,':k','LineWidth',3);
                fig1.plot(timeVector, intersectionLanePlotVector[0][3], "-m", 3.0f, "Tronson Est");  // plot(x,y2,':k','LineWidth',3);
//                fig1.plot(timeVector, intersectionDensityPlotVector[4], "-g", 3.0f, "Intersectia 5");  // plot(x,y2,':k','LineWidth',3);
                fig1.RenderPlot();                    // First render plot before modifying
                fig1.title("Evolutia in timp a densitatii pe tronsoane");    // title('Stock 1 vs. Stock 2');
                fig1.xlim(0, simulationTime);                   // xlim([10 100]);
                fig1.ylim(0, 40);                  // ylim([200 300]);
                fig1.xlabel("Timp");                  // xlabel('Days');
                fig1.ylabel("Densitate");                 // ylabel('Price');
                fig1.grid("on", "on");                 // grid on;
                fig1.legend("northeast");             // legend('AAPL','BAC','Location','northeast')
                fig1.font("Helvetica", 23);            // .. 'FontName','Helvetica','FontSize',15
                fig1.saveas("nonControlOneStateIntersection0.PNG", 850, 600);   // saveas(gcf,'MyPlot','jpeg');
//            }
                donePlotting = true;

                EventLogEntries.add("Plotare finalizata.");

            }
        }

    }

    boolean donePlotting = false;

    private void ActOnTrafficPhase(){
        if (!request.isEmpty()) {

            if(normalCycleTimer[0] ) {
                if ((controllerPairedState && request.size() >= numberOfIntersections) || (!controllerPairedState && request.size() >= numberOfIntersections)) {
                    for (int i = 0; i < numberOfIntersections; i++) {
                        outerloop:
                        for (int j = 0; j < request.size(); j++) {
                            if (CoreAgent.LocationGraph.get(i).getComponentID() == request.get(j).getComponentID()) {
                                if (CoreAgent.LocationGraph.get(i).getIntersectionActing() != null) {

                                    if (!CoreAgent.LocationGraph.get(i).getIntersectionActing().Equals(request.get(j).getObjToHandle())) {
                                        actOnTrafficLights(request.get(j));
                                        request.remove(j);
                                        System.out.println("Found another state for intersection ID = " + i + "dimensiune request: " + request.size());
                                        break outerloop;
                                    } else {
                                        System.out.println("Same state for intersection ID = " + i + "dimensiune request: " + request.size());
                                    }

                                } else {
                                    actOnTrafficLights(request.get(j));
                                    request.remove(j);
                                    break outerloop;
                                }
                            }
                        }
                    }
                    normalCycleTimer[0]  = false;
                    changeBoxColor();
                }
            }
        }
    }

    private void SimulateTrafficFlow(){

        InitCreateIntersectionSensing();
        if(normalPhaseTick) {
            UpdateSimulationCars();
            if(plotIndex == 0) {
                timeVector[plotIndex] = 0;
            }
            else
                if( plotIndex-1 < timeVector.length  && !donePlotting) {

                    timeVector[plotIndex] = timeVector[plotIndex - 1] + (double) cycleTimeforLaneDecreasing / 1000;
                    for (int i = 0; i < numberOfIntersections; i++) {
                        int maxDensity = CoreAgent.LocationGraph.get(i).getIntersectionSensing().getMaxDensity()[0] + CoreAgent.LocationGraph.get(i).getIntersectionSensing().getMaxDensity()[1];
                        intersectionDensityPlotVector[i][plotIndex] = (double) maxDensity;//.getDensity(i);

                        for (int j = 0; j < 4; j++) {
                            intersectionLanePlotVector[i][j][plotIndex] = CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(j);
                        }
                    }
                }


            plotIndex++;
        }

    }

    public void InitCreateIntersectionSensing(){
        if (CoreAgent.doneProcessingNucleusesLocation && !doneInitCreatingCarSimulation) { // World intersection net is not intialized in intersection sensing point of view.

            IntersectionSensing intersectionSensing;
            Random randomGenerator = new Random();

            int result;

            for (int i = 0; i < numberOfIntersections; i++) {
                intersectionSensing = new IntersectionSensing();
                for (int j = 0; j < 4; j++) {

                    int numberOfCarsperSensorArea = randomGenerator.nextInt(numberOfSensorperLane * maxCarsPerSensingArea);
                    for (int k = 0; k < numberOfSensorperLane; k++) {

                        if (numberOfCarsperSensorArea <= maxCarsPerSensingArea)
                            result = numberOfCarsperSensorArea;
                        else
                            result = maxCarsPerSensingArea;
                        numberOfCarsperSensorArea = numberOfCarsperSensorArea - result;

                        intersectionSensing.setLaneDensityPerObj(j, k, result);
                    }
                }

                CoreAgent.LocationGraph.get(i).setIntersectionSensing(intersectionSensing);
                Helper.LogFileData(i, (System.currentTimeMillis() - Helper.tStart) / 1000, CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(0), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(1), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(2), CoreAgent.LocationGraph.get(i).getIntersectionSensing().getDensity(3));
            }

            doneInitCreatingCarSimulation = true;
        }
    }

    public void actOnTrafficLights(actingHandler act) {
        //System.out.println("\nStarted normal cycle for intersection " + act.getComponentID() );

        // Update Core Agent sensing objects
        CoreAgent.LocationGraph.get(act.getComponentID()).setIntersectionActing(act.getObjToHandle());

        if(act.waitCycle == true){
            if(act.getObjToHandle().getIntersectionState()[0] && act.getObjToHandle().getIntersectionState()[2]){
                // Intersection Item position are ok, clockwise starting from UP, where UP means from main perspective the lane to go UP, results the intersection item from the bottom!!!

            }
            if(act.getObjToHandle().getIntersectionState()[1] && act.getObjToHandle().getIntersectionState()[3]){
                // Intersection Item position are ok, clockwise starting from UP, where UP means from main perspective the lane to go UP, results the intersection item from the bottom!!!
            }

        }
    }

    public static void createEventLogEntry(){
        if(!EventLogEntries.isEmpty()) {
            String event = EventLogEntries.remove(0);
            nifty.getCurrentScreen().findNiftyControl("myListBox", ListBox.class).addItem( ">>" + " " + event);
            nifty.getCurrentScreen().findNiftyControl("myListBox", ListBox.class).addItem( "");
        }
    }

    private void SenseOnTrafficFlow() {
        synchronized (response) {
            if (CoreAgent.automaticMode) {
                sensingHandler currentResponse;

                for (int i = 0; i < numberOfIntersections; i++) {
                    if (CoreAgent.LocationGraph.size() == numberOfIntersections) {
                        if (CoreAgent.LocationGraph.get(i).getIntersectionSensing() != null) {
                            currentResponse = new sensingHandler("Intersection", i, CoreAgent.LocationGraph.get(i).getIntersectionSensing());

                            if (graphicEngine.response.size() != 0) {
                                boolean equals = false;
                                for (int k = 0; k < graphicEngine.response.size(); k++) {

                                    equals = equals || graphicEngine.response.get(k).equals(currentResponse);
                                    if (equals)
                                        break;
                                }
                                if (!equals) {
                                    graphicEngine.response.add(currentResponse);
                                }
                            } else {
                                graphicEngine.response.add(currentResponse);
                            }


                            //graphicEngine.response.add(currentResponse);
                        }
                    }
                }

            }
        }
    }

    private void load_interfata() {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        int h = settings.getHeight();
        int w = settings.getWidth();

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        nifty.addScreen("test", new ScreenBuilder("Hello Nifty Screen") {{
            controller(new DefaultScreenController()); // Screen properties

            // <layer>
            layer(new LayerBuilder("Layer_ID") {{ // Most of the ID-s not relevant!!!
                childLayoutHorizontal(); // layer properties, add more...

                width(w + "px");
                height("350px");

                panel(new PanelBuilder("Full Screen Panel") {
                    {
                        childLayoutCenter();
                        width("100%");
                        height("100%");

                        panel(new PanelBuilder("Botton Right Screen Panel") {
                            {
                                childLayoutVertical(); // panel properties, add more...
                                width("300px");
                                style("nifty-panel-no-shadow");
                                height("500px");
                                valignBottom();
                                alignRight();

                                panel(new PanelBuilder("controllerManualAutomaticModePanel") { // Panel for Manual / Automatic control
                                    {
                                        //childLayoutVertical(); // panel properties, add more...
                                        childLayoutHorizontal();
                                        width("100%");
                                        height("40");
                                        valignTop();
                                        alignCenter();

                                        control(new ButtonBuilder("manualMode", "Manual") {{
                                            width("50%");
                                            height("40px");
                                            focusable(true);
                                            //this.onCustomEffect();
                                        }});

                                        control(new ButtonBuilder("automaticMode", "Automat") {{
                                            //style("nifty-panel-red");
                                            width("50%");
                                            height("40px");
                                            focusable(true);
                                        }});
                                    }});
//
                                control(new ButtonBuilder("startMASinit", "Start MAS") {{
                                    //style("nifty-panel-red");
                                    width("100%");
                                    height("40px");
                                    focusable(false);
                                    //color(Color.randomColor());

                                    //this.style("nifty-panel-red");
                                }});

                                control(new ButtonBuilder("StartSimulation", "Start simulare") {{
                                    //style("nifty-panel-red");
                                    width("100%");
                                    height("40px");
                                    focusable(false);

                                }});

                                panel(new PanelBuilder("Interface for agents") {
                                    {
                                        childLayoutVertical();
                                        alignCenter();
                                        width("90%");
                                        height("210px");

//                                        control(new ButtonBuilder("referintaLabel", "Referinta Nucleu Global:") {{
//                                            alignCenter();
//                                            height("30px");
//                                            width("100%");
//                                            this.onActiveEffect(new EffectBuilder("nimic"));
//                                            this.focusable(false);
//                                        }});
//
//                                        control(new SliderBuilder("referintaValue", false) {{
//                                            alignCenter();
//                                            this.focusable(false);
//                                            width("90%");
//                                            height("40px");
//                                            buttonStepSize(1f);
//                                            min(1);
//                                            max(15);
//                                        }});


                                        control(new ButtonBuilder("carAverageSpeed", "Viteza medie de circulatie:") {{
                                            alignCenter();
                                            height("30px");
                                            width("100%");
                                            this.onActiveEffect(new EffectBuilder("nimic"));
                                            this.focusable(false);
                                        }});

                                        control(new SliderBuilder("carAverageSpeedValue", false) {{
                                            alignCenter();
                                            this.focusable(false);
                                            width("90%");
                                            height("40px");
                                            buttonStepSize(1f);
                                            min(5);
                                            max(50);
                                            this.initial(13);
                                        }});

                                        control(new ButtonBuilder("trafficLightInterval", "Interval semafor:") {{
                                            alignCenter();
                                            height("30px");
                                            width("100%");
                                            this.onActiveEffect(new EffectBuilder("nimic"));
                                            this.focusable(false);
                                        }});

                                        control(new SliderBuilder("trafficLightIntervalValue", false) {{
                                            alignCenter();
                                            this.focusable(false);
                                            width("90%");
                                            height("40px");
                                            buttonStepSize(1f);
                                            min(4);
                                            max(20);
                                            this.initial(8);
                                        }});

                                        panel(new PanelBuilder("Stari controllere semafor") {
                                            {
                                                childLayoutVertical();
                                                valignTop();
                                                alignCenter();
                                                width("90%");
                                                height("240px");
                                                control(new ButtonBuilder("pertext", "Stari controllere semafor") {{
                                                    alignCenter();
                                                    valignTop();
                                                    height("40px");
                                                    width("100%");
                                                    this.onActiveEffect(new EffectBuilder("nimic"));
                                                    this.focusable(false);

                                                }});

                                                panel(new PanelBuilder("LeftSemaforController") {
                                                    {
                                                        childLayoutAbsoluteInside();
                                                        width("90%");
                                                        height("66px");
                                                        valignTop();
                                                        alignCenter();

                                                        control(new CheckboxBuilder("activeIntersectionControllerUP") {{
                                                            alignCenter();
                                                            valignTop();
                                                            this.focusable(false);
                                                            this.checked(true);
                                                            width("40px");
                                                            height("40px");
                                                            this.x("34%");
                                                        }});

                                                        panel(new PanelBuilder("sensorsAndActuators") {
                                                            {
                                                                //childLayoutAbsoluteInside();
                                                                //childLayoutHorizontal();
                                                                childLayoutVertical();
                                                                width("40px");
                                                                height("40px");
                                                                // alignCenter();
                                                                alignLeft();
                                                                valignTop();
                                                                this.x("51%");

                                                                control(new CheckboxBuilder("activeIntersectionSensorUP") {{
                                                                    alignCenter();
                                                                    valignTop();
                                                                    this.focusable(false);
                                                                    this.checked(true);
                                                                    width("50%");
                                                                    height("50%");
                                                                }});

                                                                control(new CheckboxBuilder("activeIntersectionActuatorUP") {{
                                                                    alignCenter();
                                                                    valignTop();
                                                                    this.focusable(false);
                                                                    this.checked(true);
                                                                    width("50%");
                                                                    height("50%");
                                                                }});

                                                            }});

                                                    }});

                                                panel(new PanelBuilder("LeftSemaforController") {
                                                    {
                                                        childLayoutAbsoluteInside();
                                                        width("100%");
                                                        height("67px");
                                                        valignTop();
                                                        alignCenter();

                                                        panel(new PanelBuilder("LeftSemaforController") {
                                                            {
                                                                childLayoutAbsoluteInside();
                                                                width("33%");
                                                                height("100%");
                                                                valignTop();
                                                                alignCenter();

                                                                control(new CheckboxBuilder("activeIntersectionControllerLEFT") {{
                                                                    alignCenter();
                                                                    valignCenter();
                                                                    this.focusable(false);
                                                                    this.checked(true);
                                                                    width("40px");
                                                                    height("40px");
                                                                    this.x("9%");
                                                                }});

                                                                panel(new PanelBuilder("sensorsAndActuators") {
                                                                    {
                                                                        childLayoutVertical();
                                                                        width("40px");
                                                                        height("40px");
                                                                        alignLeft();
                                                                        valignTop();
                                                                        this.x("100%");

                                                                        control(new CheckboxBuilder("activeIntersectionSensorLEFT") {{
                                                                            alignCenter();
                                                                            valignTop();
                                                                            this.focusable(false);
                                                                            this.checked(true);
                                                                            width("50%");
                                                                            height("50%");
                                                                            //this.x("0%");
                                                                        }});

                                                                        control(new CheckboxBuilder("activeIntersectionActuatorLEFT") {{
                                                                            alignCenter();
                                                                            valignTop();
                                                                            this.focusable(false);
                                                                            this.checked(true);
                                                                            width("50%");
                                                                            height("50%");
                                                                            //this.x("0%");
                                                                        }});

                                                                    }});

                                                            }});

                                                        childLayoutHorizontal();

                                                        panel(new PanelBuilder("LeftSemaforController") {
                                                            {
                                                                childLayoutAbsoluteInside();
                                                                width("33%");
                                                                height("100%");
                                                                valignTop();
                                                                alignCenter();

                                                                control(new CheckboxBuilder("activeIntersectionControllerMIDDLE") {{
                                                                    alignCenter();
                                                                    valignCenter();
                                                                    this.focusable(false);
                                                                    this.checked(true);
                                                                    width("40px");
                                                                    height("40px");
                                                                    this.x("9%");
                                                                }});

                                                                panel(new PanelBuilder("sensorsAndActuators") {
                                                                    {
                                                                        //childLayoutAbsoluteInside();
                                                                        //childLayoutHorizontal();
                                                                        childLayoutVertical();
                                                                        width("40px");
                                                                        height("40px");
                                                                        // alignCenter();
                                                                        alignLeft();
                                                                        valignTop();
                                                                        this.x("100%");

                                                                        control(new CheckboxBuilder("activeIntersectionSensorMIDDLE") {{
                                                                            alignCenter();
                                                                            valignTop();
                                                                            this.focusable(false);
                                                                            this.checked(true);
                                                                            width("50%");
                                                                            height("50%");
                                                                            //this.x("0%");
                                                                        }});

                                                                        control(new CheckboxBuilder("activeIntersectionActuatorMIDDLE") {{
                                                                            alignCenter();
                                                                            valignTop();
                                                                            this.focusable(false);
                                                                            this.checked(true);
                                                                            width("50%");
                                                                            height("50%");
                                                                            //this.x("0%");
                                                                        }});

                                                                    }});

                                                            }});

                                                        panel(new PanelBuilder("LeftSemaforController") {
                                                            {
                                                                childLayoutAbsoluteInside();
                                                                width("33%");
                                                                height("100%");
                                                                valignTop();
                                                                alignCenter();

                                                                control(new CheckboxBuilder("activeIntersectionControllerRIGHT") {{
                                                                    alignCenter();
                                                                    valignCenter();
                                                                    this.focusable(false);
                                                                    this.checked(true);
                                                                    width("40px");
                                                                    height("40px");
                                                                    this.x("9%");
                                                                }});

                                                                panel(new PanelBuilder("sensorsAndActuators") {
                                                                    {
                                                                        childLayoutVertical();
                                                                        width("40px");
                                                                        height("40px");
                                                                        alignLeft();
                                                                        valignTop();
                                                                        this.x("100%");

                                                                        control(new CheckboxBuilder("activeIntersectionSensorRIGHT") {{
                                                                            alignCenter();
                                                                            valignTop();
                                                                            this.focusable(false);
                                                                            this.checked(true);
                                                                            width("50%");
                                                                            height("50%");
                                                                            //this.x("0%");
                                                                        }});

                                                                        control(new CheckboxBuilder("activeIntersectionActuatorRIGHT") {{
                                                                            alignCenter();
                                                                            valignTop();
                                                                            this.focusable(false);
                                                                            this.checked(true);
                                                                            width("50%");
                                                                            height("50%");
                                                                            //this.x("0%");
                                                                        }});

                                                                    }});

                                                            }});

                                                    }});


                                                panel(new PanelBuilder("LeftSemaforController") {
                                                    {
                                                        childLayoutAbsoluteInside();
                                                        width("90%");
                                                        height("66px");
                                                        valignTop();
                                                        alignCenter();//

                                                        control(new CheckboxBuilder("activeIntersectionControllerDOWN") {{
                                                            alignCenter();
                                                            valignCenter();
                                                            this.focusable(false);
                                                            this.checked(true);
                                                            width("40px");
                                                            height("40px");
                                                            this.x("34%");
                                                        }});

                                                        panel(new PanelBuilder("sensorsAndActuators") {
                                                            {
                                                                //childLayoutAbsoluteInside();
                                                                //childLayoutHorizontal();
                                                                childLayoutVertical();
                                                                width("40px");
                                                                height("40px");
                                                                // alignCenter();
                                                                alignLeft();
                                                                valignTop();
                                                                this.x("51%");

                                                                control(new CheckboxBuilder("activeIntersectionSensorDOWN") {{
                                                                    alignCenter();
                                                                    valignTop();
                                                                    this.focusable(false);
                                                                    this.checked(true);
                                                                    width("50%");
                                                                    height("50%");
                                                                    //this.x("0%");
                                                                }});

                                                                control(new CheckboxBuilder("activeIntersectionActuatorDOWN") {{
                                                                    alignCenter();
                                                                    valignTop();
                                                                    this.focusable(false);
                                                                    this.checked(true);
                                                                    width("50%");
                                                                    height("50%");
                                                                    //this.x("0%");
                                                                }});

                                                            }});

                                                    }});
                                            }
                                        });
                                    }
                                });


                            }
                        });

                        panel(ConsoleApp);



                    }
                });

            }});
            // </layer>

        }}.build(nifty));

        nifty.subscribe(nifty.getCurrentScreen(), "startMASinit", ButtonClickedEvent.class, eventHandler1);
        nifty.subscribe(nifty.getCurrentScreen(), "carAverageSpeedValue", SliderChangedEvent.class, eventHandler2);
        nifty.subscribe(nifty.getCurrentScreen(), "trafficLightIntervalValue", SliderChangedEvent.class, eventHandler3);
        nifty.subscribe(nifty.getCurrentScreen(), "StartSimulation", ButtonClickedEvent.class, eventHandler4);
        nifty.subscribe(nifty.getCurrentScreen(), "manualMode", ButtonClickedEvent.class, eventHandler5);
        nifty.subscribe(nifty.getCurrentScreen(), "automaticMode", ButtonClickedEvent.class, eventHandler7);

        nifty.subscribe(nifty.getCurrentScreen(), "referintaValue", SliderChangedEvent.class, eventHandler6);

        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionControllerUP", CheckBoxStateChangedEvent.class, eventHandler9);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionSensorUP", CheckBoxStateChangedEvent.class, eventHandler91);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionActuatorUP", CheckBoxStateChangedEvent.class, eventHandler92);

        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionControllerLEFT", CheckBoxStateChangedEvent.class, eventHandler10);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionSensorLEFT", CheckBoxStateChangedEvent.class, eventHandler101);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionActuatorLEFT", CheckBoxStateChangedEvent.class, eventHandler102);

        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionControllerMIDDLE", CheckBoxStateChangedEvent.class, eventHandler11);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionSensorMIDDLE", CheckBoxStateChangedEvent.class, eventHandler111);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionActuatorMIDDLE", CheckBoxStateChangedEvent.class, eventHandler112);

        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionControllerRIGHT", CheckBoxStateChangedEvent.class, eventHandler12);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionSensorRIGHT", CheckBoxStateChangedEvent.class, eventHandler121);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionActuatorRIGHT", CheckBoxStateChangedEvent.class, eventHandler122);

        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionControllerDOWN", CheckBoxStateChangedEvent.class, eventHandler13);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionSensorDOWN", CheckBoxStateChangedEvent.class, eventHandler131);
        nifty.subscribe(nifty.getCurrentScreen(), "activeIntersectionActuatorDOWN", CheckBoxStateChangedEvent.class, eventHandler132);

        nifty.gotoScreen("test");

    }


    // UP INTERSECTION !!!
    // CONTROLLER
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler9 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
             ActiveIntersectionControllers[1] = !ActiveIntersectionControllers[1];
        }
    };

    // SENSOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler91 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionSensors[1] = !ActiveIntersectionSensors[1];
        }
    };

    // ACTUATOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler92 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionActuators[1] = !ActiveIntersectionActuators[1];
        }
    };



    // LEFT INTERSECTION !!!
    // CONTROLLER
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler10 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionControllers[2] = !ActiveIntersectionControllers[2];
        }
    };

    // SENSOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler101 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionSensors[2] = !ActiveIntersectionSensors[2];
        }
    };

    // ACTUATOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler102 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionActuators[2] = !ActiveIntersectionActuators[2];
        }
    };

    // MIDDLE INTERSECTION !!!
    // CONTROLLER
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler11 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionControllers[0] = !ActiveIntersectionControllers[0];
            EventLogEntries.add("TS: "+ (float)((System.currentTimeMillis() - tStart)/1000) + " s-a defectat intersectia centrala.");
            System.out.println("TS: "+ (float)((System.currentTimeMillis() - tStart)/1000) + " s-a defectat intersectia centrala.");
        }
    };

    // SENSOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler111 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionSensors[0] = !ActiveIntersectionSensors[0];
        }
    };

    // ACTUATOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler112 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionActuators[0] = !ActiveIntersectionActuators[0];
        }
    };


    // RIGHT INTERSECTION !!!
    // CONTROLLER
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler12 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionControllers[4] = !ActiveIntersectionControllers[4];
        }
    };

    // SENSOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler121 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionSensors[4] = !ActiveIntersectionSensors[4];
        }
    };

    // ACTUATOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler122 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionActuators[4] = !ActiveIntersectionActuators[4];
        }
    };


    // DOWN INTERSECTION !!!
    // CONTROLLER
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler13 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionControllers[3] = !ActiveIntersectionControllers[3];
        }
    };

    // SENSOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler131 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionSensors[3] = !ActiveIntersectionSensors[3];
        }
    };

    // ACTUATOR
    EventTopicSubscriber<CheckBoxStateChangedEvent> eventHandler132 = new EventTopicSubscriber<CheckBoxStateChangedEvent>() {
        @Override
        public void onEvent(String s, CheckBoxStateChangedEvent checkBoxStateChangedEvent) {
            ActiveIntersectionActuators[3] = !ActiveIntersectionActuators[3];
        }
    };

    EventTopicSubscriber<ButtonClickedEvent> eventHandler1 = new EventTopicSubscriber<ButtonClickedEvent>() {
        @Override
        public void onEvent(String s, ButtonClickedEvent checkBoxStateChangedEvent) {
            startApplication = true;
        }
    };

    EventTopicSubscriber<SliderChangedEvent> eventHandler2 = new EventTopicSubscriber<SliderChangedEvent>() {
        @Override
        public void onEvent(final String topic, final SliderChangedEvent event) {
            carSpeed = (int) nifty.getCurrentScreen().findNiftyControl("carAverageSpeedValue", Slider.class).getValue();
            String value = String.valueOf(carSpeed);
            nifty.getCurrentScreen().findNiftyControl("carAverageSpeed", Button.class).setText("Viteza medie de circulatie: " + value);
        }
    };

    EventTopicSubscriber<SliderChangedEvent> eventHandler3 = new EventTopicSubscriber<SliderChangedEvent>() {
        @Override
        public void onEvent(final String topic, final SliderChangedEvent event) {
            cicleInterval = (int) nifty.getCurrentScreen().findNiftyControl("trafficLightIntervalValue", Slider.class).getValue();
            String value = String.valueOf(cicleInterval);
            cicleInterval = cicleInterval * 1000;
            nifty.getCurrentScreen().findNiftyControl("trafficLightInterval", Button.class).setText("Interval semafor: " + value);
        }
    };

    EventTopicSubscriber<ButtonClickedEvent> eventHandler4 = new EventTopicSubscriber<ButtonClickedEvent>() {
        @Override
        public void onEvent(final String topic, final ButtonClickedEvent event) {

            normalCycleTimer[0] = false;
            changedUISpeed = !changedUISpeed;
        }
    };

    EventTopicSubscriber<ButtonClickedEvent> eventHandler5 = new EventTopicSubscriber<ButtonClickedEvent>() {
        @Override
        public void onEvent(final String topic, final ButtonClickedEvent event) {
            CoreAgent.automaticMode = false;
            if(tStart == -1) {
                EventLogEntries.add("Initializare: Modul controler este manual.");
            }
            else {
                EventLogEntries.add("Timp de simulare: "+ String.valueOf((System.currentTimeMillis() - tStart)/1000) + ". Modul controler este manual.");
            }

            nifty.getCurrentScreen().findNiftyControl("manualMode", Button.class).setFocusable(!CoreAgent.automaticMode);
            nifty.getCurrentScreen().findNiftyControl("automaticMode", Button.class).setFocusable(CoreAgent.automaticMode);

        }
    };

    EventTopicSubscriber<ButtonClickedEvent> eventHandler7 = new EventTopicSubscriber<ButtonClickedEvent>() {
        @Override
        public void onEvent(final String topic, final ButtonClickedEvent event) {
            CoreAgent.automaticMode = true;
            if(tStart == -1) {
                EventLogEntries.add("Initializare: Modul controler este automat.");
            }
            else {
                EventLogEntries.add("TS: " + String.valueOf((System.currentTimeMillis() - tStart)/1000) + ". Modul controler este automat.");
            }

            nifty.getCurrentScreen().findNiftyControl("manualMode", Button.class).setFocusable(!CoreAgent.automaticMode);
            nifty.getCurrentScreen().findNiftyControl("automaticMode", Button.class).setFocusable(CoreAgent.automaticMode);
            nifty.getCurrentScreen().findNiftyControl("manualMode", Button.class).setFocusable(!CoreAgent.automaticMode);
            nifty.getCurrentScreen().findNiftyControl("automaticMode", Button.class).setFocusable(CoreAgent.automaticMode);

        }
    };

    EventTopicSubscriber<SliderChangedEvent> eventHandler6 = new EventTopicSubscriber<SliderChangedEvent>() {
        @Override
        public void onEvent(final String topic, final SliderChangedEvent event) {
            CoreAgent.GlobalNucleusSetPoint = (int) nifty.getCurrentScreen().findNiftyControl("referintaValue", Slider.class).getValue();
            String value = String.valueOf(CoreAgent.GlobalNucleusSetPoint);
            nifty.getCurrentScreen().findNiftyControl("referintaLabel", Button.class).setText("Referinta Nucleu Global: " + CoreAgent.GlobalNucleusSetPoint);
        }
    };

    public void cameraSetup() {
        cam.setFrustumFar(4000);
        cam.onFrameChange();
        flyCam.setMoveSpeed(10);
    }

    public void setMap() {
        assetManager.registerLocator("src/assets/Models/map.zip", ZipLocator.class);
        map = assetManager.loadModel("map.mesh.j3o");
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
        hudText.setColor(Green);           // the text
        hudText.setLocalTranslation(25, offset, 300); // position
        guiNode.attachChild(hudText);

        setDisplayStatView(true);
    }

    public void setSun() {


//        for(int i=0;i<1;i++) // GOOD TRAFFIC LIGHT INIT !!!
//            for(int j=0;j<4;j++) {
//
//                trafficLightSpots[i][j] = new SpotLight();
//                trafficLightSpots[i][j].setSpotRange(10f);                           // distance
//                trafficLightSpots[i][j].setSpotInnerAngle(150); // inner light cone (central beam)
//                trafficLightSpots[i][j].setSpotOuterAngle(150); // outer light cone (edge of the light)
//                trafficLightSpots[i][j].setColor(ColorRGBA.Red);         // light color
//
//                trafficLightSpots[i][j].setPosition(trafficLightLocations[i][j].add(0, 2, 0));               // shine from camera loc
//                trafficLightSpots[i][j].setDirection(new Vector3f(200f, 0, 100));//trafficLightLocations[0].add(70,2,22));//new Vector3f(trafficLightLocations[0].add(4,1,0)));             // shine forward from camera loc
//
//                rootNode.addLight(trafficLightSpots[i][j]);
//            }

        // Snapshot only code block

                trafficLightSpots[0][0] = new SpotLight();
                trafficLightSpots[0][0].setSpotRange(10f);                           // distance
                trafficLightSpots[0][0].setSpotInnerAngle(150); // inner light cone (central beam)
                trafficLightSpots[0][0].setSpotOuterAngle(150); // outer light cone (edge of the light)
                trafficLightSpots[0][0].setColor(ColorRGBA.Green);         // light color

                trafficLightSpots[0][0].setPosition(trafficLightLocations[0][0].add(0, 2, 0));               // shine from camera loc
                trafficLightSpots[0][0].setDirection(new Vector3f(200f, 0, 100));//trafficLightLocations[0].add(70,2,22));//new Vector3f(trafficLightLocations[0].add(4,1,0)));             // shine forward from camera loc

                rootNode.addLight(trafficLightSpots[0][0]);



                trafficLightSpots[0][1] = new SpotLight();
                trafficLightSpots[0][1].setSpotRange(10f);                           // distance
                trafficLightSpots[0][1].setSpotInnerAngle(150); // inner light cone (central beam)
                trafficLightSpots[0][1].setSpotOuterAngle(150); // outer light cone (edge of the light)
                trafficLightSpots[0][1].setColor(ColorRGBA.Red);         // light color

                trafficLightSpots[0][1].setPosition(trafficLightLocations[0][1].add(0, 2, 0));               // shine from camera loc
                trafficLightSpots[0][1].setDirection(new Vector3f(-90f, 0, 122));//trafficLightLocations[0].add(70,2,22));//new Vector3f(trafficLightLocations[0].add(4,1,0)));             // shine forward from camera loc

                rootNode.addLight(trafficLightSpots[0][1]);


                trafficLightSpots[0][2] = new SpotLight();
                trafficLightSpots[0][2].setSpotRange(10f);                           // distance
                trafficLightSpots[0][2].setSpotInnerAngle(150); // inner light cone (central beam)
                trafficLightSpots[0][2].setSpotOuterAngle(150); // outer light cone (edge of the light)
                trafficLightSpots[0][2].setColor(ColorRGBA.Green);         // light color

                trafficLightSpots[0][2].setPosition(trafficLightLocations[0][2].add(0, 2, 0));               // shine from camera loc
                trafficLightSpots[0][2].setDirection(new Vector3f(-200f, 0, -26));//trafficLightLocations[0].add(70,2,22));//new Vector3f(trafficLightLocations[0].add(4,1,0)));             // shine forward from camera loc

                rootNode.addLight(trafficLightSpots[0][2]);


                trafficLightSpots[0][3] = new SpotLight();
                trafficLightSpots[0][3].setSpotRange(10f);                           // distance
                trafficLightSpots[0][3].setSpotInnerAngle(150); // inner light cone (central beam)
                trafficLightSpots[0][3].setSpotOuterAngle(150); // outer light cone (edge of the light)
                trafficLightSpots[0][3].setColor(ColorRGBA.Red);         // light color

                trafficLightSpots[0][3].setPosition(trafficLightLocations[0][3].add(0, 2, 0));               // shine from camera loc
                trafficLightSpots[0][3].setDirection(new Vector3f(-55, 0, -150f));//trafficLightLocations[0].add(70,2,22));//new Vector3f(trafficLightLocations[0].add(4,1,0)));             // shine forward from camera loc

                rootNode.addLight(trafficLightSpots[0][3]);




        ColorRGBA sun_central_color = new ColorRGBA();
        sun_central_color.set(255 / 255f, 255 / 255f, 251 / 255f, 0.5f);
        PointLight sun_central = new PointLight();
//        sun_central.setColor(sun_central_color);
//        sun_central.setRadius(2000f);
//        sun_central.setPosition(new Vector3f(0.0f, 400.0f, 0.0f));

        PointLight sun_up = new PointLight();
        sun_up.setColor(sun_central_color);
        sun_up.setRadius(10000f);
        sun_up.setPosition(new Vector3f(0.0f, 1000.0f, 0.0f));

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
        //rays.get(numberOfCars++).setLimit(20);
    }

    public void SetTrafficLights(int state) {
        SpotLight sl = new SpotLight();
        SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, 1000);
        SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager, 1000);

        sl.setColor(Red);
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
        inputManager.addMapping("useSingleLaneState", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("usePairedLaneState", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("controlledNegociation", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("unControlledNegociation", new KeyTrigger(KeyInput.KEY_3));




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
        inputManager.addMapping("Teleport", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

        inputManager.addListener(this, "Teleport");
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
        inputManager.addListener(this, "gui");
        inputManager.addListener(this, "useSingleLaneState");
        inputManager.addListener(this, "usePairedLaneState");
        inputManager.addListener(this, "controlledNegociation");
        inputManager.addListener(this, "unControlledNegociation");
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
                vehicle.setPhysicsLocation(new Vector3f(98, 0, 81));
                vehicle.setPhysicsRotation(new Matrix3f());
                vehicle.setLinearVelocity(Vector3f.ZERO);
                vehicle.setAngularVelocity(Vector3f.ZERO);
                vehicle.resetSuspension();
                getPhysicsSpace().add(vehicle);
                rootNode.attachChild(carNode);
            } else {
            }
        }
        else if (binding.equals("Teleport"))
        {
           // player.setPhysicsLocation(cam.getLocation());
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
        else if (binding.equals("gui")) {
            if (isPressed) {
                gui=!gui;
            }
        }
        else if (binding.equals("useSingleLaneState")) { // if the controller has access to act paired lane traffic light
            if (isPressed) {
                controllerPairedState=false;
                if(tStart == -1) {
                    EventLogEntries.add("Init: Modul controler este setat pe un singur semafor.");
                }
                else {
                    EventLogEntries.add("TS: "+ String.valueOf((System.currentTimeMillis() - tStart)/1000) + ". Modul controler este setat pe un singur semafor.");
                }
//                EventLogEntries.add("Controller-ul este setat pe un singur semafor.");
            }
        }
        else if (binding.equals("usePairedLaneState")) { // if the controller has access to act paired lane traffic light
            if (isPressed) {
                controllerPairedState=true;
                if(tStart == -1) {
                    EventLogEntries.add("Init: Modul controler este setat pe doua semafoare.");
                }
                else {
                    long currentSimulationTime = System.currentTimeMillis() - tStart;
                    if(currentSimulationTime <= 1000)
                        currentSimulationTime = currentSimulationTime % 1000;
                    EventLogEntries.add("TS :"+ String.valueOf((System.currentTimeMillis() - tStart)/1000) + ". Modul controler este setat pe doua semafoare.");
                }
            }
        }else if (binding.equals("controlledNegociation")) { // if the controller has access to act paired lane traffic light
            if (isPressed) {
                uncontrolledNegociation=false;
                if(tStart == -1) {
                    EventLogEntries.add("Init: Nucleu: Negocierea controlata activata.");
                }
                else {
                    long currentSimulationTime = System.currentTimeMillis() - tStart;
                    if(currentSimulationTime <= 1000)
                        currentSimulationTime = currentSimulationTime % 1000;
                    EventLogEntries.add("TS :"+ String.valueOf((System.currentTimeMillis() - tStart)/1000) + ". Nucleu: Negocierea controlata activata..");
                }
            }
        }else if (binding.equals("unControlledNegociation")) { // if the controller has access to act paired lane traffic light
            if (isPressed) {
                uncontrolledNegociation=true;
                if(tStart == -1) {
                    EventLogEntries.add("Init: Nucleu: Negocierea necontrolata activata.");
                }
                else {
                    long currentSimulationTime = System.currentTimeMillis() - tStart;
                    if(currentSimulationTime <= 1000)
                        currentSimulationTime = currentSimulationTime % 1000;
                    EventLogEntries.add("TS :"+ String.valueOf((System.currentTimeMillis() - tStart)/1000) + ". Nucleu: Negocierea necontrolata activata.");
                }
            }
        }

    }

    private void buildVehicle(String modelPath, Vector3f physLocatcation, Matrix3f rotation) {
        float stiffness = 120.0f;//200=f1 car
        float compValue = 0.2f; //(lower than damp!)
        float dampValue = 0.3f;
        final float mass = 400;

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
        //rays.get(numberOfCars++).setLimit(20);

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

    @Override
    public void destroy() {
        super.destroy();

        long currentTimeElapse = (System.currentTimeMillis() - Helper.tStart) / 1000;

        System.out.println( "Running simulation time is: " + currentTimeElapse + " s");
    }
}