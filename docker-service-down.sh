echo ""
echo "  _________                __         .__                          .___                               "
echo "  \_   ___ \  ____   _____/  |______  |__| ____   ___________    __| _/______  _  ______              "
echo "  /    \  \/ /  _ \ /    \   __\__  \ |  |/    \_/ __ \_  __ \  / __ |/  _ \ \/ \/ /    \             "
echo "  \     \___(  <_> )   |  \  |  / __ \|  |   |  \  ___/|  | \/ / /_/ (  <_> )     /   |  \            "
echo "   \______  /\____/|___|  /__| (____  /__|___|  /\___  >__|    \____ |\____/ \/\_/|___|  / /\  /\  /\ "
echo "          \/            \/          \/        \/     \/             \/                 \/  \/  \/  \/ "

sleep 3

cd ./auth-service
./docker-service-down.sh &
cd ..

cd ./chat-service
./docker-service-down.sh &
cd ..


cd ./voice-service
./docker-service-down.sh &
cd ..


cd ./openvidu-service
./docker-container-down.sh &
cd .. 



cd ./nginx-service
./docker-service-down.sh &
cd ..