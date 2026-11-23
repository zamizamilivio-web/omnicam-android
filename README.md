# OmniCam AI 5.0 — Android alpha

Native Android camera studio prototype. Separate from the existing OmniCam website.

## Implemented
- Choose an image or video from Android's document picker
- Preview looping video, still images, or a photo from the device camera app
- Image filters (natural, grayscale, sepia)
- Custom text overlay and export still-image preview into app-specific files

## Not implemented
- Android system-wide virtual camera; WhatsApp, Instagram, WorldTalk, Messenger compatibility
- Video filters, video recording/export, audio mixing, face effects, scheduling

## Build on your phone
Open **Actions** > **Build Android APK** > newest successful run > **Artifacts** > **OmniCam-AI-5-debug-apk**. Download and unzip the artifact to find the APK. Debug APK is for testing, not production distribution.

No root privileges are requested. Never grant root permissions to an unknown app. Camera capture is delegated to the phone's camera app. Exported images are stored in this app's external-files directory.
