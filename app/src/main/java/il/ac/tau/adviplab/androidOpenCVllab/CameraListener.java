package il.ac.tau.adviplab.androidOpenCVllab;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

/**
 * Created by amitboy on 2/28/2015.
 */
//todo: Change the tutorial such that CameraListener is a different class

public class CameraListener implements CameraBridgeViewBase.CvCameraViewListener2 {

    // Constants:
    public static final int VIEW_MODE_DEFAULT = 0;
    public static final int VIEW_MODE_RGBA = 1;
    public static final int VIEW_MODE_GRAYSCALE =2;

    //Mode selectors:
    private int mViewMode = VIEW_MODE_DEFAULT;
    private int mColorMode = VIEW_MODE_RGBA;


    //members
    Mat mImToProcess;

    //Getters and setters
    //todo: add to tutorial
    public int getColorwMode() {
        return mColorMode;
    }

    public void setColorwMode(int mColorMode) {
        this.mColorMode = mColorMode;
    }

    public int getViewMode() {
        return mViewMode;
    }

    public void setViewMode(int mViewMode) {
        this.mViewMode = mViewMode;
    }



    @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            switch (this.mColorMode) {
                case CameraListener.VIEW_MODE_RGBA:
                    mImToProcess = inputFrame.rgba();

                    break;
                case CameraListener.VIEW_MODE_GRAYSCALE:
                    mImToProcess = inputFrame.gray();
                    break;
            }
            switch (this.mViewMode) {
                case VIEW_MODE_DEFAULT:
                    break;
            }
            return mImToProcess;
        }

    };



