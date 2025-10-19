# core

![Maven Central Version](https://img.shields.io/maven-central/v/dev.flowty.steamdeploy/core)
[![javadoc](https://javadoc.io/badge2/dev.flowty.steamdeploy/core/javadoc.svg)](https://javadoc.io/doc/dev.flowty.steamdeploy/core)

Java API for uploading to steamworks.

## Usage

### Dependency

```xml

<project>
  <dependencies>
    <dependency>
      <groupId>dev.flowty.steamdeploy</groupId>
      <artifactId>core</artifactId>
      <version>x.y.z</version>
    </dependency>
  </dependencies>
</project>
```

### Build an Auth object

You've got three options here:

```java
// I'm going to be re-using a steamcmd installation that already has an 
// authenticated config.vdf file
Auth auth = new Auth("my_user_name");

// The account has been set up with app-based steamguard and I'm happy to 
// respond to the 2FA prompts 
Auth auth = new Auth("my_user_name", "my_password");

// I've configured a STEAM_CONFIG_VDF environment variable with the
// base64-encoded contents of a successfully-authenticated config.vdf file
Auth auth = new Auth("my_user_name", InjectableFile.ofB64(System.getenv("STEAM_CONFIG_VDF")));
```

If you're operating in the context of a CI process, then the third option is by far the easiest.
See the [main readme](..) for how to generate the secret content.

### Define an `appBuild` VDF

If you don't have any special requirements then there's a convenience method:

```java
InjectableFile appBuild = InjectableFile.appBuild(1234, "", true, false, 5678);
```

that will result in an appBuild VDF like so:

```
"AppBuild"
{
  "AppId" "1234"
  "Desc" ""
  "verbose" "1"
  "preview" "0"
  "ContentRoot" "..\content\"
  "BuildOutput" "..\output\"
  "Depots"
  {
    "5678"
    {
      "FileMapping"
      {
        "LocalPath" "*"
        "DepotPath" "."
        "recursive" "1"
      }
    }
  }
}
```

If you want something different then you can do:

```java
String vdfContent = load_resource_or_whatever();
InjectableFile apBuild = InjectableFile.of(vdfContent);
```

### Build a SteamCMD instance

```java
Path install = Paths.get("path/to/install/directory");
SteamCMD steamCMD = new SteamCMD(install);
```

This will:

* Download the steamCMD executable for the current platform
* Extract it to the specified installation directory
* Run the steamCMD executable to auto-update the installation

You can expect construction to take a few seconds to complete.

If the installation directory already exists then it is assumed to contain a steamcmd installation
and we won't bother downloading a new one. This might be a convenient way to tackle the
authentication issue, if you can preserve an logged-in installation for every deployment.

### Upload to steam

```java
Path appDir = Paths.get("path/to/compiled/application");
Result result = steamCMD.deploy(auth, appDir, appBuild);
```

### Check the result

```java
if( result.getStatus() == 0 ) {
  System.out.println( "Deployment was successful!" );
}
else {
  System.err.println( "Deployment failed!" );
  System.err.println( result.stdOut() );
}
```