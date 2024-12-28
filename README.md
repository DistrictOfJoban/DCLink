# DCLink
A customizable linking mod between Discord and Minecraft, designed for use in The District of Joban.  
*Note: This project is now in maintenance mode*

[![works badge](https://cdn.jsdelivr.net/gh/nikku/works-on-my-machine@v0.2.0/badge.svg)](https://github.com/nikku/works-on-my-machine)
![build status](https://github.com/Kenny-Hui/DCLink/actions/workflows/build.yml/badge.svg)

## The origin
A server that I previously manages has been upgraded to 1.17.1.  
Unfortunately, I can't find a decent Discord Chat Linking Bot that suits my need and runs on Fabric 1.17.1. (Which by now is fairly outdated, but hey stubborn players exists)

[Chatter](https://github.com/axieum/chatter) is what I used to use before upgrading, but it's stuck on 1.16.5.  
While a newer rewritten mod by the same author, [Minecord](https://github.com/axieum/minecord) is only available for 1.19.

I don't have enough understanding of the codebase to port either of the mod to 1.17, so I have to make another Discord linking mod just for a server I manages.

Eventually I tried making it a more proper project, (hopefully) implement bridging to services outside of only Discord. (2023-12-15: And gave up)

## Features
- Config hot-reloadable
- Support bot activities
- Support custom guild emoji (Minecraft to Discord)
- Allow silencing relays from/to Minecraft
- Fully customizable embed based on Discord's specification

### Supported Server Events to be relayed to Discord
- Server starting / started / stopping / stopped
- Server crash

### Supported Player Events to be relayed to Discord
- Player join
- Player leave
- Player send chat messages (Including commands)
- Player entering another dimension
- Player death
- Player achieve Advancement

### Supported Discord events relayed to Minecraft
- Message Send/Reply
- Message Edit
- Message Delete
- Message Reaction Add/Remove

## Future Goals
- Bridge to services other than Discord, Revolt would be a good first step.
- Relay more Discord events
- Add bridged commands
- Touch some grass

## Downloads
**This project is not yet considered finish nor stable, expect alpha quality!**

For downloading the latest build, please [click here](https://github.com/Kenny-Hui/DCLink/actions).

You're welcome to open an issue if you believe you've found a bug, however note that the config file format may change overtime without any documentation, please only use it if you can understand my semi-spegetti code :)

## Cross Version Mapping
For easier development, all version-specific code is stored in `src/main/mappings`.

The files will then be copied over to `src/main/java/com/lx862/dclink`.

To change the Minecraft version for development, edit **default_mc_version** in `gradle.properties`.
(Or if you simply want to build, `./gradlew build -PbuildVersion=<Minecraft Version Here>`)

## License
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT)
