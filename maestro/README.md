# Maestro UI tests

End-to-end UI tests that run against the **mock** flavour of the app, which replaces the Wallet
SDK, the ID Check SDK, the network layer and `LoginSession` with mocks. Nothing leaves the device,
so the journeys are deterministic.

Flows live in [flows](flows).

## Running locally

Install the [Maestro CLI](https://docs.maestro.dev/getting-started/installing-maestro), following
their instructions for your platform. These flows were written against 2.12.1.

Start an emulator or connect a device, then install the mocked build and run the flows:

```bash
./gradlew :app:installMockDebug
maestro test maestro/flows
```

## Writing flows

- Match on visible text. Compose `testTag`s are not exposed to Maestro unless
  `testTagsAsResourceId` is enabled, which this app does not set.
- Copy the text to match exactly out of `strings.xml`. Several strings use a typographic apostrophe
  (`’`) rather than `'` — the second line of the sign in screen, for example — and a selector typed
  with the plain character will not match.
- Start each test scenario with `launchApp: clearState: true`, meaning the flow that begins a
  journey rather than every flow it is composed of. Choices such as the analytics preference are
  persisted, so without it a scenario only passes on a freshly installed app.
- Wait with `extendedWaitUntil` rather than a fixed delay. The splash screen navigates only once
  the app info response has been handled.
