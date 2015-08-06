package com.cavsusa.ccastconsole;

import android.content.Context;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


public class StatusPanel implements GLSurfaceView.Renderer {
	public StatusPanel(Context context) {
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		this.glesDraw();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		this.glesResize(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		this.glesInit();
	}

	public native void glesInit();
	public native void glesDraw();
	public native void glesResize(int w, int h);

	static {
		System.loadLibrary("ccastdraw");
	}
}
