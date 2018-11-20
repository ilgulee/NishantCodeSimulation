package ilgulee.com.nishantcodesimulation;

import android.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Nishant Rajput on 20/10/18.
 */

public class SessionController {

    private static final String TAG = SessionController.class.getName();
    private Session mSession;
    private SessionUserInfo mSessionUserInfo;
    private Thread mThread;
    private SftpController mSftpController;
    private ConnectionStatusListener mConnectStatusListener;
    private static SessionController sSessionController;
    private String mVideoUrl;

    private SessionController() {
    }

    public static SessionController getSessionController() {
        if (sSessionController == null) {
            sSessionController = new SessionController();
        }
        return sSessionController;
    }

    public Session getSession() {
        return mSession;
    }

    private SessionController(SessionUserInfo sessionUserInfo) {
        mSessionUserInfo = sessionUserInfo;
        connect();
    }

    public static boolean exists() {
        return sSessionController != null;
    }

    public static boolean isConnected() {
        Log.v(TAG, "session controller exists... " + exists());
        if (exists()) {
            Log.v(TAG, "disconnecting");
            if (getSessionController().getSession().isConnected())
                return true;
        }
        return false;
    }

    public void setUserInfo(SessionUserInfo sessionUserInfo) {
        mSessionUserInfo = sessionUserInfo;
    }

    public SessionUserInfo getSessionUserInfo() {
        return mSessionUserInfo;
    }

    public void connect() {
        if (mSession == null) {
            mThread = new Thread(new SshRunnable());
            mThread.start();
        } else if (!mSession.isConnected()) {
            mThread = new Thread(new SshRunnable());
            mThread.start();
        }
    }

    public SftpController getSftpController() {
        return mSftpController;
    }


    public void setConnectionStatusListener(ConnectionStatusListener csl) {
        mConnectStatusListener = csl;
    }


    public void uploadFiles(File[] files, SftpProgressMonitor spm) {
        if (mSftpController == null) {
            mSftpController = new SftpController();

        }
        mSftpController.new UploadTask(mSession, files, spm).execute();
    }

    public void disconnect() throws IOException {

        if (mSession != null) {
            if (mSftpController != null) {
                mSftpController.disconnect();
            }

            synchronized (mConnectStatusListener) {
                if (mConnectStatusListener != null) {
                    mConnectStatusListener.onDisconnected();
                }
            }

            mSession.disconnect();
        }
        if (mThread != null && mThread.isAlive()) {
            try {
                mThread.join();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        mSftpController = null;
    }

    public void setVideoUrl(String url) {
        mVideoUrl = url;
    }

    public class SshRunnable implements Runnable {

        public void run() {
            JSch jsch = new JSch();
            mSession = null;
            try {
                mSession = jsch.getSession(mSessionUserInfo.getUser(), mSessionUserInfo.getHost(),
                        mSessionUserInfo.getPort()); // port 22

                mSession.setUserInfo(mSessionUserInfo);

                Properties properties = new Properties();
                properties.setProperty("StrictHostKeyChecking", "no");
                mSession.setConfig(properties);
                mSession.connect();

            } catch (JSchException jex) {
                Log.e(TAG, "JschException: " + jex.getMessage() +
                        ", Fail to get session " + mSessionUserInfo.getUser() +
                        ", " + mSessionUserInfo.getHost());
            } catch (Exception ex) {
                Log.e(TAG, "Exception:" + ex.getMessage());
            }

            Log.d("SessionController", "Session connected? " + mSession.isConnected());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        //keep track of connection status
                        try {
                            Thread.sleep(2000);
                            if (mConnectStatusListener != null) {
                                if (mSession.isConnected()) {
                                    mConnectStatusListener.onConnected(mVideoUrl);
                                } else mConnectStatusListener.onDisconnected();
                            }
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }).start();
        }
    }
}
