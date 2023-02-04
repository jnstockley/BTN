import apprise
import logging
from apprise import Apprise, NotifyFormat

from youtube.uploads import YouTubeChannel

logger = logging.getLogger(f"BSN:{__name__}")


class Notification:

    def __init__(self, servers: list[str]):

        self.apprise: Apprise = apprise.Apprise(servers)

        self.title: str = ""

        self.body: str = ""

        logger.debug("Created new Notification Object")

    def __repr__(self):
        return f"Server: {self.apprise.urls()}, Title: {self.title}, Body: {self.body}"

    def create(self, **kwargs):
        if "starting_message" in kwargs and kwargs["starting_message"] is True:
            self.title = "BSN Started!"
            self.body = "BSN listening for updates!"
            logger.debug(f"Notification Object updated: {self}")
        elif "youtube_upload" in kwargs:
            youtube_upload = list[YouTubeChannel](kwargs["youtube_upload"])
            verb = "has" if len(youtube_upload) == 1 else "have"
            self.title = f'{", ".join([channel.channel_name for channel in youtube_upload])} {verb}' \
                         f' uploaded a new YouTube Video!'
            self.body = self.__setup_youtube_body__(youtube_upload)
            logger.debug(f"Notification Object updated: {self}")
        elif "youtube_livestream" in kwargs:
            youtube_livestream = list[YouTubeChannel](kwargs["youtube_livestream"])
            verb = "has" if len(youtube_livestream) == 1 else "have"
            self.title = f'{", ".join([channel.channel_name for channel in youtube_livestream])} {verb}' \
                         f' gone live on YouTube!'
            self.body = self.__setup_youtube_body__(youtube_livestream)
            logger.debug(f"Notification Object updated: {self}")
        elif "youtube_short" in kwargs:
            youtube_short = list[YouTubeChannel](kwargs["youtube_short"])
            verb = "has" if len(youtube_short) == 1 else "have"
            self.title = f'{", ".join([channel.channel_name for channel in youtube_short])} {verb}' \
                         f' uploaded a new YouTube Short!'
            self.body = self.__setup_youtube_body__(youtube_short)
            logger.debug(f"Notification Object updated: {self}")
        return self

    @staticmethod
    def __setup_youtube_body__(youtube: list[YouTubeChannel]):
        if len(youtube) == 1:
            return f"[{youtube[0].latest_upload.title}](https://youtube.com/watch?v=" \
                   f"{youtube[0].latest_upload.upload_id})"
        else:
            return "[Check them out of YouTube!](https://www.youtube.com/feed/subscriptions)"

    def send(self):
        self.apprise.notify(
            title=self.title,
            body=self.body,
            body_format=NotifyFormat.MARKDOWN
        )
        logger.info(f"Sent Apprise Notification: {self}")
        self.title = ""
        self.body = ""
