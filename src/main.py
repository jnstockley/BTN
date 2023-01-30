import time

from helper.data import read
from notification.email import new_upload
from youtube import uploads
from youtube.uploads import YouTubeChannels, YouTubeChannel, write_data

while True:
    uploads.previous_uploads = read('/home/jackstockley/BTTN/data/youtube/uploads.json')

    channel_ids = list(uploads.previous_uploads.keys())

    data = YouTubeChannels.get_data(channel_ids)

    channels: list[YouTubeChannel] = []

    for channel_data in data:
        channel = YouTubeChannel(channel_data)

        channels.append(channel)

    write_data(channels)

    new_uploads = [channel for channel in channels if channel.latest_upload is not None]

    if new_uploads:
        new_upload(new_uploads)

    print(new_uploads)

    print("Sleeping...")
    time.sleep(45)

    '''
    TODO
    1. Ensure API Keys are used equally
    2. Determine checking for livestreams
    3. Ensure shorts checking works
    4. Refactor code to work better
    5. Add comments for 
    '''
