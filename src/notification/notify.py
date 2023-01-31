import apprise

from secrets import notifications
from youtube.uploads import YouTubeChannel


def new_upload(channels: list[YouTubeChannel]):
    msg = f"""\
        {channels[0].channel_name} has uploaded a new YouTube Video!</br>
        {channels[0].latest_upload.title}</br>
        https://www.youtube.com/watch?v={channels[0].latest_upload.upload_id}</br>
        </br>
        Diagnostic Data:</br>
        {channels}
    """

    notification = apprise.Apprise()

    notification.add(notifications)

    notification.notify(
        body=msg,
        title=f"{channels[0].channel_name} has uploaded a new YouTube Video!"
    )
