export YOUTUBE_PLAYLISTS=/config/youtube/playlists.json

if [ ! -e $YOUTUBE_PLAYLISTS ]; then java -jar BSN.jar -V; fi