package il.ac.tau.adviplab.androidOpenCVllab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import il.ac.tau.adviplab.myimageproc.MyImageProc;
import il.ac.tau.adviplab.myimageproc.Util;

public class StillsActivity extends SpatialFilteringActivity {

    private Button mLoadButton;
    private static final String TAG = StillsActivity.class.getName();
    private static final int SELECT_PICTURE = 1;
    private Uri mURI;
    private Bitmap mBitmap;
    private ImageView mImageView;
    private Mat mImToProcess = new Mat();
    private Mat mImGray = new Mat();
    private Mat mFilteredImage = new Mat();
    private SeekBar mSeekBarSpatial;
    private SeekBar mSeekBarIntensity;
    private TextView mTextViewSpatial;
    private TextView mTextViewIntensity;
    private MenuItem mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stills);
        mImageView = (ImageView) findViewById(R.id.imageView1);

        mLoadButton = (Button) findViewById(R.id.loadButton);
        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "onClick event");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                                "Select image for histogram matching"),
                        SELECT_PICTURE);

            }
        });

        mSeekBarSpatial = (SeekBar) findViewById(R.id.seekBarSpatial);
        mSeekBarIntensity = (SeekBar) findViewById(R.id.seekBarIntensity);
        mTextViewSpatial = (TextView) findViewById(R.id.sigmaSpatialTextView);
        mTextViewIntensity = (TextView) findViewById(R.id.sigmaIntensityTextView);

        mSeekBarSpatial = (SeekBar) findViewById(R.id.seekBarSpatial);
        mTextViewSpatial = (TextView)
                findViewById(R.id.sigmaSpatialTextView);
        setSeekBar(mSeekBarSpatial, mTextViewSpatial,
                getResources().getString(R.string.stringSpatial),
                MyImageProc.SIGMA_SPATIAL_MAX);
        mSeekBarIntensity = (SeekBar) findViewById(R.id.seekBarIntensity);
        mTextViewIntensity = (TextView)
                findViewById(R.id.sigmaIntensityTextView);
        setSeekBar(mSeekBarIntensity,mTextViewIntensity, getResources()
                .getString(R.string.stringIntensity),MyImageProc.SIGMA_INTENSITY_MAX);

    }

    private void setSeekBar(final SeekBar seekbar, final TextView
            textview,final String string, final float sigmaMax){
        float sigma = ((float) seekbar.getProgress() / (float)
                seekbar.getMax()) * sigmaMax;
        textview.setText(string+sigma);
        seekbar.setOnSeekBarChangeListener(new
                                                   SeekBar.OnSeekBarChangeListener() {
                                                       @Override
                                                       public void onProgressChanged(SeekBar seekBar, int progresValue,
                                                                                     boolean fromUser) {
                                                       }

                                                       @Override
                                                       public void onStartTrackingTouch(SeekBar seekBar) {
                                                       }

                                                       @Override
                                                       public void onStopTrackingTouch(SeekBar seekBar) {
                                                           float sigma = ((float) seekbar.getProgress() / (float)
                                                                   seekbar.getMax()) * sigmaMax;
                                                           textview.setText(string+sigma);
//Call the filter again
                                                           if (mSelectedItem !=null) {
                                                               int groupId = mSelectedItem.getGroupId();
                                                               if (groupId == FILTER_GROUP_ID) {
                                                                   launchRingDialog(mSelectedItem.getItemId());
                                                               }
                                                           }
                                                       }
                                                   });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mURI = data.getData();
            if (mURI != null) {
                try {
                    mBitmap = Util.getBitmap(this, mURI);
                    mImageView.setImageBitmap(Util.getResizedBitmap(mBitmap,1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        mSelectedItem = item;
        int groupId = item.getGroupId();
        int id = item.getItemId();
        switch (groupId) {
            case SETTINGS_GROUP_ID:
                Toast.makeText(this, getString(R.string.notAvilable),
                        Toast.LENGTH_SHORT).show();
                break;
            case DEFAULT_GROUP_ID:
                if (mURI!=null) {
                    mBitmap = Util.getBitmap(this, mURI);
                    mImageView.setImageBitmap(Util.getResizedBitmap(mBitmap,1000));
                    mSeekBarIntensity.setProgress(MyImageProc.SIGMA_INTENSITY_DEFAULT);
                    mSeekBarSpatial.setProgress(MyImageProc.SIGMA_SPATIAL_DEFAULT);
                }
                break;
            case COLOR_GROUP_ID:
                if (mURI!=null) {
                    mBitmap = Util.getBitmap(this, mURI);
                    if (id == CameraListener.VIEW_MODE_GRAYSCALE) {
                        Utils.bitmapToMat(mBitmap, mImToProcess);
                        Imgproc.cvtColor(mImToProcess, mImToProcess,
                                Imgproc.COLOR_RGBA2GRAY);
                        mBitmap = Bitmap.createBitmap(mImToProcess.cols(),
                                mImToProcess.rows(), Bitmap.Config.RGB_565);
                        Utils.matToBitmap(mImToProcess, mBitmap, true);
                    }
                    mImageView.setImageBitmap(Util.getResizedBitmap(mBitmap,1000));
                }
                break;
            case FILTER_GROUP_ID:
                launchRingDialog(id);
                //Here we shall add the filters
                break;
        }
        return true;
    }

    public static void filterImage(int id, Mat imToDisplay, Mat
            imToProcess, Mat filteredImage, float sigmaSpatial, float
                                           sigmaIntensity) {
        switch (id) {
            case CameraListener.VIEW_MODE_SOBEL:
                int[] window = MyImageProc.setWindow(imToProcess);
                MyImageProc.gaussianFilter(imToProcess, filteredImage, window, sigmaSpatial);
                MyImageProc.sobelCalcDisplay(imToDisplay, filteredImage, filteredImage);
                break;
            case CameraListener.VIEW_MODE_GAUSSIAN:
                if (sigmaSpatial > 0) {
                    MyImageProc.gaussianCalcDisplay(imToDisplay, imToProcess,
                            filteredImage, sigmaSpatial);
                }
                break;
            case CameraListener.VIEW_MODE_BILATERAL:
                if (sigmaSpatial > 0){
                    MyImageProc.bilateralCalcDisplay(imToDisplay,
                            imToProcess, filteredImage, sigmaSpatial,
                            sigmaIntensity);
                }
                break;
        }
    }

    public void launchRingDialog(final int id) {
        final ProgressDialog ringProgressDialog =
                ProgressDialog.show(StillsActivity.this, "Please wait ...",
                        "Processing Image ...", true);
        ringProgressDialog.setCancelable(false);
        Thread filterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String TAG = "launcherDialogTag";
                try {
                    // Here you should write your time consuming task...
                    float sigmaSpatial = mSeekBarSpatial.getProgress();
                    float sigmaIntensity = mSeekBarIntensity.getProgress();
                    mBitmap = Util.getBitmap(StillsActivity.this,mURI);
                    Utils.bitmapToMat(mBitmap, mImToProcess);
                    Imgproc.cvtColor(mImToProcess, mImGray,
                            Imgproc.COLOR_RGBA2GRAY);
                    filterImage(id, mImToProcess, mImGray, mFilteredImage,
                            sigmaSpatial, sigmaIntensity);
                    mBitmap = Bitmap.createBitmap(mImToProcess.cols(),
                            mImToProcess.rows(), Bitmap.Config.RGB_565);
                    Utils.matToBitmap(mImToProcess, mBitmap, true);
                    /*Since a View can only be updated by the thread that
                    created it, we use the "post" method, to tell the UI
                    thread to update the ImageView after the other
                    thread ended*/
                    mImageView.post(new Runnable() {
                        public void run() {
                            mImageView.setImageBitmap(Util.getResizedBitmap(mBitmap,1000));
                        }
                    });
                    Log.i(TAG, "filter finished");                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                ringProgressDialog.dismiss();
            }
        });
        filterThread.start();
    }


}
