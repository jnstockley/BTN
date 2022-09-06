FROM debian:stable-slim

RUN apt update && apt upgrade -y && apt install openjdk-17-jre -y

ADD out/BSN.jar BSN.jar

RUN echo 'alias BSN="java -jar BSN.jar"' >> ~/.bashrc

RUN mkdir /config

ENV ALERTZY_KEY = /config/alertzy/keys.json

ENV TWITCH_KEY = /config/twitch/keys.json

ENV YOUTUBE_KEY = /config/youtube/keys.json

ENV TWITCH_CHANNELS = /config/twitch/channels.json

ENV YOUTUBE_CHANNELS = /config/youtube/channels.json

ENV YOUTUBE_PLAYLISTS = /config/youtube/playlists.json

#CMD echo "Enter Twitch Username: "

#CMD read twitchUsername

COPY setup.sh .

RUN chmod +x setup.sh

RUN ./setup.sh

#CMD if [ ! -e $ALERTZY_KEY ]; then java -jar BSN.jar cred alertzy add; fi

#CMD if [ ! -e $TWITCH_KEY ]; then java -jar BSN.jar cred twitch add; fi

#CMD if [ ! -e $YOUTUBE_KEY ]; then java -jar BSN.jar cred youtube add; fi

#CMD if [ ! -e $TWITCH_CHANNELS ]; then java -jar BSN.jar twitch add -n $twitchName; fi

#CMD if [ ! -e $YOUTUBE_CHANNELS ]; then java -jar BSN.jar youtubelive add -f /config/subscriptions.csv; fi

#CMD if [ ! -e $YOUTUBE_PLAYLISTS ]; then java -jar BSN.jar youtube add -f /config/subscriptions.csv; fi

# CMD if [ ! -e $YOUTUBE_PLAYLISTS ]; then java -jar BSN.jar -V; fi


ENTRYPOINT ["java", "-jar", "BSN.jar"]
