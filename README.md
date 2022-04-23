# BTTN (Better Twitch.tv Notifications)

## Version 2.0 Update 12!!
I am currently testing a new version of BTTN that strickly checks if a new YouTube video is uploaded and makes sure it's classifed correctly, i.e, YouTube Premiere, YouTube short or a regular video. I am going to test this for at least a week to make sure all parts of it work as expected and then Ill implement the remaing parts and test. FYI when this new version is released, it will not be compatible with ANY previous versions of BTTN, and sadly all data will need to be re-entered maually.

## Version 2.0 Update 11!
Sorry for the lack of updates! This project is not dead. I have been very busy latey and not super motivated to work on this project to be honest. The main reason is becuase I believe the current version has become unmanagable for me to work on. With that said, I am currently in the process of writing this from the ground up, AGAIN. This time I am using the Google API java client to hopefully keep the code more manageable and under control as well as improve reliability. No time frame on the next update sadly.

## Version 2.0-Alpha2!!!
The seconds alpha version of BTTN-2 has been released. This version still have plenty of bugs and needs lots of love. Only use this version if you are ok with hiccups while setting it up and running the program. This version is very different from the previous version and thus and new steps needed to setup the program. Please read over the [setup.md](setup.md) file

## Version 2.0 Update 10!!!
I am very excited to release the next version of BTTN. With that said, the next alpha version won't be released for at least a week, if there are no major issues. This new version is drastically different from the previous version. The main improvemnets are that it runs as a service, linux only for now, switches away from storing data in files and moves to storing data in a redis database, as well as a much improved way to setup BTTN. More infor on how to do all of this when the next version is released.

## Version 2.0 Update 9!!!
I am pleased to report I am testing a new version that, if all goes well, will be public. This version includes a bog fix to YouTube premieres, a lower latency way of getting Twitch.tv live stream statuses. This version will be tested throughout this week to make sure all features works since the way the program is being run has drastically changed. I'll post an update if this version works, or has any issues.

## Version 2.0 Update 8!!!
After almost giving up on this new improvment to the program I am happy to report that I MIGHT have something working. I have just started some early testing to make sure that it works. I will update with more info if this works and how I am acomplishing this as well as any decreased delay.

## Version 2.0 Update 7!!!
Updates to this project will be delayed since I am currently looking at using either WebSockets or WebHooks to have data sent to BTTN from YouTube and Twitch instead of getting the data every minute from them. Right now I am leaning toward WebSockets but that might change. Stay tuned.

## Version 2.0 Update 6!!!
Sorry for the long update gap. Updates to this project will start to become longer and with lesser changes. Since I am a college student I am going to focus on School and work over this project for the time being. I will try to post some updates here and there but progress will slow drastically. With that said, I am currently testing a verision of BTTN V2 with YouTube Premiere support. I found a way to determine if a video is a "Premiere" or not and want to make sure it's accurate. Not timieline of when I will make this version public. Stay tuned.

## Version 2.0-Alpha1!!!
I have just published the first alpha version of BTTN V2. Please read the README in the root of the directory on how to setup BTTN and read the README in the config examples folder on how to configure the config and data files. Any issues, post an issue with the `version2` lable on the issue tracker.

## Version 2.0 Update 5!!!
In the next day or so I plan on releasing source code as well as a compiled jar file for the first 2.0 release. From this point further all version 2.0 updates will be released here and not on the main branch. I will also make a section here for known bugs, what I am working on for the next version, how to setup the current version and any other updates. Stays tuned!

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
  - [X] Youtube Video Upload support
  - [X] Youtube Live Stream support
  - [X] Youtube Shorts support
  - [ ] Possible New Name (Suggestions Welcome)

## Currently Working on
  - [ ] Adding support for removing and updating YouTube and YouTube live channels
  - [ ] Better argument handling
  - [ ] Better error messages
  - [ ] Improved speed, especially for YouTube Live Channels

## Current Limitations
 - BTTN can only check for 300 YouTube channels. This is due to the YouTube quota. I don't anticipate anybody running into this issue, but I do have plans to possibly add a feature to get around this. (Might not be a thing with Alpha-2, need to look into it)

## Known bugs
Here is a list of known bugs, a check marks means that they have been fixed, an empty check box mean they are affecting the most current version
  - [ ] BTTN will crash at random time intervals and restarts automatically
  - [ ] Info/error messages are inconsitent or wrong

## What you need for BTTN to work
 1. Java 17+
 2. A [YouTube V3 Data API key](https://developers.google.com/youtube/v3/getting-started)
 3. A [Twitch.TV client ID and client secret](https://dev.twitch.tv/docs/v5)
 4. An [Alertzy API Key](https://alertzy.app)
 5. A redis server locally or cloud
 6. A computer/server running 24/7
