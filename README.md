# steamdeploy
Build plugin for deployment to steamworks

## todo

 * maven-plugin
 * gradle-plugin

## Refreshing your authentication VDF

At some point the cached credentials will need to be refreshed.

Using an instance of [the SteamCMD executable](https://developer.valvesoftware.com/wiki/SteamCMD), run `steamcmd` and log in, e.g.:

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
Note that the exact location of the `config.vdf` file that contains the authentication details is platform-specific.
Check out the [`Platform` enum](core/src/main/java/dev/flowty/steamdeploy/Platform.java) in this project for details.

Update the github secret with the contents of `cfgb64.txt`
