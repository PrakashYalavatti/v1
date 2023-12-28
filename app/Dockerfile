# Use the official Android base image for Android 12 (API level 34)
FROM openjdk:17-jdk-slim

# Set environment variables for Android
ENV ANDROID_HOME="/opt/android/sdk" \
    SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-7302050_latest.zip" \
    GRADLE_VERSION="8.1.2" \
    GRADLE_HOME="/opt/gradle"

# Install required dependencies
RUN apt-get update && \
    apt-get install -y wget unzip && \
    rm -rf /var/lib/apt/lists/*

# Download and install Android SDK
RUN mkdir -p ${ANDROID_HOME} && \
    wget -q ${SDK_URL} -O android-sdk.zip && \
    unzip -q android-sdk.zip -d ${ANDROID_HOME} && \
    rm android-sdk.zip

# Add Android SDK and Gradle to the system PATH
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/emulator:${ANDROID_HOME}/platform-tools:${GRADLE_HOME}/bin"

# Install required Android components
RUN yes | sdkmanager --licenses && \
    sdkmanager "platforms;android-34" && \
    sdkmanager "build-tools;34.0.3" && \
    sdkmanager "extras;google;m2repository" && \
    sdkmanager "extras;android;m2repository"

# Download and install Gradle
WORKDIR ${GRADLE_HOME}
RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip && \
    unzip -q gradle-${GRADLE_VERSION}-bin.zip && \
    rm gradle-${GRADLE_VERSION}-bin.zip

# Set the working directory to /app
WORKDIR /app

RUN ls -R /var/app/build/outputs

# Copy the current directory contents into the container at /app
COPY . /var

# Set up any other build configurations or commands as needed

# Clean up
RUN apt-get remove -y wget unzip && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

# Default command
CMD ["bash"]