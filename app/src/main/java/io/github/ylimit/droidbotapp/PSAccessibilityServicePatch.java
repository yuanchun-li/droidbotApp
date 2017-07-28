package io.github.ylimit.droidbotapp;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import android.view.accessibility.AccessibilityEvent;

import io.github.privacystreams.accessibility.AccEvent;
import io.github.privacystreams.core.Callback;
import io.github.privacystreams.core.Item;
import io.github.privacystreams.core.UQI;
import io.github.privacystreams.core.purposes.Purpose;
import io.github.privacystreams.utils.PSDebugSocketServer;

/**
 * Patch for PrivacyStreams accessibility service
 * Need to add following lines to io.github.privacystreams.accessibility.PSAccessibilityService
 * every time importing a new version of PrivacyStreams.
 */
public class PSAccessibilityServicePatch extends AccessibilityService {
    private static final String TAG = "PSAccessibilityService";
    public static boolean enabled = false;

    //++++++++++++++++++++++++++++++++++++++++
    private UQI uqi;
    private IWindowManager wm = IWindowManager.Stub.asInterface(
            ServiceManager.getService(Context.WINDOW_SERVICE));
    private IRotationWatcher watcher = new IRotationWatcher.Stub() {
        @Override
        public void onRotationChanged(int rotation) throws RemoteException {
            PSDebugSocketServer.v().send("rotation >>> " + rotation * 90);
        }
    };
    //=========================================

    @Override
    protected void onServiceConnected() {
        enabled = true;
        super.onServiceConnected();

        //++++++++++++++++++++++++++++++++++++++++
        uqi = new UQI(this);
        uqi.getData(AccEvent.asUpdates(), Purpose.FEATURE("DroidBot accessibility event bridge"))
                .logOverSocket("AccEvent")
                .forEach(new Callback<Item>() {
                    @Override
                    protected void onInput(Item input) {
                        // Do nothing.
                    }
                });
        try {
            wm.watchRotation(watcher);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //=========================================

    }

    @Override
    public boolean onUnbind(Intent intent) {
        enabled = false;

        //++++++++++++++++++++++++++++++++++++++++
        // Sadly, wm.removeRotationWatcher() is only available on API >= 18. Instead, we
        // must make sure that whole process dies, causing DeathRecipient to reap the
        // watcher.
        uqi.stopAll();
        if (Build.VERSION.SDK_INT >= 18) {
            try {
                wm.removeRotationWatcher(watcher);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //=========================================

        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    public void onInterrupt() {
    }
}
