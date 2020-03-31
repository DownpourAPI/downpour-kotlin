# Downpour

Downpour is a collection of libraries which can be used to control an instance of `deluged` using the API
opened up by the web-based `Deluge Web UI`. Experimental support is also included for `rtorrent`, accessible via
`rutorrent`. Please see the section on `rutorrent` support further down this README for more details.

Downpour is *not* intended to replace those user interfaces, which can do a lot of powerful things for managing the
underlying applications. It is instead intended to be a way to quickly and easily monitor and manage torrents in a
remote session. It will therefore generally not override any default options set on the remote application. Nor does it 
give every piece of information it is possible to know about a torrent. "Piece length"? I don't need to know in my 
"quick and simple" library. "Percent done"? Now that's useful information.


## What's included
- Add Torrents by: Magnet URI / Torrent File
- Remove Torrents from session -- With or without deleting data
- Get information on all Torrents in a session
- Get information on a specific Torrent by Info Hash
- Pause / Resume Torrents by Info Hash
- Force a Recheck of Torrent data by Info Hash
- Set a Max Ratio for a Torrent by Info Hash
- Get the Free Space available on the remote filesystem


## Release strategy
Downpour is currently available in the following flavours:
- Kotlin/JVM (compatible with Android)

With the following in development:
- C# (.NET Standard 2.0)
- Go
- Python (3.6+)
- TypeScript (Node.js & Web)

And some more planned for some point in the future:
- C
- C++
- Ruby
- Rust

Releases will be numbered using semver: `vMAJOR.MINOR.BUGFIX`
    
    MAJOR = Backwards incompatible API changes
    MINOR = New Features
    BUGFIX = Fixin' what's broke and cleaning up code

`MAJOR` and `MINOR` numbers are synchronised across all flavours. Any two flavours with the same Major and Minor values
will have the same feature set. `BUGFIX` numbers are *not* synchronised.

    e.g. C# v1.2.1 and Go v1.2.6 have the same set of features, and therefore the same API, but I found more bugs to
    squash in the Go version, so it received more bugfixes
    
The **numbers** are synchronised, but the **releases** may not be. I am not a machine, so some languages may get new
features faster than others.


## rutorrent support
There is a module in this library for controlling boxes which run an `rtorrent` backend managed with the `rutorrent` web
frontend. However, the `rtorrent` XML-RPC API is much less-well documented than *Deluge*'s JSON-RPC API, and even then,
the exact specifications can vary wildly from setup to setup. For instance, `rutorrent` comes with no authentication out
of the box. **Most** implementations I have seen use HTTP Basic Authentication, but this is not a requirement.

The API for `rtorrent` also has some interesting omissions.
For example: It is not possible to discover the amount of free space which is available in the download folder without 
providing the Info Hash of a torrent which is not marked as "Finished". So if all of your torrents are "Finished", or
you have an empty torrent box, there is no way I can find to get the amount of Free Space available to you.

In my experience testing this library,`rtorrent` seems also to be quite fragile. A malformed request is sometimes enough
to kill the entire service. Which is not ideal, to put it lightly.

`rtorrent` is a useful and common piece of software, so I have included support despite these difficulties. However,
that support comes with a huge caveat:

I make **absolutely zero** guarantees that it will work *at all*.

If things don't work, absolutely feel free to add them to the issue tracker, but don't be surprised if I'm liberal with
the "WONTFIX" label.


## FAQ

*Or at least, what I imagine might be FA'd Qs*

1. Why write this in so many languages? Why not write it in C and let other people figure out how to do interop into
their favourite language?

- Good question! I want this to be easy for anyone to use. If I'm a kid writing my first Python script because I want to
monitor the status of my torrent box from the screen on the Raspberry Pi kit I got for Christmas, I'm going to be
immediately turned off when I learn that the library that does exactly what I need is written in C, and I need to
"figure it out, kid". My hobby is ruined, all joy in my life cast into the fires of Mt Doom, and I crawl into my bed,
sad, defeated, broken, with no eagles to save me or my gardener...

2. So why not do the interop yourself?

- ~~Because that sounds hard~~ Because I thought it would be more fun to build these things from the ground up in the
language they were going to be used in, to learn a little bit about the ecosystem and the tooling each language has to
offer. Plus libraries with natively-compiled elements have caused so many problems for me in the past, I want to avoid
giving other people that same experience.

3. Isn't torrenting illegal? Aren't you supporting illegal activity?

- BitTorrent is a protocol for sharing data. There is nothing inherently illegal about the transfer of data. For example
you can download the entirety of Wikipedia using BitTorrent: https://meta.wikimedia.org/wiki/Data_dump_torrents. You can
do illegal things with email too, but I don't condone that, nor do I condone using BitTorrent to do illegal things, and 
by extension I absolutely do not condone the use of Downpour for any illegal activity.
