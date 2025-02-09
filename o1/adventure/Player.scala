package o1.adventure

import scala.collection.mutable.Map

// Main player class that handles all player actions and inventory
class Player(startingArea: Area):
  // Keep track of player's current state
  private var currentLocation = startingArea
  private var quitCommandGiven = false
  private val inventoryMap = Map[String, Item]()  // Max 3 items allowed
  private var riddleAttempts = 2    // Player gets 2 chances to solve riddle
  private var hasPasscode = false   // Tracks if player has used the passcode
  private var flashlightActive = false  // Tracks if flashlight is currently on
  private var hasAnsweredRiddle = false // Prevents repeated riddle challenges
  
  // Basic getter methods
  def hasQuit = this.quitCommandGiven
  def location = this.currentLocation
  def hasItem(itemName: String) = inventoryMap.contains(itemName)
  def inventoryCount = inventoryMap.size
  def getRiddleAttempts = riddleAttempts
  
  // Handles movement between areas
  def go(direction: String) =
    val destination = this.location.neighbor(direction)
    destination match
      case Some(area) =>
        if area.name == "Mystery Room" && !area.isUnlocked then
          "The door is locked. You need to use the library card to enter."
        else if area.name == "Archives" && !area.isUnlocked then
          "The door is locked. You need to use the passcode here."
        else if area.name == "Grand Entrance" && !hasItem("master_key") then
          "The ghostly librarian blocks your path. 'You need the master key to exit.'"
        else
          flashlightActive = false  // Reset flashlight when moving
          area.setLit(false)
          this.currentLocation = area
          s"You go $direction."
      case None =>
        s"You can't go $direction."

  // Handles picking up items
  def get(itemName: String) =
    if inventoryCount >= 3 then
      "You can't carry more than three items. Drop something first."
    else if this.location.isDarkArea && !flashlightActive && itemName != "flashlight" then
      "It's too dark to see any items clearly."
    else if this.location.containsItem(itemName) then
      if itemName == "library_card" && !hasAnsweredRiddle && this.location.name == "Main Lobby" then
        """The ghost librarian appears before you and asks:
          |'Answer my riddle to receive the library card:
          |I have pages but no paper,
          |A spine but no bones,
          |Stories to share but cannot speak.
          |What am I?'
          |Type 'answer [your_answer]' to respond.""".stripMargin
      else
        this.location.removeItem(itemName) match
          case Some(item) =>
            inventoryMap(itemName) = item
            if itemName == "passcode" then hasPasscode = true
            s"You pick up the $itemName."
          case None =>
            s"There is no $itemName here to pick up."
    else 
      s"There is no $itemName here to pick up."
    
  // Handles dropping items from inventory
  def drop(itemName: String) =
    inventoryMap.remove(itemName) match
      case Some(item) =>
        this.location.addItem(item)
        if itemName == "passcode" then hasPasscode = false
        if itemName == "flashlight" then
          flashlightActive = false    // Turn off flashlight when dropped
          this.location.setLit(false)
        s"You drop the $itemName."
      case None =>
        "You don't have that!"

  // Reads notices and readable items
  def read(itemName: String) =
    if itemName == "notice" && this.location.name == "Reference Section" then
      "The notice reads: 'The passcode can be found in the book of knowledge.'"
    else if itemName == "ancient_tome" && hasItem(itemName) then
      "The tome reveals: 'The passcode lies in the north, but wise people always carry lights.'"
    else
      "There's nothing to read here."

  // Handles using items in specific locations
  def use(itemName: String) =
    inventoryMap.get(itemName) match
      case Some(item) =>
        (item.name, this.location.name) match
          case ("library_card", "Ancient Tome Room") =>
            val destination = this.location.neighbor("north").get
            destination.setUnlocked(true)
            "You use the library card. The door to the Mystery Room unlocks with a click."
          case ("flashlight", "Mystery Room") =>
            flashlightActive = true
            this.location.setLit(true)
            if this.location.containsItem("passcode") then
              "The flashlight illuminates the room, revealing a piece of paper with numbers - the passcode!"
            else
              "The flashlight illuminates the empty room."
          case ("flashlight", _) =>
            flashlightActive = false
            this.location.setLit(false)
            "You turn on the flashlight, but there's nothing special to see here."
          case ("passcode", "Reference Section") =>
            val destination = this.location.neighbor("north").get
            destination.setUnlocked(true)
            "You enter the passcode. The north door unlocks with a click."
          case _ =>
            s"You can't use the $itemName here."
      case None =>
        "You don't have that to use."

  // Handles the ghost librarian's riddle
  def answerRiddle(answer: String) =
    if answer.toLowerCase == "book" then
      val item = this.location.removeItem("library_card").get
      inventoryMap("library_card") = item
      hasAnsweredRiddle = true
      true
    else
      riddleAttempts -= 1
      false

  // Examines items in inventory
  def examine(itemName: String) =
    if this.location.isDarkArea && !flashlightActive then
      "It's too dark to examine anything clearly."
    else
      inventoryMap.get(itemName) match
        case Some(item) =>
          s"You look closely at the $itemName.\n${item.description}"
        case None =>
          "If you want to examine something, you need to pick it up first."

  // Shows current inventory status
  def inventory =
    if inventoryMap.isEmpty then
      "You are empty-handed."
    else
      s"You are carrying (${inventoryCount}/3):\n${inventoryMap.keys.mkString("\n")}"

  // Handles quitting the game
  def quit =
    this.quitCommandGiven = true
    ""
end Player