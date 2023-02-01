import time

from helper.data import read, write
from notification.notify import new_upload
from youtube.uploads import YouTubeChannels

file = 'data/youtube/uploads.json'

'''
    TODO
    1. Ensure API Keys are used equally
    2. Determine checking for livestreams WORKS
    3. Ensure shorts checking works WORKS
    4. Refactor code to work better DONE
    5. Add comments for 
    '''


def main():
    while True:

        channels = YouTubeChannels(read(file))

        if len(channels.recently_uploaded) > 0:
            new_upload(channels.recently_uploaded)

        write(file, channels.channel_file_repr)

        print("Sleeping...")
        time.sleep(45)


if __name__ == '__main__':
    main()
