cd ~/git/iws-hw2-ms2
sudo ant build
sudo cp servlet.war /usr/share/jetty/webapps/
cd /usr/share/jetty
sudo java -jar start.jar

