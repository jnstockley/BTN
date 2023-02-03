import time

import secrets
from helper.data import read, write
from notification.notify import Notification
from youtube.uploads import YouTubeChannels

file = 'data/youtube/uploads.json'

'''
    TODO
    1. Add comments for 
    '''


def main():

    notification = Notification(secrets.notifications)

    notification.create(starting_message=True).send()

    while True:

        channels = YouTubeChannels(read(file))

        if len(channels.uploads) > 0:
            notification.create(youtube_upload=channels.uploads).send()
        if len(channels.livestreams) > 0:
            notification.create(youtube_livestream=channels.livestreams).send()
        if len(channels.shorts) > 0:
            notification.create(youtube_short=channels.shorts).send()

        write(file, channels.channel_file_repr)

        print("Sleeping...")
        time.sleep(10)


if __name__ == '__main__':
    main()
