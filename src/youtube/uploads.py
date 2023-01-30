from googleapiclient.http import HttpRequest
from dateutil import parser, tz

import isodate

import secrets
from helper.data import write, read
from youtube.auth import APIKeys, create_youtube_service

keys = APIKeys(secrets.yt_api_keys)

previous_uploads = read('data/youtube/uploads.json')


class YouTubeChannels:
    """
    TODO
    Make compatible with with 50 channel_id limit

    Possibly make separate function that splits channels into list of 50 max,
    then calls `get_data`
    """

    # TODO Determine better way to authenticate with YouTube Data API (possibly in __init__.py)

    # , keys: APIKeys
    @staticmethod
    def get_data(channel_ids: list[str]) -> list[dict]:
        youtube = create_youtube_service(keys.next_key())

        request: HttpRequest = youtube.channels().list(
            part="contentDetails,statistics,snippet",
            id=channel_ids,
            maxResults=50
        )

        response: dict = request.execute()

        if response['pageInfo']['totalResults'] == len(channel_ids):
            return response['items']

        return [{"error": "Results is different then supplied number of channel_ids"}]


class YouTubeChannel:

    def __init__(self, channel_data: dict):
        self.channel_id = ""

        self.channel_name = ""

        self.playlist_id = ""

        self.previous_uploads: int = -1

        self.current_uploads: int = -1

        self.latest_upload: YouTubeUpload = None

        self.parse_data(channel_data)

    def __repr__(self):
        return f"[channel_id = {self.channel_id}, playlist_id = {self.playlist_id}, previous_uploads = " \
               f"{self.previous_uploads}, current_uploads = {self.current_uploads}, Latest Upload = {self.latest_upload}]"

    def parse_data(self, channel_data: dict):
        if "error" in channel_data:
            print(channel_data)
            exit(1)
        self.channel_id = channel_data['id']
        self.playlist_id = channel_data['contentDetails']['relatedPlaylists']['uploads']
        self.previous_uploads = previous_uploads[self.channel_id]
        self.current_uploads = int(channel_data['statistics']['videoCount'])
        self.channel_name = channel_data['snippet']['title']
        if self.current_uploads > self.previous_uploads:
            self.get_latest_upload()

    def get_latest_upload(self):
        self.latest_upload = YouTubeUpload(self.playlist_id)


class YouTubeUpload:

    def __init__(self, playlist_id: str):
        self.playlist_id: str = playlist_id

        self.upload_id: str = self.get_upload_id()

        self.uploaded_at: str = "1970-1-1 00:00:00"

        self.title: str = ""

        self.length: float = -1.0  # in seconds

        self.thumbnail_url: str = ""

        self.short: bool = False

        self.livestream: bool = False

        upload_data = self.get_upload_data()

        self.parse_data(upload_data)

    def __repr__(self):
        return f"[Upload ID = {self.upload_id}, Uploaded At: {self.uploaded_at}, Title = {self.title}" \
               f"Length = {self.length}, Thumbnail URL = {self.thumbnail_url}, Short = {self.short}, " \
               f"Livestream = {self.livestream}]"

    # TODO Determine better way to authenticate with YouTube Data API

    def get_upload_id(self) -> str:
        youtube = create_youtube_service(keys.next_key())

        request: HttpRequest = youtube.playlistItems().list(
            part="contentDetails",
            playlistId=self.playlist_id,
            maxResults=1
        )

        response: dict = request.execute()

        return response['items'][0]['contentDetails']['videoId']

    def get_upload_data(self) -> dict:
        youtube = create_youtube_service(keys.next_key())

        request: HttpRequest = youtube.videos().list(
            part="contentDetails,snippet,liveStreamingDetails",
            id=self.upload_id,
            maxResults=1
        )

        response: dict = request.execute()

        return response['items'][0]

    def parse_data(self, upload_data: dict):
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


def write_data(channels: list[YouTubeChannel]):
    data = {}
    for channel in channels:
        data[channel.channel_id] = channel.current_uploads
    if data:
        write('data/youtube/uploads.json', data)


'''
TODO
1. Get YT Channel Name DONE
2. Parse YouTube Upload Data DONE
3. Check if Short DONE
4. Determine if YouTube Upload contains live streams or not? It does, use `liveStreamingDetails` and check if it exists
'''
