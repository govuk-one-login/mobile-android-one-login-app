# How To - Running the code

## Introduction

This guide shows you how to setup an Android Virtual Devive (AVD) and run the application from Android Studio.

_Note:_ it is assumed that you have already cloned the git repo to a suitable working directory

## Creating the AVD

With the project loaded in Android Studio open the `Device Manager` tab from the right-hand toolbar

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio with the Device Manager tab opened](./assets/runningTheCode/runningTheCode_deviceManager-create.png)

</div>

Click the `Create Device` button and you will be presented with the AVD hardware selection modal

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's hardware selection modal](./assets/runningTheCode/runningTheCode_avdCreate-selectHardware.png)

</div>

Select `Pixel XL` from the list and hit `Next`

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's hardware selection modal with Pixel XL selected](./assets/runningTheCode/runningTheCode_avdCreate-selectDevice.png)

</div>

From the system image modal click on the download icon next to the `Q` release

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's system image modal](./assets/runningTheCode/runningTheCode_avdCreate-selectSystemImage.png)

</div>

The SDK Component Installer will start downloading the required assets, click `Finish` once it has completed

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's SDK Component Installer modal downloading assets](./assets/runningTheCode/runningTheCode_avdCreate-sdkComponentInstalling.png)
![Screenshot of Android Studio's SDK Component Installer modal with a completed download](./assets/runningTheCode/runningTheCode_avdCreate-sdkComponentInstalled.png)

</div>

Click `Next` on the system image modal

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's system image modal after installing the Q relese](./assets/runningTheCode/runningTheCode_avdCreate-systemImageInstalled.png)

</div>

Click the `Show Advanced Settings` button in the next screen, then scroll down to the bottom of the left pane to see `SD Card`

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's AVD configuration verification modal](./assets/runningTheCode/runningTheCode_avdCreate-verify.png)
![Screenshot of Android Studio's AVD configuration verification modal showing the advanced settings](./assets/runningTheCode/runningTheCode_avdCreate-sdCardSize.png)

</div>

Change the default `512 MB` to `2048 MB` and click `Finish`

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's AVD configuration verification modal showing the updated SD Card size](./assets/runningTheCode/runningTheCode_avdCreate-finished.png)

</div>

Your newly created AVD should now be shown as a virtual device in the `Device Manager` tab

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's Device Manager showing the Pixel XL AVD](./assets/runningTheCode/runningTheCode_avdLoad-start.png)

</div>

## Starting the AVD manually

Click on the play button next to the `Pixel XL API 29` AVD in the `Actions` column. The `Running Devices` tab should open

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio's Running Devices tab](./assets/runningTheCode/runningTheCode_avdLoad-runningDevices.png)

</div>

After a few seconds you should see the AVD start to boot in the `Running Devices` tab

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio showing the Pixel XL AVD booting](./assets/runningTheCode/runningTheCode_avdLoad-booting.png)

</div>

Once the device has finished booting you will be left at a default Android phone's desktop

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio showing the Pixel XL AVD after booting is complete](./assets/runningTheCode/runningTheCode_avdLoad-finished.png)

</div> 

## Running the app

From the top toolbar in Android Studio click the green play button found just to the right of center within the toolbar

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio highlighting the run button](./assets/runningTheCode/runningTheCode_appLaunch-runApp.png)

</div> 

Gradle will build the app, install it onto our AVD, and launch it for us. The progress of these actions can be seen in the status bar in the bottom right of the Android Studio window.

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio showing the Gradle Build Running progress bar](./assets/runningTheCode/runningTheCode_appLaunch-gradleBuild.png)

</div> 

Once the task has finished you should see the landing screen of the app in the AVD

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot of Android Studio showing the app running on the AVD](./assets/runningTheCode/runningTheCode_appLaunch-finished.png)

</div>
