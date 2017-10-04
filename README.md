# android-face-camera-view

An android app for testing a face change api with a gallery bottom sheet. This is built with Material Design in mind. The app uses com.google.camera-view https://github.com/google/cameraview to access the camera hardware. 

The Face Change API is an api implemented by cpury (https://github.com/cpury) where given an image with a face, it returns
female-fied or male-fied version. It is based on Circular Generative Adversarial Networks (CGAN).

#Note:
* There is a known problem with orientation with certain hardware and on the emulator. It will look 90 degree rotated in landscape mode for some phones. Oh the joy of Android fragmented hardware-scape.


* Instead of using external download library like Volley to download thumbnail. I wrote the downloader and caching to improve my understanding of it. I am sure there are more efficient way to do it. I will get back to it sometime. The uploader is based on Volley. And, yes, I know this will never go in production, but this is only a fun project for me.
