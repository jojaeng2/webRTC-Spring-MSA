git pull origin main
cd ./auth-service
mvn clean package
cd ..

cd ./chat-service
mvn clean package
cd ..

cd ./voice-service
mvn clean package
cd ..

