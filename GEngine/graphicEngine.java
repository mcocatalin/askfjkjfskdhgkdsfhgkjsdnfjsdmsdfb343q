package GEngine;

import Controlling.VehicleController;
import Utility.IntersectionItem;
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
import de.lessvoid.nifty.controls.slider.builder.SliderBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import org.bushe.swing.event.EventTopicSubscriber;

import java.util.*;

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
    public Node[] trafficLights;

    public LinkedList<BoundingSphere> bounds = new LinkedList<BoundingSphere>();

    public LinkedList<Ray> rays = new LinkedList<Ray>();

    public static int noOfCars = 0;

    public Vector3f trafficLightLocations[] =
            {
                    new Vector3f(33f, 18.257574f, -97f), // first intersection
                    new Vector3f(50f, 18.257574f, -97f),
                    new Vector3f(50f, 18.257574f, -114f),
                    new Vector3f(33f, 18.257574f, -114f),
                    new Vector3f(33f, 18.257574f, -97f),

                    new Vector3f(63f, 18.257574f, -97f), // second intersection
                    new Vector3f(50f, 48.257574f, -97f),
                    new Vector3f(50f, 18.257574f, -84f),
                    new Vector3f(93f, 18.257574f, -114f),
                    new Vector3f(33f, 18.257574f, -97f),
            };

    private static int indexTrafficLight = 0;

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

    public LinkedList<IntersectionItem> Semafoare;

    // Communication members
    public static List<actingHandler> request = new ArrayList<actingHandler>();

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

    VehicleController vc;

    private Vector3f posDir = new Vector3f();
    private Vector3f upDirVehicle = new Vector3f();
    private Vector3f dir = new Vector3f();
    private Vector3f targetPosDir = new Vector3f();

    public static void main(String[] args) {

    }

    public int GetIntersectionLocationDensity(Vector3f Location){
        // !!!!!!!!!!!!!!!!!! Check number of cars in range!!!
        return 0;
    }


    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        setMap();
        setHudText();
        setSun();
        load_sky();
        buildVehicle(modelPaths[0], valideLocations[5], valideRotations[3]);

        flyCam.setEnabled(true);
        setDisplayFps(false);
        setDisplayStatView(false);



        cameraSetup();
        load_player();


        setUpKeys();

    }

    private void load_sky(){
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false);
        sky.setLocalScale(1000);
        rootNode.attachChild(sky);
        rootNode.setShadowMode(RenderQueue.ShadowMode.Off);
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
            vehicle.accelerate(-speed * speedmult);
            vehicle.brake(0);
        }
    }

    public void CarGoTo(Vector3f destination) {

//        vehicle.getPhysicsLocation(vector1);
//        vehicle.getForwardVector(vector3);
//        vector2.set(destination);
//        float distance = vector1.distance(destination);
//        float checkRadius = 10;
////        if (distance <= checkRadius) {
////            //moving = false;
////            vehicle.accelerate(0);
////            vehicle.brake(100);
////        } else {
//            System.out.println("Rotatie :" + vehicle.getPhysicsRotationMatrix());
//            System.out.println("Locatie :" + vehicle.getPhysicsLocation());
//            plane.setOriginNormal(destination, vector1);
//            // plane.setOriginNormal(map.getWorldTranslation(), vector4);
//            float dot = 1 - vector3.dot(vector2);
//            float angle = vector3.angleBetween(destination);
//            if (angle > FastMath.QUARTER_PI) {
//                angle = FastMath.QUARTER_PI;
//            }
//            float anglemult = 1;//FastMath.PI / 4.0f;
//            float speedmult = 0.3f;//0.3f;
//            ROTATE_RIGHT.multLocal(vector3);
//            if (plane.whichSide(vector3) == Plane.Side.Negative) {
//                anglemult *= -1;
//            }
//
//            if (dot > 1) {
//                speedmult *= -1;
//                anglemult *= -1;
//            }
//
//            vehicle.steer(angle * anglemult);
//            vehicle.accelerate(speed * speedmult);
//            vehicle.brake(0);

        //}

        vehicle.getPhysicsLocation(vector1);
        vector2.set(destination);
        vector2.subtractLocal(vector1);


        vector2.normalizeLocal();

        posDir.set(vehicle.getPhysicsLocation());
        upDirVehicle.set(new Vector3f(0, 1, 0));
        dir.set(vector2);
        targetPosDir.set(destination);


        Vector3f left = upDirVehicle.cross(dir);  // might be dir.cross(upDirVehicle)

        Vector3f targetRelative = targetPosDir.subtract(posDir).normalizeLocal();

        float steer = left.dot(targetRelative);
        float forward = dir.dot(targetRelative);

        if (forward < 0) steer *= -1;

        vehicle.steer(-200 * steer);
        vehicle.accelerate(-200 * forward);

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

        if (!request.isEmpty()) {
            actingHandler x = request.get(0);
            if (x.type == "vehicleMovement") {
                vehicle.accelerate(40);
            }
        }

        Vector3f destination = valideLocations[8];
        //CarGoTo(destination);
        CarMoveAt(destination);
    }

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
