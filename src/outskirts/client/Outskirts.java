package outskirts.client;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import outskirts.client.audio.AudioEngine;
import outskirts.client.audio.AudioSource;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiScreen;
import outskirts.client.gui.screen.GuiScreenInGame;
import outskirts.client.gui.screen.GuiScreenRoot;
import outskirts.client.render.Camera;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.entity.Entity;
import outskirts.entity.EntityPlayer;
import outskirts.entity.EntityStall;
import outskirts.event.Events;
import outskirts.event.client.DisplayResizedEvent;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.event.client.input.MouseEvent;
import outskirts.event.world.block.BlockChangedEvent;
import outskirts.init.Blocks;
import outskirts.init.Bootstrap;
import ext.testing.CPacketStrMsg;
import outskirts.network.ChannelHandler;
import outskirts.physics.dynamic.RigidBody;
import outskirts.physics.dynamic.forcegen.BoosterSpring;
import outskirts.server.OutskirtsServer;
import outskirts.server.ServerSettings;
import outskirts.util.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;
import outskirts.world.World;
import outskirts.world.chunk.Octree;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import static org.lwjgl.opengl.GL11.*;

public class Outskirts {

    private int mouseDWheel;
    private int mouseDY;
    private int mouseDX;
    private int mouseY;
    private int mouseX;

    private static Outskirts INSTANCE;

    public static RenderEngine renderEngine;

    private AudioEngine audioEngine;

    private boolean running;

    private GuiScreenRoot rootGUI = new GuiScreenRoot();

    private World world;

    private Camera camera = new Camera();
    private GameTimer timer = new GameTimer();
    private RayPicker rayPicker = new RayPicker();

    private EntityPlayer player;


    private final Queue<Runnable> scheduledTasks = new LinkedList<>();

    public void run() {
        try
        {
            this.startGame();

            while (this.running)
            {
                this.runGameLoop();
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            this.destroy();
        }
    }

    private void startGame() throws Throwable {

        this.running = true;

        Outskirts.INSTANCE = this;

        GameSettings.loadOptions();

        this.createDisplay();

        renderEngine = new RenderEngine();

        {   //draw splash screen
            Gui.drawRect(Colors.DARK_BLUE, 0, 0, getWidth(), getHeight());
            Gui.drawString("INIT PHASE..", 16, getHeight() - 80, Colors.BLUE, 32);
            this.updateDisplay(); // also use for init RenderEngine.projectionMatrix
        }

        audioEngine = new AudioEngine();

        displayScreen(null);

        Bootstrap.register();

        world = new World();


//        OutskirtsServer.startServerThread(new OutskirtsServer());

        player = new EntityPlayer();
        world.addEntity(player);

        player.getRigidBody().getGravity().setY(0);

        EntityStall entityStall = new EntityStall();
        world.addEntity(entityStall);

        RigidBody rigidBody = entityStall.getRigidBody();
        rigidBody.setMass(0.2f);

        Events.EVENT_BUS.register(KeyboardEvent.class, e -> {
            if (e.getKey() == Keyboard.KEY_F)
                rigidBody.totalForces().add(0, 1000 * rigidBody.getMass(), 0);
            if (e.getKey() == Keyboard.KEY_R)
                rigidBody.getTransform().origin.set(0,0,0);
            if (e.getKey() == Keyboard.KEY_T)
                rigidBody.getAngularVelocity().add(0, 1, 0);
        });

        rigidBody.attachBooster(new BoosterSpring(Vector3f.ZERO));

//        ChannelHandler channelHandler = ChannelHandler.createConnection("127.0.0.1", ServerSettings.SERVER_PORT);

    }
    //should blockPos.w be depth or size ..?

    private void runGameLoop() throws Throwable {

        if (Display.isCloseRequested()) {
            this.running = false;
        }

        this.timer.update();

        synchronized (scheduledTasks) {
            while (!scheduledTasks.isEmpty()) {
                scheduledTasks.poll().run();
            }
        }

        while (timer.pollFullTick())
        {
            this.runTick();
        }

        this.handleInput();


        camera.update(player);

        rayPicker.update();

        renderEngine.prepare();

        renderEngine.render(world);


        glDisable(GL_DEPTH_TEST);
        rootGUI.onDraw();
        glEnable(GL_DEPTH_TEST);

        this.updateDisplay();
    }

    private void runTick() {

        if (currentScreen() == null) {
            float delta = 1f / GameTimer.TPS;

            Vector3f ownerRotation = player.getRotation();
            ownerRotation.y += -Outskirts.getMouseDX();
            ownerRotation.x += -Outskirts.getMouseDY();
            ownerRotation.x = Maths.clamp(ownerRotation.x, -90, 90);

            if (GameSettings.KEY_WALK_FORWARD.isKeyDown()) {
                player.walkStep(delta, 0);
            }
            if (GameSettings.KEY_WALK_BACKWARD.isKeyDown()) {
                player.walkStep(delta, 180);
            }
            if (GameSettings.KEY_WALK_LEFT.isKeyDown()) {
                player.walkStep(delta, 90);
            }
            if (GameSettings.KEY_WALK_RIGHT.isKeyDown()) {
                player.walkStep(delta, -90);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                player.getRigidBody().getLinearVelocity().y += 1f;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                player.getRigidBody().getLinearVelocity().y -= 1f;
            }
        }

        world.onTick();

        //update visible chunks provides
        int distanceChunks = GameSettings.VIEW_DISTANCE_CHUNKS;
        for (int xOffset = -distanceChunks;xOffset <= distanceChunks;xOffset++) {
            for (int zOffset = -distanceChunks;zOffset <= distanceChunks;zOffset++) {
                world.provideChunk(Maths.floor(camera.getPosition().x/16) + xOffset, Maths.floor(camera.getPosition().z/16) + zOffset);
            }
        }
    }

    private void destroy() {

        GameSettings.saveOptions();

//        OutskirtsServer.shutdown();

        audioEngine.destroy();

        Display.destroy();
    }

    private void handleInput() {
        mouseDWheel = 0;
        mouseDY = 0;
        mouseDX = 0;

        while (Mouse.next()) {
            mouseDWheel = Mouse.getEventDWheel();
            mouseDY = -Mouse.getEventDY();
            mouseDX = Mouse.getEventDX();
            mouseY = Display.getHeight() - Mouse.getEventY();
            mouseX = Mouse.getEventX();

            Events.EVENT_BUS.post(new MouseEvent());

            KeyBinding.postInput(Mouse.getEventButton(), Mouse.getEventButtonState(), KeyBinding.TYPE_MOUSE);

            rootGUI.onMouse(Mouse.getEventButton(), Mouse.getEventButtonState());
        }

        while (Keyboard.next()) {

            Events.EVENT_BUS.post(new KeyboardEvent());

            KeyBinding.postInput(Keyboard.getEventKey(), Keyboard.getEventKeyState(), KeyBinding.TYPE_KEYBOARD);

            rootGUI.onKeyboard(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState());
        }
    }

    @Nullable
    public static GuiScreen currentScreen() {
        return INSTANCE.rootGUI.getCurrentScreen();
    }

    public static void displayScreen(@Nullable GuiScreen screen) {

        Mouse.setGrabbed(screen == null);
//        Keyboard.enableRepeatEvents(screen != null);

        INSTANCE.rootGUI.setCurrentScreen(screen);
    }

    public static void addScheduledTask(Runnable runnable) {
        synchronized (INSTANCE.scheduledTasks) {
            INSTANCE.scheduledTasks.add(runnable);
        }
    }

    public static GuiScreenInGame getIngameGUI() {
        return (GuiScreenInGame)INSTANCE.rootGUI.getIngameGUI();
    }

    public static RayPicker getRayPicker() {
        return INSTANCE.rayPicker;
    }

    public static World getWorld() {
        return INSTANCE.world;
    }

    public static float getDelta() {
        return INSTANCE.timer.getDelta();
    }

    public static Camera getCamera() {
        return INSTANCE.camera;
    }

    public static int getDWheel() {
        return INSTANCE.mouseDWheel;
    }

    public static int getMouseDX() {
        return INSTANCE.mouseDX;
    }

    public static int getMouseDY() {
        return INSTANCE.mouseDY;
    }

    public static int getMouseX() {
        return (int) (INSTANCE.mouseX / GameSettings.GUI_SCALE);
    }

    public static int getMouseY() {
        return (int) (INSTANCE.mouseY / GameSettings.GUI_SCALE);
    }

    public static int getWidth() {
        return (int) (Display.getWidth() / GameSettings.GUI_SCALE);
    }

    public static int getHeight() {
        return (int) (Display.getHeight() / GameSettings.GUI_SCALE);
    }

    private void updateDisplay() {

        if (Display.wasResized()) {
            Events.EVENT_BUS.post(new DisplayResizedEvent());

            glViewport(0, 0, Display.getWidth(), Display.getHeight());

            renderEngine.setProjectionMatrix(Maths.createPerspectiveProjectionMatrix(GameSettings.FOV, Display.getWidth(), Display.getHeight(), GameSettings.NEAR_PLANE, GameSettings.FAR_PLANE));

            rootGUI.onResize();
        }

        Display.sync(GameSettings.FPS_CAPACITY);
        Display.update();
    }

    private void createDisplay() throws IOException, LWJGLException {

        if (!SystemUtils.IS_OS_MAC)
        {
            Display.setIcon(new ByteBuffer[] {
                    Loader.loadTextureData(ImageIO.read(new ResourceLocation("textures/gui/icons/icon_16x16.png").getInputStream()), false),
                    Loader.loadTextureData(ImageIO.read(new ResourceLocation("textures/gui/icons/icon_32x32.png").getInputStream()), false)
            });
        }

        Display.setDisplayMode(new DisplayMode(GameSettings.ProgramArguments.WIDTH, GameSettings.ProgramArguments.HEIGHT));
        Display.setTitle("ENGINE");
        Display.setResizable(true);
        Display.setVSyncEnabled(GameSettings.ENABLE_VSYNC);

        try
        {
            Display.create(new PixelFormat().withDepthBits(24));
        }
        catch (LWJGLException ex)
        {
            Log.warn("Can't create display with 24 depthBits");

            Display.create();
        }
    }
}
