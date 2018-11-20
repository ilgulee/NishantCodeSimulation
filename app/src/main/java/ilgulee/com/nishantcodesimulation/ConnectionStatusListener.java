package ilgulee.com.nishantcodesimulation;

/**
 * Created by Nishant Rajput on 19/10/18.
 */
public interface ConnectionStatusListener {
    void onDisconnected();

    void onConnected(String videoUrl);
}
