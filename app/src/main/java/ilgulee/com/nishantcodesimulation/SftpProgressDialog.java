package ilgulee.com.nishantcodesimulation;

import android.app.ProgressDialog;
import android.content.Context;

import com.jcraft.jsch.SftpProgressMonitor;

public class SftpProgressDialog extends ProgressDialog implements SftpProgressMonitor {

    private long mSize = 0;
    private long mCount = 0;

    public SftpProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public boolean count(long arg0) {
        mCount += arg0;
        this.setProgress((int) ((float) (mCount) / (float) (mSize) * (float) getMax()));
        return true;
    }

    public void end() {
        this.setProgress(this.getMax());
        this.dismiss();

    }

    public void init(int arg0, String arg1, String arg2, long arg3) {
        mSize = arg3;
    }
}
