package o1.adventure

// Main game class that controls the whole game flow
class Adventure:
  val title = "The Haunted Library"

  // Create all the areas in the game with their descriptions
  private val referenceSection = Area("Reference Section", 
    """You wake up in the reference section. It's 2 AM and eerily quiet.
      |There's a notice posted on the north door that you could read.""".stripMargin)
      
  private val lobby = Area("Main Lobby", 
    "A spacious lobby with a reception desk. A ghostly librarian floats nearby.")
    
  private val ancientTomeRoom = Area("Ancient Tome Room", 
    "A room filled with ancient books and manuscripts.")
    
  private val mysteryRoom = Area("Mystery Room", 
    "A mysterious room shrouded in darkness.")
    
  private val archives = Area("Archives", 
    "A vast room filled with old documents. The master key gleams in a corner.")
    
  private val entrance = Area("Grand Entrance", 
    "The main entrance of the library. Heavy doors block your escape.")

  // Connect all areas together to make the map
  referenceSection.setNeighbors(Vector("north" -> archives, "east" -> lobby))
  lobby.setNeighbors(Vector("west" -> referenceSection, "east" -> ancientTomeRoom, "south" -> entrance))
  ancientTomeRoom.setNeighbors(Vector("west" -> lobby, "north" -> mysteryRoom))
  mysteryRoom.setNeighbors(Vector("south" -> ancientTomeRoom))
  archives.setNeighbors(Vector("south" -> referenceSection))

  // Put all the items in their starting locations
  referenceSection.addItem(Item("flashlight", "A working flashlight that might help in dark areas."))
  lobby.addItem(Item("library_card", "A staff access card that might grant access to restricted areas."))
  ancientTomeRoom.addItem(Item("ancient_tome", "An old book with mysterious writings."))
  mysteryRoom.addItem(Item("passcode", "A sequence of numbers written on old parchment."))
  archives.addItem(Item("master_key", "The key to the library's main entrance."))

  // Set up special properties for certain areas
  mysteryRoom.setDark(true)
  mysteryRoom.setRequiresCard(true)
  archives.setPasscodeProtected(true)

  // Create player and start counting turns
  val player = Player(referenceSection)
  var turnCount = 0
  val timeLimit = 30

  // Check if player has won
  def isComplete = this.player.location == entrance && this.player.hasItem("master_key")

  // Check if game should end
  def isOver = this.isComplete || this.player.hasQuit || 
               this.turnCount >= this.timeLimit || 
               this.player.getRiddleAttempts <= 0

  // Message shown when game starts
  def welcomeMessage = 
    """You wake up in the library's reference section at 2 AM.
      |The building feels different, supernatural even.
      |You must find the master key and escape through the main entrance.
      |Type 'help' for available commands.""".stripMargin

  // Different messages for different game endings
  def goodbyeMessage =
    if this.isComplete then
      "You've successfully escaped the haunted library!"
    else if this.turnCount >= this.timeLimit then
      "Time has run out. The library claims another soul..."
    else if this.player.getRiddleAttempts <= 0 then
      "The ghost librarian banishes you from the library forever..."
    else
      "You give up, resigning yourself to your fate in the haunted library."

  // Handle each turn of the game
  def playTurn(command: String) =
    val action = Action(command)
    val outcomeReport = action.execute(this.player)
    if outcomeReport.isDefined then
      this.turnCount += 1
    outcomeReport.getOrElse("Unknown command: " + command)
end Adventure