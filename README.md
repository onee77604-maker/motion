# Camera — GyroLock V28.6 (gyro/flow fusion fix)

This revision removes the experimental additive sparse-optical-flow correction from the per-frame render path. The V28.5 flow estimator integrated global block-match displacement and then added that result on top of the gyro-derived correction. Since both estimates can represent the same camera rotation, this could apply motion compensation twice and create visible jelly/bounce.

The 240-point gyro trajectory, current stabilization gain, Vulkan render path, 30 FPS output timestamp normalization, audio remux, and portrait rotation handling are retained. This change is intended to eliminate conflicting correction; it is not a claim that rolling-shutter wobble is fully corrected. The optical-flow class remains in the source tree but is not invoked during rendering.

## Build

Push the contents of this project folder to the root of a GitHub repository and run **Actions → Build GyroLock Vulkan APK → Run workflow**. The workflow installs the Android SDK/NDK and builds a debug APK. This environment did not run a full Android SDK build, so a successful APK build has not been verified here.

Output target remains 30 FPS with the original audio remuxed without re-encoding. Actual stabilization quality depends on gyro alignment, camera readout timing, and device codec behavior.

## Motion Photo experimental mode

This branch adds a continuous low-resolution CameraX video buffer, full-quality still capture, a 1.5-second post-shutter capture window, gyro stabilization, and JPEG+MP4 Motion Photo packaging with Google Camera XMP metadata. Still capture uses CameraX `CAPTURE_MODE_MAXIMIZE_QUALITY`; this does not guarantee hardware HDR because the device camera may not expose an HDR capture mode. Motion Photo gallery support varies by gallery app and OEM firmware. The rolling buffer is trimmed to approximately the last three seconds before stabilization. This is an experimental device-test build; verify playback and metadata on the target Oppo device.
