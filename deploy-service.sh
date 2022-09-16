git pull origin main


echo ''
echo "  _________                __         .__                                               __                 __  ._._. "
echo "  \_   ___ \  ____   _____/  |______  |__| ____   ___________   __ ________     _______/  |______ ________/  |_| | | "
echo "  /    \  \/ /  _ \ /    \   __\__  \ |  |/    \_/ __ \_  __ \ |  |  \____ \   /  ___/\   __\__  \\_  __ \   __\ | | "
echo "  \     \___(  <_> )   |  \  |  / __ \|  |   |  \  ___/|  | \/ |  |  /  |_> >  \___ \  |  |  / __ \|  | \/|  |  \|\| "
echo "   \______  /\____/|___|  /__| (____  /__|___|  /\___  >__|    |____/|   __/  /____  > |__| (____  /__|   |__|  ____ "
echo "          \/            \/          \/        \/     \/              |__|          \/            \/             \/\/ "
echo ''

sleep 3

cd ./auth-service
./deploy-service.sh &
cd ..

sleep 3

cd ./chat-service
./deploy-service.sh &
cd ..


sleep 3

cd ./voice-service
./deploy-service.sh & 
cd ..

sleep 3

# cd ./openvidu-service
# ./deploy.sh &
# cd .. 


sleep 50

cd ./nginx-service
./deploy-service.sh &
cd ..