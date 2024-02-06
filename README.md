# YahooTowersClient
![image](https://github.com/blakbro2k/YahooTowersWebClient/assets/3727243/0fe49548-8e94-480f-adce-945559b2d72d)

## Welcome to my personal project to remake Yahoo! Towers from scratch
This project has been a labor of love, and I do mean LABOR.  Yahoo towers was a wonderful love letter to tetris and columns back in the day.
Once Yahoo! games shutdown, this gem went with it.  I am trying to correct that, but it has been challenging.

This game combines the hardest concept of a game.
Server/Client networking, no single play here.
8 players to sync.
powers that can affect others.

Here is what I have so far:

I have the core game code pretty much complete.  I decided to separate it out incase others might want to port it one day, and it also made the Server/Client code MUCH easier.
The core game code can do the following:
Sets up a board for a player AND their partner
Game loop handles moving the tri-piece down
checks the grid for broken blocks
handles yahoo!
handles all powers

Next I am working on the web client:
So far I have most of the main components down of the client.
what is left is:
the blocks on a grid have to break when they fall down.
Test the animation of all powers
finish the Powers Queue component
Build game client for 8 players
Skin the client

Lastly...  the server
This was the most challenging part.
I have the skeleton, but it needs more.
the persistance layer is not complete, this is how games will be stored and tracked on the database side
The website to launch a game needs to be completed
Converting websocket communication to KryoNet.  (From research that seems to be the best approach for a smooth network game)
The Server is using Spring Boot, which I have never used before an therefore another challenge.


A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff).

Project template included launchers with [Autumn](https://github.com/crashinvaders/gdx-lml/tree/master/autumn) class scanners and a basic [Autumn MVC](https://github.com/czyzby/gdx-lml/tree/master/mvc) application.

Here is a demo of the client
Yahoo! Towers alpha (https://blakbro2k.itch.io/yahoo-towers-dev)

### Wish to contribute? Great to hear! below are acouple of commands you can use when you clone the project.
## Gradle

This project uses [Gradle](http://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `html:dist`: compiles GWT sources. The compiled application can be found at `html/build/dist`: you can use any HTTP server to deploy it.
- `html:superDev`: compiles GWT sources and runs the application in SuperDev mode. It will be available at [localhost:8080/html](http://localhost:8080/html). Use only during development.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.


<a href="https://www.paypal.com/donate/?hosted_button_id=Q3B297GYMH6DQ"><img src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif" height="40"></a>  
If you enjoyed this project — or just feeling generous, consider buying me a beer. Cheers! :beers:
