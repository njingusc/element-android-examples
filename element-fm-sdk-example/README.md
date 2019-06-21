![element](https://github.com/Element1/element-android-examples/raw/master/element-face-sdk-example/images/element.png "element")
# Element FM SDK
The Element FM (Face Matching) SDK provides an API library to authenticate users by taking selfie images on Android devices. The images will be processed on a server in order to obtain the matching results. This document contains information to integrate the Element FM SDK into an Android application by using Android Studio.

## Version Support
- The Element FM SDK requires Android 5.0+ / API 21+ (Lollipop and up)
- Android Studio 3.2.0 with Gradle Wrapper 4.6
- Android Target SDK Version 28, Build Tool Version 28.0.3, and AndroidX
- Android Material: 1.0.0
- AndroidX Work Manager: 2.0.1
- Google Play Service Location: 17.0.0
- Google Guava for Android: 27.0.1-android
- AWS Mobile SDK: 2.8.5

Check the `dependencies` block in the [build.gradle](https://github.com/Element1/element-android-examples/blob/master/element-fm-sdk-example/apps/build.gradle) in the example project for more details.

## Prerequisites
### Element Dashboard
The Element Dashboard is the gateway to the assets in order to use the Element FM SDK. The URL of the Element Dashboard varies based on your region. Also an account is required to access the Element Dashboard. Please contact [Element](https://github.com/Element1/element-android-examples/tree/master/element-fm-sdk-example#questions) for more information.

### AAR
The Element FM SDK is in the [AAR](https://developer.android.com/studio/projects/android-library) format. Download the AAR:
1. Log into the Element Dashboard with your account.
1. Select `Account` tab in the left navigation bar.
1. Find the menu item named `SDK`, click the `View` button next to it.
![dashboard-account](https://github.com/Element1/element-android-examples/raw/master/element-face-sdk-example/images/dashboard-account.jpg "dashboard-account")
1. Under `SDK Files` section, click the SDK download link and save it to the desktop of your computer.
![dashboard-sdk-files](https://github.com/Element1/element-android-examples/raw/master/element-face-sdk-example/images/dashboard-sdk-files.jpg "dashboard-sdk-files")

### Register the Application Id (App Id) to Element and obtain the Encrypted Access Key (EAK)
The Element FM SDK requires the *Encrypted Access Key* (*EAK*) file. The *EAK* file carries encrypted information including the [Application Id (App Id)](https://developer.android.com/studio/build/application-id) of your Android app. Your registered *EAK* is available on the Element Dashboard, under `Account -> SDK`. Here is how you get a new *EAK* file:
1. On the same page of where you download the SDK file, fill in the `App Id` field with your `application id`. You can find your `application id` in your module-level `build.gradle` file. Leave other fields unchanged and click `Create EAK`.
1. You new EAK will be listed on the page. Hover your mouse on the EAK you want to download and a little download icon will appear next to your `app id`. Click it, name the file `element.eak` and save it to the desktop of your computer.

![dashboard-create-eak](https://github.com/Element1/element-android-examples/raw/master/element-face-sdk-example/images/dashboard-create-eak.jpg "create-eak")

## Setup with Android Studio
### Import the AAR
1. Open your project in Android Studio.
1. On the top menu bar, click `File -> New -> New Module`. In the `Create New Module` window, click `Import .JAR/.AAR Package`, then click `Next`.
1. In the next window, click the `...` next to `File name` field and select the AAR file in your computer's desktop directory. Then type in `element-fm-sdk` in the `Subproject Name` field.
1. Click the `Finish` button and wait for Android Studio to finish building the project.

### Add element-fm-sdk and dependencies
1. Add `element-fm-sdk` module to your project by adding the following code to the `settings.gradle` file in the root directory of your project:
    ```
    include ':element-fm-sdk'
    ```
1. On the top menu bar, click `File -> Project Structure`.
1. Select your app module under `Modules` on the left pane, click on the `Dependencies` tab, and click on the `+` button at the bottom of the window. In the popup, click `Module Dependency` and select `:element-fm-sdk`. Click `Ok`.
1. Add the following dependencies to the module-level `build.gradle`:
    ```
        dependencies {
            .....
            implementation 'androidx.work:work-runtime:2.0.1'
            implementation 'com.amazonaws:aws-android-sdk-core:2.8.5'
            implementation 'com.amazonaws:aws-android-sdk-s3:2.8.5'
            implementation 'com.google.android.gms:play-services-location:17.0.0'
            implementation 'com.google.android.material:material:1.0.0'
            implementation 'com.google.guava:guava:27.0.1-android'
        }
    ```
    Note that you might have already declared some of these dependencies in your module-level `build.gradle` file, so please make sure you did not declare them twice. And you might also have to tweak a little bit on the versions of the dependencies as well as `compileSdkVersion` and `targetSdkVersion` in the `build.gradle`. Please follow the Android Studio's prompts on this. More information can be found [here](https://developer.android.com/studio/build/#module-level).
1. In gradle.properties, add the following lines to enable AndroidX support:
    ```
        android.useAndroidX=true
        android.enableJetifier=true
    ```
1. Wait for the Android Studio to sync.

### Include the EAK in the application
1. Create a resources directory at `[project dir]/app/src/main/resources`.
1. Copy the `element.eak` file into the `resources` directory.
![resources](https://github.com/Element1/element-android-examples/raw/master/element-face-sdk-example/images/resources.jpg "resources")

## Using the Element FM SDK APIs
### Initialize the Element FM SDK
1. Create a class which extends [android.app.Application](https://developer.android.com/reference/android/app/Application) if you haven't, and initialize the Element FM SDK in `onCreate()` method:
    ```
        public class MainApplication extends Application {
          @Override
          public void onCreate() {
            super.onCreate();
            ElementFaceSDK.initSDK(this);
          }
        }
    ```
1. Declare the `MainApplication` class in AndroidManifest.xml:
    ```
        <manifest>
          .....
          <application android:name=".MainApplication">
            .....
          </application>
        </manifest>
    ```

### Ask for user permissions
1. The Element FM SDK requires the following permissions:
    - `android.Manifest.permission.CAMERA`
    - `android.Manifest.permission.ACCESS_FINE_LOCATION`
    - `android.Manifest.permission.ACCESS_COARSE_LOCATION`
    Those permissions are declared in the Element FM SDK AAR, so no need to declare them again in the manifest in your app.
1. The Element FM SDK provides `PermissionUtils.verifyPermissions(Activity activity, String... permissionsToVerified)` for requesting the permissions:
    ```
        PermissionUtils.verifyPermissions(
          MainActivity.this,
          Manifest.permission.CAMERA,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION);
    ```
    For the Android Marshmallow 6.0 (API 23) OS and up, make sure the permissions are granted before starting any Activity provided by the Element FM SDK.

### User face matching
The Element FM SDK utilizes the `ElementFaceCaptureActivity` class for face matching. It allows capturing selfies for further processing on the server side.
1. Create a child class of the `ElementFaceCaptureActivity` class
1. Override the `onImageCaptured(Capture[] captures, String resultCode)` callback method to receive the selfie images
1. The `Capture[]` object from the callback contains the JPEG image data in bytes
1. The 'resultCode' string specifies the status of the captures
1. Sent the captured image data to the server if there status is OK
```
    public class FmActivity extends ElementFaceCaptureActivity {
      @Override
      public void onImageCaptured(Capture[] captures, String resultCode) {
          if (CAPTURE_RESULT_OK.equals(resultCode) || CAPTURE_RESULT_GAZE_OK.equals(resultCode)) {
              for (Capture capture : captures) {
                  String encoded = Base64.encodeToString(capture.data, Base64.DEFAULT);
              }
              .....
              new FmTask(faceMatchingTaskCallback).execute(getId(), captures);
              .....
          } else {
              .....
          }
      }
    }
```

### SDK Debug Mode
The debug mode can be enabled with the Element FM SDK. It is disabled initially. Once the debug mode is turned on, all image captures will be sent to Element for investigation. Please use the feature if you need help from Element.
```
    ElementFaceSDK.enableDebugMode(getBaseContext(), true);
```

### Questions?
If you have questions, please contact devsupport@discoverelement.com.
