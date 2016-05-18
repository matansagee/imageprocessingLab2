package il.ac.tau.adviplab.androidOpenCVllab;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import il.ac.tau.adviplab.myimageproc.MyImageProc;

/**
 * Created by amitboy on 2/28/2015.
 */
//todo: Change the tutorial such that CameraListener is a different class

public class CameraListener implements CameraBridgeViewBase.CvCameraViewListener2 {

    // Constants:
    public static final int VIEW_MODE_DEFAULT = 0;
    public static final int VIEW_MODE_RGBA = 1;
    public static final int VIEW_MODE_GRAYSCALE =2;
    public static final int VIEW_MODE_SOBEL =3;
    public static final int VIEW_MODE_GAUSSIAN =4;
    public static final int VIEW_MODE_BILATERAL =5;
    public static final int VIEW_MODE_UNSHARP_MASKING = 6;

    //Mode selectors:
    private int mViewMode = VIEW_MODE_DEFAULT;
    private int mColorMode = VIEW_MODE_RGBA;
    private int mFilterMode = VIEW_MODE_SOBEL;

    //members
    Mat mImToProcess;
    Mat mFilteredImage;

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
        mFilteredImage = new Mat();

        }

        @Override
        public void onCameraViewStopped() {
            mFilteredImage.release();

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
                case VIEW_MODE_SOBEL:
                    MyImageProc.sobelCalcDisplay(mImToProcess,
                            inputFrame.gray(),
                            mFilteredImage);
                    break;
                case VIEW_MODE_GAUSSIAN:
                    MyImageProc.gaussianCalcDisplay(mImToProcess,
                            inputFrame.gray(),
                            mFilteredImage);
                    break;
                case VIEW_MODE_BILATERAL:
                    MyImageProc.bilateralCalcDisplay(mImToProcess,
                            inputFrame.gray(),
                            mFilteredImage);
                    break;
            }
            return mImToProcess;
        }

    public void setFilterMode(int filterMode) {
        mFilterMode = filterMode;
    }
};



