![element](https://github.com/Element1/element-android-examples/raw/master/element-face-sdk-example/images/element.png "element")
# element-android-examples

_Element SDK Library_ is a standalone Android application that enables the creation of biometrics models that can be used to identify users. _Element Orbit_ is the Android library providing interfaces to communicate with _Element SDK Library_. This document contains information to integrate _Orbit Lite_ into an Android application by using Android Studio.

## Prerequisites
### Element Dashboard
The Element Dashboard is [here](https://dashboard.discoverelement.com/login). An account is required to access the Element Dashboard.

### Register the Application Id (App Id) to Element and obtain the Encrypted Access Key (EAK)
The Element SDK requires the Encrypted Access Key (EAK) file. The EAK carries encrypted information including the [Application Id (App Id)](https://developer.android.com/studio/build/application-id). The App Id in the EAK will need to match the id of the running application on an Android device. The EAK is also available on Element Dashboard, under Account -> SDK.
- Fill the App Id field with your application id, and click on Create EAK.
- Download the EAK file.

![dashboard-create-eak](https://github.com/Element1/element-android-examples/raw/master/element-face-sdk-example/images/dashboard-create-eak.jpg "create-eak")


### Setup with Android Studio
#### Import _Orbit Lite_
1. Download the latest [_orbitlite-release.aar_](https://github.com/Element1/element-android-examples/blob/master/orbit-lite-integration-example/orbitlite-release-aar/orbitlite-release.aar) file.
2. Open your project in Android Studio.
3. On the top menu bar, click _File->New->New Module->Import .aar/.jar libraries->Next_.
4. In the next window, choose the path to _ orbitlite-release.aar_ in the _File Name_ field, and type in _orbitlite_ in the _Subproject name_ field.
5. Click the _Finish_ button and wait for Android Studio to finish building the project.

#### Refer to _Orbit Lite_ as a project dependency
1. On the top menu bar, click _File->Project Structure_.
2. Select your app module under _Modules_ on the left pane, switch to the _Dependencies_ tab, and click on the _+_ button at the bottom of the window.
3. Choose the _Module dependency_ option in the popup, and select _element-orbit_.

### Integrating _Orbit Lite_ into your application
* In AndroidManifest.xml, declare your EAK in a meta-data tag.
```
<manifest>
    .....
    <application>
        .....
        <meta-data
            android:name="com.element.EAK"
            android:value="[YOUR_EAK]"/>
        .....
    </application>
</manifest>
```

### Implementation of the key classes

This is our _[JavaDoc](https://element1.github.io/element-android-examples/)_

#### _ElementSDKManager_
_ElementSDKManager_ defines a set of handy functions to send out requests to _Element SDK Library_.

* initElementSDK(Context context): request to initialize the Element SDK Library. If this is not called before the first SDK request, an "Unauthorized" error is presented. At a minimum, this method should be called in _onCreate()_ of your application class, or when the app comes to the foreground.
* enrollNewUser(Activity activity, String userName, HashMap<String,String> extras): request to add a new user
* identifyUser(Activity activity, ArrayList<String> elementUserIds): look through the list of user ids specified to find a user via their palm
* authenticateUser(Activity activity, String elementUserId): authenticate the specified user via their palm    
* requestSyncState(Activity activity, ArrayList<String> elementUserIds, final SyncStateListener listener): request the sync information for the users in the list. Pass an empty list to get sync state for all users
* requestServerSync(Context context): ask the SDK to refresh it's data from the server
* cursorToSyncState(Cursor cursor): convert a LoaderCallbacks Cursor to ElementSyncState and make the data accessible

#### _Listeners_
Used to receive results from _Element SDK Library_.
_EnrollListener_ is used for Enroll.
_SearchListener_ is used for Search.
_AuthListener_ is used for Authentication.
_SyncStateListener_ is used to receive SyncState results.

_ElementSDKManager.getCursorLoader_ is available to listen to the Element ContentProvider using LoaderManager.LoaderCallbacks<Cursor>. This is preferable to requestSyncState() which only polls.

Call _ElementSDKManager.onActivityResult()_ with an instance of one or all of these listeners to receive the appropriate callbacks after making a request.

```
public class MainActivity extends AppCompatActivity implements ElementSDKManager.EnrollListener, ElementSDKManager.SearchListener, ElementSDKManager.SyncStateListener{

...
	@Override
    	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        	super.onActivityResult(requestCode, resultCode, data);
        	ElementSDKManager.onActivityResult(requestCode, resultCode, data, this);
    	}
...
}

```

#### _ElementSyncState_
 Data classes are the informative packets that are delivered between _Element SDK Library_ & _Orbit Lite_.
* _ElementSyncState_: contains the model information and status of each user in the _Element SDK Library_

### Notes
* _Element SDK Library_ is required to be installed on an Android device. It is available on [HockeyApp](https://rink.hockeyapp.net/apps/458abb63bfb442b0afc8989fd0e8b853).
* _Element SDK Library_ is currently in beta release. Please contact Element for access.
