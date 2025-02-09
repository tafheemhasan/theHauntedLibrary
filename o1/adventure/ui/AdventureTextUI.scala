package o1.adventure.ui

import o1.adventure.*
import scala.io.StdIn.*

// Text-based user interface for the game
object AdventureTextUI extends App:
  // Create a new game instance
  private val game = Adventure()
  private val player = game.player
  
  // Show welcome message when game starts
  println(this.game.welcomeMessage)
  
  // Main game loop - keeps running until game is over
  while !this.game.isOver do
    this.printAreaInfo()
    this.playTurn()
    
  // Show final message when game ends
  println(this.game.goodbyeMessage)
  
  // Shows information about current location
  private def printAreaInfo() =
    val area = this.player.location
    println("\n" + area.name)
    println("-" * area.name.length)  // Makes a line under the area name
    println(area.fullDescription)
    
  // Handles each turn of the game
  private def playTurn() =
    println()
    val command = readLine("Command: ")  // Get player's input
    val turnReport = this.game.playTurn(command)
    if turnReport.nonEmpty then
      println(turnReport)
end AdventureTextUI