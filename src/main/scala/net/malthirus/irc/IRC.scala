package net.malthirus.irc

import java.io.{BufferedReader, InputStreamReader, BufferedWriter, OutputStreamWriter}
import java.net.Socket

class IRC(server: String, port: Int=6667, login: String, pass: String=null) {
    protected val socket = new Socket(server, 6667)
    protected val writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    protected val reader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    
    // Log in to the server
    private var credentials = Seq("NICK "+login)
    if(pass!=null) credentials = ("PASS " + pass) +: credentials
    write(credentials:_*)
    
    // Read lines from the server until it tells us we have connected.
    private var success = false
    private var line: String = null
    while (line != null && !success) {
      if (line.indexOf("004") >= 0) // We are now logged in.
        success = true
      println(line)
      line = read
    }
    
    def read = reader.readLine
    def write(messages: String*) {
      messages.foreach(m=>writer.write(m+"\r\n"))
      writer.flush
    }
    def join(channel: String) {
      write("JOIN "+channel)
    }
    def part(channel: String) {
      write("PART "+channel)
    }
    def privmsg(channel: String, msg: String) {
      write("PRIVMSG " + channel + " :" + msg)
    }
    
    def close = {
      reader.close
      writer.close
      socket.close
    }
}