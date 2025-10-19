# steamdeploy-maven-plugin

![Maven Central Version](https://img.shields.io/maven-central/v/dev.flowty.steamdeploy/steamdeploy-maven-plugin)

Maven plugin for uploading to steamworks.

## Usage

```xml

<project>
  <build>
    <plugins>
      <plugin>
        <!-- Uploads the runtime image to steam -->
        <groupId>dev.flowty.steamdeploy</groupId>
        <artifactId>steamdeploy-maven-plugin</artifactId>
        <version>x.y.z</version>
        <configuration>
          <application>${project.build.directory}/image</application>
          <appId>1234</appId>
          <depotId>5678</depotId>
          <user>my_steam_user_name</user>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>deploy</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

The plugin is bound to the `deploy` phase by default, so running `mvn deploy` will provoke the
plugin to execute

This usage assumes that:

* The compiled application files are in `target/image`
* I'm happy with the simple `appBuild` VDF shown in the [core readme](../core/README.md)
* There's an environment variable named `STEAM_AUTH_VDF` that contains the base64-encoded contents
  of a logged-in `config.vdf` file

See the [main readme](..) for how to generate that content.

## Parameters

`mvn dev.flowty.steamdeploy:steamdeploy-maven-plugin:x.y.z:help -Ddetail=true`

```
steamdeploy-maven-plugin x.y.z
  Build tooling for uploading to steamworks

This plugin has 2 goals:

steamdeploy:deploy
  Deploys an application to steam

  Available parameters:

    appId
      Optional: The steam application ID. depotId is also required.
      User property: steamdeploy.appId

    application
      The directory that holds the application to deploy
      Required: Yes
      User property: steamdeploy.application

    authVdfVar (Default: STEAM_AUTH_VDF)
      The name of the environment variable that holds the base64-encoded
      authorised config.vdf content
      User property: steamdeploy.authVdfVar

    depotId
      Optional: The steam depot ID. appId is also required.
      User property: steamdeploy.depotId

    description
      Optional: A description for the build. Default value will detail the OS
      name and current time.
      User property: steamdeploy.description

    install (Default: target/steamcmd)
      Optional: The directory in which to install the steamCMD executable
      User property: steamdeploy.install

    passwordVar (Default: STEAM_PASSWORD)
      The name of the environment variable that holds the steam user's password
      User property: steamdeploy.passwordVar

    preview (Default: false)
      Optional: Skips the upload of compiled application content
      User property: steamdeploy.preview

    script
      Optional: The path to the appBuild install script for your deployed
      application. If this is supplied then all of:
      * appId
      * depotId
      * description
      * verbose
      * preview are forbidden
      User property: steamdeploy.script

    skip (Default: false)
      Optional: Skips plugin execution
      User property: steamdeploy.skip

    source
      Optional: The URL from which to download the steamCMD executable, if the
      defaults don't work for you
      User property: steamdeploy.source

    user
      The steam user name.
      Required: Yes
      User property: steamdeploy.user

    verbose (Default: false)
      Optional: Controls verbose logging for the steam deployment
      User property: steamdeploy.verbose

steamdeploy:help
  Display help information on steamdeploy-maven-plugin.
  Call mvn steamdeploy:help -Ddetail=true -Dgoal=<goal-name> to display
  parameter details.

  Available parameters:

    detail (Default: false)
      If true, display all settable properties for each goal.
      User property: detail

    goal
      The name of the goal for which to show help. If unspecified, all goals
      will be displayed.
      User property: goal

    indentSize (Default: 2)
      The number of spaces per indentation level, should be positive.
      User property: indentSize

    lineLength (Default: 80)
      The maximum length of a display line, should be positive.
      User property: lineLength
```
