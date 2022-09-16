echo "               __  .__                __                 __     "
echo " _____   __ ___/  |_|  |__     _______/  |______ ________/  |_  "
echo " \__  \ |  |  \   __\  |  \   /  ___/\   __\__  \\_  __ \   __\ "
echo " / __ \|  |  /|  | |   Y  \  \___ \  |  |  / __ \|  | \/|  |    "
echo "(____  /____/ |__| |___|  / /____  > |__| (____  /__|   |__|    "
echo "     \/                 \/       \/            \/               "

mvn clean package
docker-compose -f docker-compose.local.yml build
docker-compose -f docker-compose.local.yml up

echo "                __  .__                __                 "
echo " _____   __ ___/  |_|  |__     _______/  |_  ____ ______  "
echo " \__  \ |  |  \   __\  |  \   /  ___/\   __\/  _ \\____ \ "
echo "  / __ \|  |  /|  | |   Y  \  \___ \  |  | (  <_> )  |_> > "
echo " (____  /____/ |__| |___|  / /____  > |__|  \____/|   __/  "
echo "      \/                 \/       \/              |__|      "