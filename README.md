# ZoomSDKApp

## Zoom Guides for SDK Integration
https://marketplace.zoom.us/docs/guides
https://marketplace.zoom.us/docs/sdk/native-sdks/introduction

## Prerequisites
Android Studio

A physical Android device with Android API Level 21+

Valid SDK Credentials


## 1) Get Valid SDK Credentials
 
Setting up developer accounts
https://marketplace.zoom.us/docs/sdk/custom/developer-accounts

Click on Create App on top right corner menu.

Choose your app type

Generate your SDK Key & Secret

Create an SDK app


Continue and Copy App Credentials -  *SDK Key*    and     *SDK Secret* 

## Your app is activated on the account

If you not understand then follow these steps

Follow This steps https://marketplace.zoom.us/docs/sdk/custom/auth


## 2 Let’s Create Android Studio Project

Import the SDK Libraries

Download the SDK library from the Fully Customizable SDK app that you registered in the Zoom App Marketplace and copy the mobilertc and commonlib folder into the root directory of the project.

Add both module to the projects

include ':mobilertc',':commonlib'

Add dependency into app level build.gradle and follow by this code

### Zoom SDK required this library dependencies
<i><b>
implementation fileTree(dir: "libs", include: ["*.jar"])

implementation project(':mobilertc')

implementation project(':commonlib')

implementation 'androidx.multidex:multidex:2.0.1'

implementation 'androidx.recyclerview:recyclerview:1.1.0'

implementation "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"

implementation 'androidx.legacy:legacy-support-v4:1.0.0'

implementation 'com.google.android:flexbox:2.0.1'
</b></i>


Add Required Permissions
The Zoom Instant SDK requires the following permissions. Ensure that your app manifest has been set up properly to request these permissions.
 
# That’s It!

