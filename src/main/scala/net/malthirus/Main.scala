package net.malthirus

import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]) {
    // Retrieving our configuration
    val conf = ConfigFactory.load
    val admin = conf.getString("twitch.admin")
    val login = conf.getString("twitch.login")
    val pass = conf.getString("twitch.oauth")
    val channels = conf.getString("twitch.users")
        .split(",").map("#"+_.toLowerCase)
    
    val myBot = new MyBot(login,pass)
    import myBot._ // Rather than type myBot. the whole time, we'll just import its methods
    channels.foreach(join(_))
    
    // If the admin says !halt in their channel or the bot's channel, end the program
    val killPhrases = Seq(admin,login).map(user =>
      String.format(":%2$s!%2$s@%2$s.tmi.twitch.tv PRIVMSG #%1$s :!halt",user,admin)) 
    
    // Start iterating through every line
    var line = read
    while (line != null && killPhrases.forall(!line.endsWith(_))) {
      if(line.equals("PING :tmi.twitch.tv"))
        write("PONG :tmi.twitch.tv")
      else process(line)
      line = read
    }
    
    // Clean up the bot
    channels.foreach(part(_))
    close
  }
}