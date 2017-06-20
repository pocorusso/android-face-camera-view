package com.pocorusso.bearbeard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.io.File;


public class FaceCameraFragment extends Fragment {
    private final static String TAG = "FaceCameraFragment";

    private final static String URL1 = "http://ec2-34-250-78-232.eu-west-1.compute.amazonaws.com/w2m";
    private final static String URL2 = "http://ec2-34-250-78-232.eu-west-1.compute.amazonaws.com/m2w";
    private CameraView mCameraView;
    private FloatingActionButton mBtnTakePicture;
    private ImageView mImageViewUploadResult;
    private ImageView mImageViewCaptureResult;
    private ImageButton mBtnRefresh;
    private ImageButton mBtnGallery;
    private ImageButton mBtnUpload1;
    private ImageButton mBtnUpload2;
    private ProgressBar mProgressBar;

    private Handler mBackgroundHandler;
    private int mCurrentFlash;
    private File mFile;

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_face_camera, container, false);


        mCameraView = (CameraView) v.findViewById(R.id.camera);
        mCameraView.addCallback(mCameraCallback);

        mImageViewCaptureResult = (ImageView) v.findViewById(R.id.capture_result);

        mImageViewUploadResult = (ImageView) v.findViewById(R.id.upload_result);

        mBtnRefresh = (ImageButton) v.findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUiState(UiState.TAKE_PICTURE);
            }
        });

        mBtnTakePicture = (FloatingActionButton) v.findViewById(R.id.take_picture);
        mBtnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCameraView != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mCameraView.takePicture();
                }
            }
        });

        //top tool bar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.camera_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mBtnGallery = (ImageButton) v.findViewById(R.id.btn_open_gallery);
        mBtnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set up galery open
            }
        });

        mBtnUpload1 = (ImageButton) v.findViewById(R.id.btn_upload_1);
        mBtnUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressBar.setVisibility(View.VISIBLE);

                Uploader.getInstance(getActivity().getApplicationContext()).uploadFile(mFile, new Uploader.UploadListener() {
                    @Override
                    public void onUploaded(Bitmap bitmap) {
                        setUiState(UiState.FINISHED_UPLOAD);
                        mImageViewUploadResult.setImageBitmap(bitmap);
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onUploadError() {
                        //todo handle upload error
                        mProgressBar.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(getActivity()
                                , getString(R.string.failed_upload)
                                , Toast.LENGTH_LONG);
                        toast.show();
                    }
                }, URL1);
            }
        });

        mBtnUpload2 = (ImageButton) v.findViewById(R.id.btn_upload_2);
        mBtnUpload2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //todo set up second upload
                mProgressBar.setVisibility(View.VISIBLE);

                Uploader.getInstance(getActivity().getApplicationContext()).uploadFile(mFile, new Uploader.UploadListener() {
                    @Override
                    public void onUploaded(Bitmap bitmap) {
                        setUiState(UiState.FINISHED_UPLOAD);
                        mImageViewUploadResult.setImageBitmap(bitmap);
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onUploadError() {
                        //todo handle upload error
                        mProgressBar.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(getActivity()
                                , getString(R.string.failed_upload)
                                , Toast.LENGTH_LONG);
                        toast.show();
                    }
                }, URL2);
            }
        });

        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        setUiState(UiState.TAKE_PICTURE);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.camera, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_flash:
                if (mCameraView != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
                return true;
            case R.id.switch_camera:
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private CameraView.Callback mCameraCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);

            setUiState(UiState.PREPARE_UPLOAD);
            mImageViewCaptureResult.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
            mProgressBar.setVisibility(View.GONE);

            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    mFile = PictureUtils.savePictureInPrivateStorage(getActivity(), data);
                }
            });


        }
    };


    private static enum UiState {
        TAKE_PICTURE,
        PREPARE_UPLOAD,
        FINISHED_UPLOAD
    }

    private void setUiState(UiState state) {
        switch (state) {
            case TAKE_PICTURE:
                mBtnTakePicture.setClickable(true);
                mBtnTakePicture.setVisibility(View.VISIBLE);

                mBtnRefresh.setClickable(false);
                mBtnRefresh.setVisibility(View.INVISIBLE);

                mBtnUpload1.setClickable(false);
                mBtnUpload1.setVisibility(View.INVISIBLE);

                mBtnUpload2.setClickable(false);
                mBtnUpload2.setVisibility(View.INVISIBLE);

                mImageViewCaptureResult.setVisibility(View.GONE);
                mImageViewUploadResult.setVisibility(View.GONE);

                mProgressBar.setVisibility(View.GONE);
                break;
            case PREPARE_UPLOAD:
                mBtnRefresh.setClickable(true);
                mBtnRefresh.setVisibility(View.VISIBLE);

                mBtnTakePicture.setClickable(false);
                mBtnTakePicture.setVisibility(View.INVISIBLE);

                mBtnUpload1.setClickable(true);
                mBtnUpload1.setVisibility(View.VISIBLE);

                mBtnUpload2.setClickable(true);
                mBtnUpload2.setVisibility(View.VISIBLE);

                mImageViewCaptureResult.setVisibility(View.VISIBLE);
                mImageViewUploadResult.setVisibility(View.INVISIBLE);

                break;
            case FINISHED_UPLOAD:
                mBtnRefresh.setClickable(true);
                mBtnRefresh.setVisibility(View.VISIBLE);

                mBtnTakePicture.setClickable(false);
                mBtnTakePicture.setVisibility(View.INVISIBLE);

                mBtnUpload1.setClickable(true);
                mBtnUpload1.setVisibility(View.VISIBLE);

                mBtnUpload2.setClickable(true);
                mBtnUpload2.setVisibility(View.VISIBLE);

                mImageViewCaptureResult.setVisibility(View.VISIBLE);
                mImageViewUploadResult.setVisibility(View.VISIBLE);

            default:
                //do nothing
        }
    }
}
