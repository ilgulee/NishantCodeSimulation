package ilgulee.com.nishantcodesimulation;

import com.jcraft.jsch.UserInfo;

/**
 * Created by Nishant Rajput on 19/10/18.
 */

public class SessionUserInfo implements UserInfo {

    private final String mPassword;
    private final String mUser;
    private final String mHost;
    private final int mPort;

    public SessionUserInfo(String user, String host, String password, int port) {

        mUser = user;
        mHost = host;
        mPassword = password;
        mPort = port;
    }

    public String getPassphrase() {
        // TODO
        return null;
    }

    public String getUser() {
        return mUser;
    }

    public String getHost() {
        return mHost;
    }

    public String getPassword() {
        return mPassword;
    }

    public int getPort() {
        return mPort;
    }

    public boolean promptPassphrase(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean promptPassword(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean promptYesNo(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    public void showMessage(String arg0) {
        // TODO Auto-generated method stub
    }


}
