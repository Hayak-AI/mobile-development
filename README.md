# Mobile Development Setup

This guide will help you set up your mobile development environment for Android using Kotlin and Java.

## Prerequisites

- [Android Studio](https://developer.android.com/studio) installed
- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) installed
- [Android SDK](https://developer.android.com/studio#downloads) installed

## Getting Started

### 1. Clone the Repository

```sh
git clone https://github.com/your-repo/your-project.git
cd your-project
```

### 2. Open the Project in Android Studio

1. Launch Android Studio.
2. Select **Open an existing Android Studio project**.
3. Navigate to the cloned repository and select it.

### 3. Configure Local Properties

Create a `local.properties` file in the root directory of your project and add the following lines:

```ini
sdk.dir=path_to_your_android_sdk
MAPS_API_KEY=your_maps_api_key
API_URL="http://your_api_url/"
GEMINI_API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent/"
GEMINI_API_KEY="your_gemini_api_key"
```

Replace `path_to_your_android_sdk`, `your_maps_api_key`, `your_api_url`, and `your_gemini_api_key` with your actual values.

### 4. Configure API URL

1. Clone the `cloud-computing` repository:

    ```sh
    git clone https://github.com/Hayak-AI/cloud-computing.git
    cd cloud-computing
    ```

2. Follow the setup instructions in the `cloud-computing` repository to deploy the backend services.

3. Once the backend services are deployed, update the `API_URL` in your `local.properties` file with the URL of your deployed backend:

    ```ini
    API_URL="http://your_deployed_backend_url/"
    ```

Replace `http://your_deployed_backend_url/` with the actual URL of your deployed backend.

### 5. Obtain API Keys

#### Google Maps API Key

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing project.
3. Navigate to **APIs & Services** > **Credentials**.
4. Click **Create credentials** and select **API key**.
5. Restrict the API key to your Android app by adding your app's package name and SHA-1 certificate fingerprint.
6. Enable the **Maps SDK for Android** API in the **Library** section.
7. Copy the generated API key and add it to your `local.properties` file as `MAPS_API_KEY`.

#### Gemini API Key

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing project.
3. Navigate to **APIs & Services** > **Credentials**.
4. Click **Create credentials** and select **API key**.
5. Enable the **Generative Language API** in the **Library** section.
6. Copy the generated API key and add it to your `local.properties` file as `GEMINI_API_KEY`.

### 6. Build the Project

1. Open the `build.gradle.kts` file and ensure all dependencies are correctly set up.
2. Sync the project with Gradle files by clicking **Sync Now** in the notification bar.

### 7. Run the Project

1. Connect an Android device or start an emulator.
2. Click the **Run** button in Android Studio or press `Shift + F10`.

## Additional Configuration

### ProGuard

If you are using ProGuard, ensure you have the necessary rules in `app/proguard-rules.pro`:

```ini
# Add project specific ProGuard rules here.
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name.
#-renamesourcefileattribute SourceFile
```

### API Keys

Ensure your API keys are kept secure and not checked into version control. Use environment variables or a secure vault for production keys.

## Troubleshooting

- **Memory Issues**: If you encounter memory allocation issues, consider increasing the heap size in `AndroidManifest.xml`:

    ```xml
    <application
        android:largeHeap="true"
        ... >
        ...
    </application>
    ```

- **Image Loading Issues**: If images are not showing on the first click, ensure the info window is refreshed after the image is loaded.

    ```kotlin
    val request = ImageRequest.Builder(view.context)
        .allowHardware(false)
        .data(evidenceUrl)
        .target(
            onStart = { placeholder -> },
            onSuccess = { result ->
                evidence.setImageBitmap(result.toBitmap())
                marker.showInfoWindow() // Force refresh the info window
            },
            onError = { error -> }
        )
        .build()
    view.context.imageLoader.enqueue(request)
    ```
## Screenshots
You can view the application screenshots [here](https://drive.google.com/drive/folders/1z8Hu0BX9fyIVq4UmEXoif5lhUQ5pI2Dw?usp=sharing).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
