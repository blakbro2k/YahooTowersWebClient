# YahooTowersClient

## Welcome to my personal project to remake Yahoo! Towers from scratch

This project has been a **labor of love**—and I do mean LABOR. Yahoo! Towers was a fantastic twist
on Tetris and Columns back in the day.

When Yahoo! Games shut down, this gem disappeared with it. I'm trying to bring it back, but it's
been quite a journey.

This game has one of the hardest challenges in game design:

- Real-time server/client networking (no single-player here)
- Synchronizing 8 players
- Supporting powers that affect other players

---

## ⚡ What this repo is

This is the **client** for YahooTowers.

✅ Built in **LibGDX** (supports HTML, desktop, Android)  
✅ Written to be modular and maintainable  
✅ Communicates with the YahooTowers server over the network

---

## ✅ New Architecture Overview

### Core separation (Yipee project)

To keep things clean and reusable, I've split out **all game logic** into its own separate project:

> [Yipee](https://github.com/blakbro2k/Yipee) (the core library)

✅ The **client** depends on Yipee as a Maven/Gradle dependency.  
✅ All gameplay logic (board state, falling pieces, match detection, power handling) lives there.  
✅ Makes it easy to keep server and client *in sync*, since they both use the same core logic.  
✅ Anyone can port the core separately if they want.

This design **massively** simplified the networking and made the server/client code much easier to
write.

---

### The Client

✅ Renders the game using **LibGDX**  
✅ Uses **Autumn MVC** for UI templating and control flow  
✅ Handles user input and converts it to **PlayerAction** packets  
✅ Maintains local prediction of board state to reduce latency  
✅ Receives server authoritative updates and corrects state

**How it works:**

- Talks to the server over **WebSockets**
- Serializes game state updates and player actions using shared packet classes
- Displays two boards at once (player and partner)
- Animates blocks breaking, falling, powers, and Yahoo drops

---

## ✅ Current Status

✅ Core game logic is complete and lives in the [Yipee project](https://github.com/blakbro2k/Yipee)  
✅ Server skeleton is functional and runs games in headless mode  
✅ Client is rendering boards and syncing with server  
✅ Animations and UI are being refined

---

## ✅ What's Left to Do

**Client:**

- Finalize power animation sequences
- Finish ClientSide prediction
- Complete all UI overlays and feedback
- Gather all Sounds for YahooTowers!

**Networking:**

- Finalize and test Packets sent to the Server. e.g. Powers/UserInput
- Optimize for low latency with better prediction/rollback
- Harden against bad connections and desync

---

## 💻 Demo of the Client

[Yahoo! Towers Alpha](https://blakbro2k.itch.io/yahoo-towers-dev?debug=true)  
![image](https://github.com/blakbro2k/YahooTowersWebClient/assets/3727243/0fe49548-8e94-480f-adce-945559b2d72d)

### BlockBurner

I recreated Yahoo! Towers as **BlockBurner**, a free online game available at [playblockburner.com](https://playblockburner.com).

---

## ❤️ Want to Contribute?

Awesome! Here are a couple of Gradle commands you can use after cloning:
<a href="https://www.paypal.com/donate/?hosted_button_id=Q3B297GYMH6DQ"><img src="https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif" height="40"></a>  
If you enjoyed this project — or just feeling generous, consider buying me a beer. Cheers! :beers:

```bash
./gradlew html:dist (produces a war that you need  web server to run)
./gradlew desktop:run
./gradlew html:superDev (sets up a local webserver that you can use to test)


