import os
import sys
import time
import logging
import datetime
import traceback
import tzlocal
from dateutil import parser
from dateutil.tz import tz

import secrets
from helper.data import read, write
from notification.notify import Notification
from youtube.uploads import YouTubeChannels

'''
    TODO
    1. Add comments for 
    '''

logger = logging.getLogger("BSN")


def main():

    # 2023-02-05T18:00:25Z

    '''upload_date = parser.parse("2023-02-05T18:00:25Z").astimezone(tz.tzlocal())

    now = datetime.datetime.now(tzlocal.get_localzone())

    previous = now - datetime.timedelta(days=1)

    print(now)

    print(upload_date)

    print(previous)

    print(previous < upload_date < now)

    exit(0)'''

    os.makedirs('logs/', exist_ok=True)

    logging.basicConfig(
        level=logging.DEBUG,
        format="[%(asctime)s] %(levelname)s %(name)s:%(funcName)s:%(lineno)s - %(message)s",
        datefmt='%Y-%m-%d %I:%M:%S %p',
        handlers=[
            logging.FileHandler(f'logs/BSN-{datetime.datetime.now()}.log'),
            logging.StreamHandler()
        ]
    )

    file = 'data/youtube/uploads.json'

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

        logger.info("Sleeping for 10 seconds...")
        time.sleep(10)


def exception_handler(type, value, tb):
    logging.error("Uncaught exception: {0}".format(str(value)))
    logging.error("".join(traceback.format_exception(type, value, tb)))


if __name__ == '__main__':
    sys.excepthook = exception_handler
    main()
