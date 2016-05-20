package net.malthirus.irc

class Twitch(login: String, pass: String) extends IRC("irc.twitch.tv", 6667, login, pass) {
    def requestMembership = write("CAP REQ :twitch.tv/membership") 
    def requestCommands = write("CAP REQ :twitch.tv/commands") 
    def requestTags = write("CAP REQ :twitch.tv/tags") 
}

trait TwitchParser {
  self:Twitch =>
  requestTags  
  
  // Override these methods to handle each type of IRC line
  def onMessage(sender: String, channel: String, msg: String, mod: Boolean) {}
  def onMod(recipient: String, channel: String) {}
  def onDeMod(recipient: String, channel: String) {}
  def onJoin(user: String, channel: String) {}
  def onPart(user: String, channel: String) {}
  def onUnknown(message: String) {}
  
  def process(line: String) {
    val msg = line.split(" ",4)
    msg match {
    
    // Private Message
    case Array(_,_,"PRIVMSG",_) =>
      val i = msg(0).indexOf(";mod=")+5
      val isMod = msg(0).substring(i,i+1).toInt==1
      onMessage(msg(1).substring(1,msg(1).indexOf("!")),msg(3).substring(0, msg(3).indexOf(":")-1),msg(3).substring(msg(3).indexOf(":")+1),isMod)
    
    // Mod or DeMod
    case Array(":jtv","MODE",_) => {
      // Someone is a mod, or got demoded
      val details = msg(2).split(" ")
      if(details(1).equals("+o"))
        onMod(details(2),details(0))
      else 
        onDeMod(details(2),details(0))
    }
    
    // Someone has arrived
    case Array(_,"JOIN",_) =>
      onJoin(msg(0).substring(1,msg(0).indexOf("!")),msg(2))
    
    // Someone has parted
    case Array(_,"PART",_) => 
      onPart(msg(0).substring(1,msg(0).indexOf("!")),msg(2))
    
    // lol I dunno
    case _ =>
      onUnknown(line)
    }
  }
}