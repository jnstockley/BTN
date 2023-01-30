from collections import deque

import googleapiclient.discovery
from googleapiclient.errors import HttpError
from googleapiclient.http import HttpRequest


class APIKey:

    def __init__(self, key: str):
        self.key: str = key

        self.valid: bool = self.valid_key()

    def __repr__(self):
        return f"""[key={self.key}, valid={self.valid}]"""

    def valid_key(self):
        """
        Makes a simple request to the YouTube v3 data API using the provided API Key
        and checks if the API is valid or not
        :return: True if the provided API is valid, False otherwise
        """
        youtube = googleapiclient.discovery.build("youtube", "v3", developerKey=self.key)

        request: HttpRequest = youtube.channels().list(
            part="id",
            id="UC_x5XG1OV2P6uZZ5FSM9Ttw"
        )

        try:
            response: dict = request.execute()
            return response['items'][0]['id'] == "UC_x5XG1OV2P6uZZ5FSM9Ttw"
        except HttpError as e:
            if e.reason == "API key not valid. Please pass a valid API key.":
                return False
        return False


class APIKeys:

    def __init__(self, keys: list[str]):
        """
        Accepts a list of YouTube Data V3 API Keys, ensures they are valid, and appends
        them to the `keys` deque.
        Checks that at least one valid YouTube Data V3 API Key was provided
        :param keys: List of YouTube Data V3 API Keys
        """
        if len(keys) < 1:
            print("No YouTube Data V3 API Keys were supplied!")
            exit(1)

        self.keys: deque[APIKey] = deque()

        for key in keys:
            api_key = APIKey(key)
            if api_key.valid:
                self.keys.append(api_key)

        if len(self.keys) < 1:
            print("No Valid YouTube Data V3 API Keys were supplied!")
            exit(1)

    def __repr__(self):
        return f"""{list(self.keys)}"""

    def next_key(self):
        """
        Pops the oldest key from the `keys` deque, adds it to the end of the deque
        and returns the str representation of the API Key
        :return: The oldest API Key, in str form
        """
        key: APIKey = self.keys.popleft()
        self.keys.append(key)
        return key.key


def create_youtube_service(key: str):
    return googleapiclient.discovery.build("youtube", "v3", developerKey=key)
