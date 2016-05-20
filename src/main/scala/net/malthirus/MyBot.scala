package net.malthirus

import net.malthirus.irc.{Twitch,TwitchParser}

// Example Bot
class MyBot(login: String, pass: String) extends Twitch(login, pass) with TwitchParser {
  // Quick Command Database Mockup
  private var commands = Map[(String,String),String]()
  def insertCommand(channel: String, command: String, quote: String) = commands += ((channel,command)->quote)
  def delCommand(channel: String, command:String) = commands -= ((channel,command))
  def commandExists(channel: String, command: String) = commands.contains((channel,command))
  def getCommand(channel: String, command: String) = commands((channel,command))
  
  // Beginning of our overrides and message handling from TwitchParser
  def onCommand(sender: String, channel: String, msg: String,mod: Boolean) = {
    val args = msg.split(" ",3)
    args(0) = args(0).toLowerCase
    
    // Add commands here
    args(0) match {
      case "!addcom" => 
        args(1) = args(1).toLowerCase
        if((channel.equals("#"+sender) || mod) && // If you're allowed
            args.length==3 && !commandExists(channel,args(1))) { // and the command is valid
        insertCommand(channel,args(1),args(2))
        privmsg(channel,"Command created: "+args(1)+"= "+args(2)) 
      }
      case "!delcom" =>
        args(1) = args(1).toLowerCase
        if((channel.equals("#"+sender) || mod) && // If you're allowed
            args.length==2 && commandExists(channel,args(1))) {
        delCommand(channel,args(1))
        privmsg(channel,"Command deleted: "+args(1)) 
      }
      case c: String => // If all else fails...
        if(commandExists(channel,args(0)))
        privmsg(channel,getCommand(channel,args(0)))
    }
  }
  override def onMessage(sender: String, channel: String, msg: String, mod: Boolean) = {
    printf("[%s] %s: %s\n", channel, sender, msg)
    val command = msg.trim
    if((msg.startsWith("!") || msg.trim.split(" ").length==1) &&
        !sender.equals("oneiroibot")) {
      onCommand(sender,channel,msg,mod)
    }
  }
  override def onJoin(user: String, channel: String) = 
    if(!user.equals("oneiroibot")) printf("[%s] %s JOIN\n", channel, user)
  override def onPart(user: String, channel: String) = 
    if(!user.equals("oneiroibot")) printf("[%s] %s PART\n", channel, user)
}