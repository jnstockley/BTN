import isodate
from dateutil import parser
from dateutil.tz import tz
from googleapiclient.http import HttpRequest

import secrets
from youtube.auth import create_youtube_service, APIKeys

keys = APIKeys(secrets.yt_api_keys)


class YouTubeChannels:

    def __init__(self, channels: dict):
        self.channels: list[YouTubeChannel] = []

        self.recently_uploaded: list[YouTubeChannel] = []

        self.channel_file_repr: dict = {}

        channel_ids = list(channels.keys())

        split_channel_ids = [channel_ids[i:i + 50] for i in range(0, len(channel_ids), 50)]

        for split_channels in split_channel_ids:
            channels_data = self.get_data(split_channels)
            for channel_data in channels_data:
                channel = YouTubeChannel(channel_data, channels)
                self.channels.append(channel)
                self.channel_file_repr[channel.channel_id] = {'uploads': channel.current_upload_amount,
                                                              'upload_id': channel.current_upload_id}
                if channel.latest_upload is not None and channel.latest_upload.livestream is False:
                    self.recently_uploaded.append(channel)

    def __repr__(self):
        return f"YouTube Channels: {self.channels}"

    @staticmethod
    def get_data(channel_ids: list[str]) -> list[dict]:
        youtube = create_youtube_service(keys.next_key())

        request: HttpRequest = youtube.channels().list(
            part="contentDetails,statistics,snippet",
            id=channel_ids,
            maxResults=50
        )

        return request.execute()['items']


class YouTubeChannel:

    def __init__(self, channel_data: dict, previous_uploads: dict):
        self.channel_id = channel_data['id']
        self.playlist_id = channel_data['contentDetails']['relatedPlaylists']['uploads']
        self.previous_upload_id = previous_uploads[self.channel_id]['upload_id']
        self.current_upload_id = self.previous_upload_id
        self.previous_upload_amount = previous_uploads[self.channel_id]['uploads']
        self.current_upload_amount = int(channel_data['statistics']['videoCount'])
        self.channel_name = channel_data['snippet']['title']
        self.latest_upload = None
        if self.current_upload_amount > self.previous_upload_amount:
            self.current_upload_id = self.get_upload_id()
            if self.current_upload_id != self.previous_upload_id:
                self.latest_upload = YouTubeUpload(self.current_upload_id)

    def __repr__(self):
        return f"Channel ID: {self.channel_id}, Playlist ID: {self.playlist_id}, Previous Upload ID: " \
               f"{self.previous_upload_id}, Current Upload ID: {self.current_upload_id}, Previous Upload Amount: " \
               f"{self.previous_upload_amount}, Current Upload Amount: {self.current_upload_amount}, Channel Name: " \
               f"{self.channel_name}, Latest Upload: {self.latest_upload}"

    def get_upload_id(self) -> str:
        youtube = create_youtube_service(keys.next_key())

        request: HttpRequest = youtube.playlistItems().list(
            part="contentDetails",
            playlistId=self.playlist_id,
            maxResults=1
        )

        print("get upload id")

        response: dict = request.execute()

        return response['items'][0]['contentDetails']['videoId']


class YouTubeUpload:

    def __init__(self, upload_id: str):
        self.upload_id = upload_id

        upload_data = self.get_data()

        self.uploaded_at = parser.parse(upload_data['snippet']['publishedAt']).astimezone(tz.tzlocal()) \
            .strftime("%b %d, %Y - %I:%M %p")
        self.title = upload_data['snippet']['localized']['title']
        self.length = isodate.parse_duration(upload_data['contentDetails']['duration']).total_seconds()
        if 'maxres' in upload_data['snippet']['thumbnails']:
            self.thumbnail_url = upload_data['snippet']['thumbnails']['maxres']['url']
        else:
            self.thumbnail_url = upload_data['snippet']['thumbnails']['standard']['url']
        self.short = 61 > self.length > 0
        self.livestream = "liveStreamingDetails" in upload_data

    def __repr__(self):
        return f"Upload ID: {self.upload_id}, Uploaded At: {self.uploaded_at}, Title: {self.title}, Length: " \
               f"{self.length}, Thumbnail URL: {self.thumbnail_url}, Short: {self.short}, Livestream: {self.livestream}"

    def get_data(self) -> dict:
        youtube = create_youtube_service(keys.next_key())

        request: HttpRequest = youtube.videos().list(
            part="contentDetails,snippet,liveStreamingDetails",
            id=self.upload_id,
            maxResults=1
        )

        response: dict = request.execute()

        return response['items'][0]
