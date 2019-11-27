package outskirts.server;

import outskirts.network.ChannelHandler;
import outskirts.util.GameTimer;
import outskirts.util.SystemUtils;
import outskirts.util.logging.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OutskirtsServer implements Runnable {

    private static OutskirtsServer INSTANCE;

    private boolean running;

    @Override
    public void run() {
        try
        {
            this.startServer();

            while (this.running)
            {
                this.runTick();

                this.sync(GameTimer.TPS);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            this.stopServer();
        }
    }

    private void startServer() throws InterruptedException {

        this.running = true;

        INSTANCE = this;

        Log.info("Starting server in port %s", ServerSettings.SERVER_PORT);

        ChannelHandler.bindServerEndpoint(ServerSettings.SERVER_PORT);

        this.startConsoleInputThread();

    }

    private void runTick() {



    }

    private void stopServer() {
        Log.info("Stopping server...");



        Log.info("Server stopped.");
    }

    public static void shutdown() {
        INSTANCE.running = false;
    }


    public static void startServerThread(OutskirtsServer server) {
        new Thread(server, "Server Thread").start();
    }

    private void startConsoleInputThread() {
        Thread thread = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line;
                while ((line = br.readLine()) != null) {

                    Log.info("InputLine: " + line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "ServerConsoleInputThread");
        thread.setDaemon(true);
        thread.start();
    }

    private long PREV_SYNC = -1;
    private void sync(long tps) throws InterruptedException {
        long FIXED_DELTA_MILLIS = 1000 / tps; // 1000/20=50

        long currentTime = System.currentTimeMillis();
        if (PREV_SYNC == -1)
            PREV_SYNC = currentTime;
        long deltaMillis = currentTime - PREV_SYNC;
        PREV_SYNC = currentTime;

        if (deltaMillis > FIXED_DELTA_MILLIS*2) {
            long overload = deltaMillis - FIXED_DELTA_MILLIS;
            Log.warn("Server overload! tick over %sms (used %sms), skipping %s tick(s).", overload, deltaMillis, overload/FIXED_DELTA_MILLIS);
        }

        SystemUtils.nanoSleep(Math.max((FIXED_DELTA_MILLIS - deltaMillis) * 1_000_000L, 0));
    }

}
