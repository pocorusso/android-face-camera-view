# android-face-camera-view

# android-custom-surface-view-camera
An android app for testing a face change api with a gallery bottom sheet. This is also in designed with Material Design in mind. The app uses com.google.camera-view https://github.com/google/cameraview to access the camera hardware. 

The Face Change API is a ML api implemented by cpury (https://github.com/cpury) where given an image with a face, it returns
female-fied or male-fied version.

There is a known problem with orientation with certain hardware and on the emulator. It will look 90 degree rotated in landscape mode for some phones. Oh the joy of Android fragmented hardware-scape.
