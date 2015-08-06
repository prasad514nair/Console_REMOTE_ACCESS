package com.cavsusa.ccastconsole;

import android.opengl.GLES20;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

@SuppressWarnings("unused")
public class PanelGLSurfaceView extends GLSurfaceView {
	StatusPanel mPanel; // this is our renderer!!

	public PanelGLSurfaceView(Context context) {
		super(context);
		mPanel = new StatusPanel(context);
        //Log.e("PanelGLSurfaceView", "version" + mPanel.mSDKVersion);

		setRenderer(mPanel);

	}

	public boolean onTouchEvent(final MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
//			nativePause();
		}
		return true;
	}

	public StatusPanel getPanel() {
		return mPanel;
	}

}
