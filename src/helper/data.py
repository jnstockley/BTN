import json
import os


def write(file: str, data: dict):
    path = os.path.split(file)[0]
    if not os.path.exists(path):
        os.makedirs(path)
    with open(file, 'w+') as json_file:
        json.dump(data, json_file)


def read(file: str) -> dict:
    try:
        with open(file, 'r') as json_file:
            return json.load(json_file)
    except FileNotFoundError:
        print(f"{file} not found, check the path or that it exists!")
        exit(1)
