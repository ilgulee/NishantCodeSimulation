package ilgulee.com.nishantcodesimulation;

/**
 * Created by Nishant Rajput on 19/10/18.
 */
public interface ExecTaskCallbackHandler {
    void onFail();

    void onComplete(String completeString);
}
