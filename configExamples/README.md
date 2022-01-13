## Twitch.json
1. Must be valid json
2. Can have any number of channel names
3. Set the value of the channel name to `false`

## Youtube.json
1. Must be valid json
2. Must be less then 300 channels
3. To get the playlistID replace the first `C` with `U` ex. `UCsodRQuyfNrnM0Q8Qtjzaww -> UUsodRQuyfNrnM0Q8Qtjzaww`
4. Set the value of the playlistID to `0`

# YoutubeLive.json
1. Must be valid json
2. Can be any number of channel id's, the more channels checked the slower the program runs
3. To get the channelID find a video from the channel and click on the channel name below the video, in the address bar the playlist ID is after `/channel/`
  Ex. `https://www.youtube.com/channel/UCsodRQuyfNrnM0Q8Qtjzaww, channelID = UCsodRQuyfNrnM0Q8Qtjzaww`
4. Set the value of the channelID to `false`
