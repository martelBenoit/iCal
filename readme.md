# iCal - A calendar discord bot
A Java bot for [Discord](https://discordapp.com/) using the [JDA library](https://github.com/DV8FromTheWorld/JDA).

It uses sql (postgresql) to store data.

## What can it do ?

After having informed a valid calendar link I am able to warn you 15 minutes before the next event.

I also warn you if an event from your calendar has been added, deleted or moved. The calendar is refreshed every 5 minutes by default.

Of course you can choose which calendar you wish to follow (maximum one per guild).

The bot offers a batch of commands allowing to display more information and to configure myself.

## Commands

Commands are prefixed with a "!" by default, this can be configured.
For a list of commands in discord the **help** command can be used.
For more information about a command use **help \<commandname\>**

## Global configuration

The global configuration is stored in the .env file. A sample of this file is available : **.env.example**
