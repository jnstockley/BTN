# BTTN (Better Twitch.tv Notifications)

## Version 2.0 Update 4!!!
I believe if all goes well during my testing this week that I am ready to release the version version of version 2.0. Right now the program checks Twitch.tv live channels, YouTube video uploads (is able to determine if its a regular YouTube video or a short) and YouTube live streams. Right now the only major issues is that if a youtube is "premiered" I believe you won't be notified until the premier is over, (need to test this, I know for a fact it won't notify you when the permier is starting but need to make sure it still notifies you after the premier). The program also creates a folder in the user's home folder called "BTTN" where all the config and data files are stored. Ill make an update when I release on how to set these up. Upon full release this will all be automated.

## Versioin 2.0 Update 3!!!
I have decided that I want to wait on pushing the first build of BTTN Version 2. I want to do some more testing with YouTube live streams and test how the program works with YouTube premieres and hopefully make a seperate notification for YouTube shorts (no promises). I also want to make the set up slightly more user friendly then how the current build is. In a perfect world, I would love to have the first build out by the end of next week but I am not promising anything since I am not sure how YouTube live streams and YouTube premiere testing will go. Stay tuned!

## Version 2.0 Update 2!!!
After doing some internal testing with my first try at YouTube notification, I found the delay to be too long. I then decided to look into using the YouTube API. After trying to figure out a way to stay under the YouTube quota, I have began work on my second try at getting YouTube notifications. I am hoping to start more internal testing on this version tomorrow to make sure it is light on the YouTube quota, and the delay is on par, or better then YouTubes. If all goes well, I plan on releasing a very early version by the end of this week!

## Version 2.0 Update 1!!!
I have deemed it possible to add Youtube Notifications to BTTN. With that said I have started work on Version 2.0 and such, the current beta of 1.7 has been canceled and the features added will be implemented in Version 2.0. I would also like to provide a progress update. I have started writting the backend for BTTN where it interacts with local files and reached out to the required APIs to detmerine if a streamer is live or new video posted. I am hoping to release a VERY early version, possibly with source code, at the start of 2022.

# What's next for BTTN
Since I am happy with how BTTN works for Twitch.tv notification, I have started some testing to see if it's theoretically possible to do a simple thing for Youtube videos. In a perfect world, I would love to get this working with Youtube live streams as well. So with that said, I am starting work on Version 2.0. This version will most likely take a while to implement since I am planning on re-writing the whole program to better facilitate future versions and new services. Below is a checklist with the featutres I plan to add, as well if they are in-progress, done, or canceled. Please create an issue if there are any features that should be added.

## Version 2.0 Progress
  - [ ] All current features in 1.62
  - [ ] Youtube Video Upload support
  - [ ] Youtube Live Stream support
  - [ ] Possible New Name (Suggestions Welcome)

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
