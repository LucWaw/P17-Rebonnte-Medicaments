#!/bin/sh

set -x
echo "Starting the screen recording..."

# Start screenrecord in the background on the device
adb shell 'screenrecord --bugreport /data/local/tmp/testRecording.mp4 > /dev/null 2>&1 &' 
# Wait a bit to ensure it starts
sleep 2

# Run instrumentation tests
set +e
./gradlew connectedCheck
TEST_STATUS=$?
echo "Test run completed with status $TEST_STATUS"

# Kill the screenrecord process
echo "Stopping screen recording..."
adb shell 'pkill -INT screenrecord' || true

# Wait until the screenrecord process fully exits
sleep 2

# Pull the video file
adb pull /data/local/tmp/testRecording.mp4 ./testRecording.mp4 || echo "Failed to pull recording"

# Exit with the same status as the test run
exit $TEST_STATUS
