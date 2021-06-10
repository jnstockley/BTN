# BTTN (Better Twitch.tv Notifications)

# Major update!
I have gone ahead and decided that I will re-write BTTN and be using Alterzy as the notification service! I have re-written the sending notifications part of the program. I have implemented most of the important features that were in the pevious BTTN. I will implement all of the features in the old BTTN in this new version before the next public release. I also plan on releasing a pre-release tomorrow that has the notifications working, while missing translation, logging, automatic updates, and other nice to have features. Please report any and all bugs on the issues tab. Once the next pre-release is published I will make an update here and delet all these old updates! Stay tuned!

# New Service Found
I believe I have found a new service to use for sending push notifications. Its called [Alertzy](https://alertzy.app/). I still plan on doing a little bit more research to see if I can find anything different which might be better but this app seems to have very similar features to Spontit. Right now I am planning on releasing a version 1.1 which supports Alertzy. I still plan on doing a re-write to make the program more stable but I want to get notifications working first. Look for an update within a couple of days.

# Spontit Update!!!
I have sent spontit an email letting them know that their API is down, no idea if they will respond or not! I have released a pre-release BTTN version with email failover support. It is buggy so I reccomend backing up your config file. I am trying to find free cross-platform alternatives to Spontit but have not had much luck. I also am consdering making a web app to support notifications, but that is a last resort. I do still plan on working on BTTN, but will be taking a break until Spontit is back working or I find an alternative to Spontit.

# Important Note!!!
It appears that Spontit is having some issues sending out notifications, giving an HTTP 502 Bad Gateway error code.  Sadly, their is nothing I can do to fix their error code. I do hope to add a backup way to send a notifcation via email or text in the next version. Sorry for any inconveniences.

## Description
This is a Java program that uses [Twitch's API](https://dev.twitch.tv/docs/api/) to determine if a channel is live. The user can add any number of channels, using the program, to a JSON 'config' file. Whenever the program is run it will use the Twitch API to determine if the channel is live. If the status of the channel has changed from not live to live, using data stored in the JSON file, it will send the user a mobile push notification using [Spontit](https://spontit.com). I felt the need to build such a program since I started to get annoyed at how delayed the 1st party Twitch notifications could be, especially for channels with large followers. I would miss the beginning of streams and join late with a feeling that I missed a lot of important content. This program can send a notification within a minute of the stream going live, depending on how many channels are being checked, as compared to Twitch's notification which could be delayed as long as 10+ minutes.

## Setup
The program does require some setup in order to get it working. You will need a computer or server that is on 24/7/365 in order to get all the live notifications for your twitch streams. The main pre-requites are to have a computer, either Windows, macOS, or Linux, with Java 8+ installed. After downloading the jar file from the GitHub releases tab, you will need to run the program in setup mode to create your JSON config file. A full setup guide can be found on the WiKi [here](https://github.com/jnstockley/BTTN/wiki/Getting-Started!).

## Running Automatically
In order to get the most out of BTTN you will want to have it run as often as possible, I reccomend once a minute. The easiet way on Linux or macOS is through a cron tab. If you are on windows, then I would reccomend running it using Task Scheduler. A full guide on how to have BTTN run automatically can be found [here](https://github.com/jnstockley/BTTN/wiki/Running-BTTN-Automatically!)

## Why use BTTN over regular Twitch Notifications?
Yes, using the default Twitch.tv live notifications is a lot easier, but the notifications can be very delayed, if they are sent at all. BTTN reliably sends live notifications within a minute of the streamer going live.

## How does BTTN work?
It uses the official Twitch.tv API to determine when a streamer goes live, saves the live status to a file, and sends a notification to your mobile device, using Spontit a free and open source application used to send notifications to peopole. BTTN also sends notifications through spontit if there are updates or if BTTN runs into issues checking the live status.

## Features to add!
1. Automatic updates
2. More lanuage support
3. Notifications to launch twitch stream on mobile devices
4. Youtube notifications (possible no promise)
