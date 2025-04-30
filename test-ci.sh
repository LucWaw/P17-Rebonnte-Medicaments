#!/bin/sh

set -x

echo "Waiting for device to be ready..."
adb wait-for-device

# Vérifie que le device est bien en ligne
adb shell getprop sys.boot_completed | grep 1
while [ $? -ne 0 ]; do
  sleep 1
  adb shell getprop sys.boot_completed | grep 1
done

echo "Device is ready, starting screen recording..."
adb shell 'screenrecord --bugreport /data/local/tmp/testRecording.mp4 > /dev/null 2>&1 &' 
sleep 2

# Build + install debug APK si nécessaire
./gradlew assembleDebug installDebug

# Lancer les tests instrumentés
set +e
./gradlew connectedCheck
TEST_STATUS=$?

echo "Stopping screen recording..."
adb shell 'pkill -INT screenrecord' || true
sleep 2

adb pull /data/local/tmp/testRecording.mp4 ./testRecording.mp4 || echo "Video not pulled"
exit $TEST_STATUS
