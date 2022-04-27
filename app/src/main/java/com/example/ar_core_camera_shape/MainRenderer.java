package com.example.ar_core_camera_shape;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.google.ar.core.Frame;
import com.google.ar.core.Session;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {
    final static String TAG = "MainRenderer :";
    RenderCallBack myCallBack;

    CameraPreView mCamera;

    int width, height;


    // 화면이 변환되었다면 true
    boolean viewportChanged;

    interface RenderCallBack{
        void preRender();   //MainActivity 에서 재정의하여 호출토록 함
    }

    //생성시 RenderCallBack을  매개변수로 대입받아 자신의 멤버로 넣는다
    // MainActivity 에서 생성하므로 MainAcitivity의 것을 받아서 처리가능 토록 한다.
    MainRenderer(RenderCallBack myCallBack){
        mCamera = new CameraPreView();
        this.myCallBack = myCallBack;
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d(TAG,  "onSurfaceCreated");

                            // R       G          B          A --> 노랑색
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);

        mCamera.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        Log.d(TAG,  "onSurfaceChanged");

        GLES20.glViewport(0,0,width,height);
        viewportChanged = true;
        this.width = width;
        this.height = height;

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
//        Log.d("MainRenderer : ", "onDrawFrame() 실행");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        myCallBack.preRender();

        GLES20.glDepthMask(false);
        mCamera.draw();
        GLES20.glDepthMask(false);
    }

    // 화면 변환이 되었다는 것을 지시할 메소드 --> MainActivity 에서 실행 할 것이다.
    void onDisplayChanged() {
        // 디스플레이가 변경이 되었다면
        viewportChanged = true;
    }

    // 화면이 갱신되었으니 false로 바꿔준다.
    // Session 업데이트시 화면 변환 상태를 보고 session의 화면을 변경한다.
    // 보통 화면 회전에 대한 처리이다.
    void updateSession(Session session, int rotation){
        if(viewportChanged){
            // 디스플레이 화면 방향 설정
            session.setDisplayGeometry(rotation, width, height);
            viewportChanged = false;
            Log.d(TAG,"UpdateSession 실행");
        }
    }

    void draw(){

    }

    int getTextureId(){
        return mCamera == null ? -1: mCamera.mTextures[0];
    }

    void transformDisplayGeometry(Frame frame){
        mCamera.transformDisplayGeometry(frame);
    }
}
