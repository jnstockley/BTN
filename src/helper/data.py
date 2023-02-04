import json
import os
import logging

logger = logging.getLogger(f"BSN:{__name__}")


def write(file: str, data: dict):
    path = os.path.split(file)[0]
    if not os.path.exists(path):
        os.makedirs(path)
        logger.debug(f"{path} doesn't exist, creating {path}")
    with open(file, 'w+') as json_file:
        json.dump(data, json_file)
        logger.debug(f"Wrote {data} to {json_file}")


def read(file: str) -> dict:
    try:
        with open(file, 'r') as json_file:
            logger.debug(f"Read data from {json_file}")
            return json.load(json_file)
    except FileNotFoundError:
        logger.error(f"{file} not found, check the path or that it exists!")
        exit(1)
