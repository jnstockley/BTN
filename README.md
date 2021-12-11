# BTTN (Better Twitch.tv Notifications)

# Version 1.62
What's new! Updated dependencies, requires Java 17+. This version is recommended for all users

# Version 1.6
What's new! Updated dependencies, BTTN now checks if an Alertzy API key is valid and sends a test notification, fixes a crash when a Twitch channel name changes, German Support, mitigation techniques to fix Twitch API Timeout Errors, BTTN will send a notification when your Twitch OAuth token has expired..

## Description
This is a Java program that uses [Twitch's API](https://dev.twitch.tv/docs/api/) to determine if a channel is live. The user can add any number of channels, using the program, to a JSON 'config' file. Whenever the program is run it will use the Twitch API to determine if the channel is live. If the status of the channel has changed from not live to live, using data stored in the JSON file, it will send the user a mobile push notification using [Alertzy](https://alertzy.app). I felt the need to build such a program since I started to get annoyed at how delayed the 1st party Twitch notifications could be, especially for channels with large followers. I would miss the beginning of streams and join late with a feeling that I missed a lot of important content. This program can send a notification within a minute of the stream going live, depending on how many channels are being checked, as compared to Twitch's notification which could be delayed as long as 10+ minutes.

## Setup
The program does require some setup in order to get it working. You will need a computer or server that is on 24/7/365 in order to get all the live notifications for your twitch streams. The main pre-requites are to have a computer, either Windows, macOS, or Linux, with Java 8+ installed. After downloading the jar file from the GitHub releases tab, you will need to run the program in setup mode to create your JSON config file. A full setup guide can be found on the WiKi [here](https://github.com/jnstockley/BTTN/wiki/Getting-Started!).

## Running Automatically
In order to get the most out of BTTN you will want to have it run as often as possible, I reccomend once a minute. The easiet way on Linux or macOS is through a cron tab. If you are on windows, then I would reccomend running it using Task Scheduler. A full guide on how to have BTTN run automatically can be found [here](https://github.com/jnstockley/BTTN/wiki/Running-BTTN-Automatically!)

## Why use BTTN over regular Twitch Notifications?
Yes, using the default Twitch.tv live notifications is a lot easier, but the notifications can be very delayed, if they are sent at all. BTTN reliably sends live notifications within a minute of the streamer going live.

## How does BTTN work?
It uses the official Twitch.tv API to determine when a streamer goes live, saves the live status to a file, and sends a notification to your mobile device, using Alertzy a free and open source application used to send notifications to peopole. BTTN also sends notifications through Alertzy if there are updates or if BTTN runs into issues checking the live status.

## Version 1.6 To-Do
1. Check Alertzy Account key to ensure validity.
2. German Language Support

## Features to add!
1. Automatic updates
2. More lanuage support
