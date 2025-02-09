package o1.adventure

// This class handles all player commands and executes them
class Action(input: String):
  // Clean up the input and split it into command and details
  private val commandText = input.trim.toLowerCase
  private val verb = commandText.takeWhile(!_.isWhitespace)    // The command (go, get, etc.)
  private val modifiers = commandText.dropWhile(!_.isWhitespace).trim  // The details (north, flashlight, etc.)

  // Process the command and return what happened
  def execute(actor: Player): Option[String] =
    this.verb match
      // Basic movement command
      case "go" => Some(actor.go(this.modifiers))
      // Exit game command
      case "quit" => Some(actor.quit)
      // Pick up items
      case "get" => Some(actor.get(this.modifiers))
      // Drop items from inventory
      case "drop" => Some(actor.drop(this.modifiers))
      // Look at items closely
      case "examine" => Some(actor.examine(this.modifiers))
      // Check what you're carrying
      case "inventory" => Some(actor.inventory)
      // Read notices and books
      case "read" => Some(actor.read(this.modifiers))
      // Use items in specific places
      case "use" => Some(actor.use(this.modifiers))
      // Handle the ghost librarian's riddle
      case "answer" =>
        if actor.location.name == "Main Lobby" && actor.location.containsItem("library_card") then
          if actor.answerRiddle(this.modifiers) then
            Some("'Correct!' the librarian smiles. The library card is now in your inventory.")
          else if actor.getRiddleAttempts > 0 then
            Some(s"'Wrong!' the librarian frowns. 'You have ${actor.getRiddleAttempts} attempts remaining.'")
          else
            Some("'Wrong!' the librarian vanishes with the library card. Game Over!")
        else
          Some("There is no riddle to answer here.")
      // Show available commands
      case "help" => Some("""Available commands:
                          |go [direction] - Move to another area
                          |get [item] - Pick up an item
                          |drop [item] - Drop an item
                          |examine [item] - Look at an item closely
                          |read [item] - Read readable items
                          |use [item] - Use an item in specific locations
                          |answer [word] - Answer the librarian's riddle
                          |inventory - Check your items (max 3)
                          |help - Show this help
                          |quit - End the game""".stripMargin)
      // Handle unknown commands
      case _ => None

  // For debugging purposes
  override def toString = s"$verb (modifiers: $modifiers)"
end Action