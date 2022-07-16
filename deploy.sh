echo ''
echo "###################################################################"
echo "###################################################################"
echo "################### Docker Container up start!! ###################"
echo "###################################################################"
echo "###################################################################"
echo ''
echo ''

cd ./auth-service
./deploy.sh &
cd ..

cd ./chat-service
./deploy.sh &
cd ..


cd ./voice-service
./deploy.sh & 
cd ..


cd ./openvidu-service
./deploy.sh &
cd .. 


sleep 50

cd ./nginx-service
./deploy.sh &
cd ..