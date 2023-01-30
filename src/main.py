from helper.data import read
from notification.email import new_upload
from youtube.uploads import YouTubeChannels, YouTubeChannel, write_data

channel_ids = list(read('data/youtube/uploads.json').keys())

data = YouTubeChannels.get_data(channel_ids)

channels: list[YouTubeChannel] = []

for channel_data in data:
    channel = YouTubeChannel(channel_data)

    channels.append(channel)

write_data(channels)

new_uploads = [channel for channel in channels if channel.latest_upload is not None]

if new_uploads:
    new_upload(new_uploads)
    # print("Not empty")

print(new_uploads)
