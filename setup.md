# First
    Baskup the folder storing your old BTTN program. This will become useful in case this new version breaks anything. Also before starting please install the [redis database](https://redis.io/download) or get a free cloud account [here](https://app.redislabs.com/). Also, please make sure to download and install the latest version of Java 17.

# Setting up BTTN
    1. Download the BTTN.jar from the GitHub repo.
    2. Create a folder called `BTTN` in your home folder
        Linux - `/home/{username}`
        Windows - `C:\Users\{username}`
        MacOS - `/Users/{username}`
    3. Download the `BTTN.service` file
        This will only work on Linux and possibly macOS systems
        This file also requires some minor tweaks
    4. Open the BTTN.service file and change this line
        WorkingDirectory=/home/ubuntu/BTTN/ -> WorkingDirectory=/home/{username}}/BTTN/
        ExecStart = java -jar /home/ubuntu/BTTN/BTTN.jar -> ExecStart = java -jar /home/{username}}/BTTN/BTTN.jar
    5. On linux type `sudo cp BTTN.service /etc/systemd/system/` and `sudo systemctl daemon-reload`
        This let's linux know that this is a service and how to run it
    6. After this make sure to enable the service by typing
        `sudo systemctl enable BTTN`
    7. After follwing the steps in `Configure BTTN` then start the service by typing
        `sudo systemctl start BTTN`
        NOTE: After making configuration changes, it is reccomended to restart the service by typing `sudo systemctl restart BTTN`

# Configure BTTN
    1. Run BTTN with the flags/arguments `-add -redis`
        This command will ask for the login information for your redis database
    2. Then run any of the following command to populate the database with your auth data
        - `-add -alertzy`
        - `-add -youtubeAuth`
        - `-add twitchAuth`
    3. After setting up your auth data now you can populate the database with your channels to be checked. Here are the commands to run
        - `-add -youtube`
        - `-add -youtubeLive`
        - `-add -twitch`
    4. If you want to update or remove any channels replace add with either `-remove` or `-update`
        NOTE: This does not work for youtube or youtubeLive right now but will in future versions
    5. You are all set know you can run BTTN

# Helpful Note
    To make running BTTN a little easier, you can add an alias to bash. Here is how to do that
        1. Open your `~.bashrc` file
        2. At the end add `alias BTTN='java -jar /home/{username}/BTTN/BTTN-4j.jar'`
        3. Then type `source ~/.bashrc` to load the changes
        4. Now to access the BTTN CLI type `BTTN {args}`