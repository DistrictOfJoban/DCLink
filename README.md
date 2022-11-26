# DCLink
[![works badge](https://cdn.jsdelivr.net/gh/nikku/works-on-my-machine@v0.2.0/badge.svg)](https://github.com/nikku/works-on-my-machine)
![build status](https://github.com/Kenny-Hui/DCLink/actions/workflows/build.yml/badge.svg)
## The story
A server that I previously manages has been upgraded to 1.17.1.  
Unfortunately, I can't find a decent Discord Chat Linking Bot that suits my need and runs on Fabric 1.17.1.

[Chatter](https://github.com/axieum/chatter) is what I used to use before upgrading, but it's stuck on 1.16.5.  
While a newer rewritten mod by the same author, [Minecord](https://github.com/axieum/minecord) is only available for 1.19.

I don't have enough understanding of the code to port either of the mod to 1.17, so I made yet another Discord linking mod with a codebase 10x worse than others.

What a brilliant idea!

## Features
- Fabric 1.16.5 - 1.19.2
- Config hot-reloadable
- Support bot activities
- Support custom guild emoji (Minecraft to Discord)
- Allow silencing from Minecraft to Discord, or the other way round
- Fully customizable Discord embed

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
- Relay more Discord events
- Add Discord Commands
- Touch some grass

## Downloads
**This is not production ready yet, only use if you know what you are doing!**

For downloading the latest build, please [click here](https://github.com/Kenny-Hui/DCLink/actions).

However no support or mod usage will be provided at the moment. There's also no guarantee on compatibilities, given that this is still Work In Progress

## Cross Version Mapping
For easier development, all version-specific code is stored in `src/main/mappings`.

The files will then be copied over to `src/main/java/com/lx/dclink`.

To change the Minecraft version for development, edit **default_mc_version** in `gradle.properties`.
(Or if you simply want to build, `./gradlew build -PbuildVersion=<Minecraft Version Here>`)

## License
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT)