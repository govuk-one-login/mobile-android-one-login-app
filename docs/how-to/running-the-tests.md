# How To - Running the tests

## Introduction

This guide shows you how to run both the unit and instrumentation tests for an Android app.

## Finding the tests

With the project open in Android Studio, expand the project directory in the `Project` tab:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the project directory selected in Android Studio](assets/running-the-tests/runningTheTests_openProject.png)

</div>

Now expand the `app` directory:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the app directory selected in Android Studio](assets/running-the-tests/runningTheTests_openApp.png)

</div>

And finally the `src` directory:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the source directory selected in Android Studio](assets/running-the-tests/runningTheTests_openSrc.png)

</div>

## Running the unit tests

Right click on the `test [unitTest]` directory:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the test directory selected in Android Studio](assets/running-the-tests/runningTheTests_rightClickOnTest.png)

</div>

Click the `Run Tests...` option and the test execution will begin:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the run tests option](assets/running-the-tests/runningTheTests_runTests.png)
![Screenshot showing the unit tests running](assets/running-the-tests/runningTheTests_runTests-start.png)

</div>

Once complete the test results are displayed:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the unit test output](assets/running-the-tests/runningTheTests_runTests-finished.png)

</div>

## Running the instrumentation tests

Right click on the `androidTest` directory:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the android test directory selected in Android Studio](assets/running-the-tests/runningTheTests_rightClickOnAndroidTest.png)

</div>

Click the `Run 'All Tests'` option and the test execution will begin:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the run all tests option](assets/running-the-tests/runningTheTests_runAndroidTests.png)
![Screenshot showing the instrumentation tests running](assets/running-the-tests/runningTheTests_runAndroidTests-start.png)

</div>

While the tests are running you will see the Android Virtual Devices (AVD)s screen launching and relaunching the app and flicking through pages as each view or journey is tested.

Once complete the test results are displayed:

<div style="width: 100%; max-width: 800px; margin-left: auto; margin-right: auto;">

![Screenshot showing the instrumentation test output](assets/running-the-tests/runningTheTests_runAndroidTests-finished.png)

</div>
