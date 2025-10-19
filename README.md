# steamdeploy

Build automation for deployment to steamworks

* [core](core): Java API for uploading to steamworks
* [steamdeploy-maven-plugin](steamdeploy-maven-plugin): Maven plugin for uploading to steamworks

## Authentication

There are three routes to logging in to steam on the command line:

### Persistent installation

Steam will cache a successful login to a configuration file, so logging in again from the same
installation only requires a username.

This is easily achieved if building from a local machine, but is less convenient in the context of
CI.

### Username and password

The Steamguard 2FA system is mandatory for all users, so logging in with username and password will
also require either of:

* typing in an emailed token
* responding to a challenge on the user's steam app

This method might seem attractive if your uploading user is set up for app-based 2FA and you're
happy to handle the challenges. Bear in mind that Steam will issue additional challenges if it
detects geographic anomalies in the user's logins, which is likely to happen in CI contexts.

### Username and cached authentication

If we capture the cached authentication from a successful manual login, we can inject it into new
installations.

To capture the cached login: create an instance
of [the SteamCMD executable](https://developer.valvesoftware.com/wiki/SteamCMD), run `steamcmd` and
log in, e.g.:

```
$ steamcmd +login <your username> <your password> +quit
```

<details>
<summary>output</summary>

```
Redirecting stderr to 'C:\Users\This PC\Documents\steamcmd\logs\stderr.txt'
Logging directory: 'C:\Users\This PC\Documents\steamcmd/logs'
[  0%] Checking for available updates...
[----] Verifying installation...
Steam Console Client (c) Valve Corporation - version 1757650979
-- type 'quit' to exit --
Loading Steam API...OK
Logging in using username/password.
Logging in user '<your username>' [U:1:0] to Steam Public...
This computer has not been authenticated for your account using Steam Guard.
Please check your email for the message from Steam, and enter the Steam Guard
 code from that message.
You can also enter this code at any time using 'set_steam_guard_code'
 at the console.
Steam Guard code:<the guard code>
OK
Waiting for client config...OK
Waiting for user info...OK
Unloading Steam API...OK
```

Depending on the configuration of the user you'll need to enter an emailed code or
respond to the app-based authentication challenge

</details>

Check that credentials have been cached:

```
$ steamcmd +login <your username> +quit
```

<details>
<summary>output</summary>

```
Redirecting stderr to 'C:\Users\This PC\Documents\steamcmd\logs\stderr.txt'
Logging directory: 'C:\Users\This PC\Documents\steamcmd/logs'
[  0%] Checking for available updates...
[----] Verifying installation...
Steam Console Client (c) Valve Corporation - version 1757650979
-- type 'quit' to exit --
Loading Steam API...OK
Logging in using cached credentials.
Logging in user '<your username>' [U:1:828553312] to Steam Public...OK
Waiting for client config...OK
Waiting for user info...OK
Unloading Steam API...OK
```

</details>

Capture the config that contains the credentials:

```
cat config/config.vdf | base64 > cfgb64.txt
```

Note that the exact location of the `config.vdf` file that contains the authentication details is
platform-specific.
Check out the [`Platform` enum](core/src/main/java/dev/flowty/steamdeploy/Platform.java) in this
project for details.

The contents of `cfgb64.txt` can be stored as a secret on the CI system and passed to the plugin as
an environment variable. 
