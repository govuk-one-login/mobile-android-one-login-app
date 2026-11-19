# Maestro UI tests

End-to-end UI tests that run against the **mock** flavour of the app, which replaces the Wallet
SDK, the ID Check SDK, the network layer and `LoginSession` with mocks. Nothing leaves the device,
so the journeys are deterministic.

Flows live in [flows](flows).

## Running locally

Install the [Maestro CLI](https://docs.maestro.dev/getting-started/installing-maestro) (tested with
2.12.1):

```bash
curl -fsSL "https://get.maestro.mobile.dev" | bash
export PATH="$PATH:$HOME/.maestro/bin"
```

Start an emulator or connect a device, then install the mocked build and run the flows:

```bash
./gradlew :app:installMockDebug
maestro test maestro/flows
```

## Reports and recordings

`.sh/tests/runMaestroChecks` assembles the mocked app and runs the flows with
[maestro-runner](https://github.com/devicelab-dev/maestro-runner), which also runs on pull
requests. It writes an HTML report, a JUnit report and a screen recording of each flow to
`app/build/reports/maestro`. In CI these are uploaded as the `maestro-reports` artifact: download
it and open `report.html` to watch the recording inline.

Recording every flow is the default. Set `MAESTRO_VIDEO` to `on-failure` to keep recordings only
for flows that fail, or `never` to turn it off:

```bash
MAESTRO_VIDEO=on-failure ./.sh/tests/runMaestroChecks
```

## Writing flows

- Match on visible text. Compose `testTag`s are not exposed to Maestro unless
  `testTagsAsResourceId` is enabled, which this app does not set.
- Avoid text containing a typographic apostrophe (`’`), such as the second line of the sign in
  screen.
- Start each flow with `launchApp: clearState: true`. Choices such as the analytics preference are
  persisted, so without it a flow only passes on a freshly installed app.
- Wait with `extendedWaitUntil` rather than a fixed delay. The splash screen navigates only once
  the app info response has been handled.
