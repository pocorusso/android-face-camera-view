package com.pocorusso.bearbeard;

import android.support.v4.app.Fragment;

public class FaceCameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new FaceCameraFragment();
    }
}
