# BTTN
This Java program mimicks the Twitch live notifications, but with drastically lower delay.

This is a contiuation of the nightly version of BSN but written in Python3.

The program will make a call to the YouTube Data V3 API every 10 seconds to determine if the selected channels have uploaded a new of not, then it will send a notification using apprise.

It is reccomened to use Python 3.11+ due to the speed improvments introduced in that version.
