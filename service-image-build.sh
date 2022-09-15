#!/bin/bash
echo ''
echo "  .__                                ___.         .__.__       .___           __                 __  ._._. "
echo "  |__| _____ _____     ____   ____   \_ |__  __ __|__|  |    __| _/   _______/  |______ ________/  |_| | | "
echo "  |  |/     \\__  \   / ___\_/ __ \   | __ \|  |  \  |  |   / __ |   /  ___/\   __\__  \\_  __ \   __\ | | "
echo "  |  |  Y Y  \/ __ \_/ /_/  >  ___/   | \_\ \  |  /  |  |__/ /_/ |   \___ \  |  |  / __ \|  | \/|  |  \|\| "
echo "  |__|__|_|  (____  /\___  / \___  >  |___  /____/|__|____/\____ |  /____  > |__| (____  /__|   |__|  ____ "
echo "           \/     \//_____/      \/       \/                    \/       \/            \/             \/\/ "
echo ''

sleep 4

cd ./auth-service
./service-image-build.sh &
cd ..

sleep 4

cd ./chat-service
./service-image-build.sh &
cd ..

sleep 4

cd ./voice-service
./service-image-build.sh &
cd ..


