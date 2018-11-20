package ilgulee.com.nishantcodesimulation;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nishant Rajput on 20/10/18.
 */

public class SftpController {

    public static final String TAG = SftpController.class.getName();

    public SftpController() {

    }

    public void disconnect() {
        if (SessionController.isConnected()) {
            try {
                SessionController.getSessionController().disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class UploadTask extends AsyncTask<Void, Void, Boolean> {

        private Session mSession;
        private SftpProgressMonitor mProgressDialog;
        private File[] mLocalFiles;

        public UploadTask(Session session, File[] localFiles, SftpProgressMonitor spd) {

            mProgressDialog = spd;
            mLocalFiles = localFiles;
            mSession = session;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            boolean success = true;
            try {
                uploadFiles(mSession, mLocalFiles, mProgressDialog);
            } catch (JSchException e) {
                e.printStackTrace();
                Log.e(TAG, "JSchException " + e.getMessage());
                success = false;
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException " + e.getMessage());
                success = false;
                disconnect();
            } catch (SftpException e) {
                e.printStackTrace();
                Log.e(TAG, "SftpException " + e.getMessage());
                success = false;
                disconnect();
            } finally {
                return success;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            disconnect();
        }
    }

    public void uploadFiles(Session session, File[] localFiles, SftpProgressMonitor spm) throws JSchException, IOException, SftpException {
        if (session == null || !session.isConnected()) {
            session.connect();
        }

        Channel channel = session.openChannel("sftp");
        channel.setInputStream(null);
        channel.connect();
        ChannelSftp channelSftp = (ChannelSftp) channel;

        for (File file : localFiles) {
            channelSftp.put(file.getPath(), file.getName(), spm, ChannelSftp.APPEND);
        }

        channelSftp.disconnect();
    }
}