package o1.adventure

import scala.collection.mutable.Map

// Represents a location in the game that players can visit
// Each area can have items and connections to other areas
class Area(var name: String, var description: String):
  // Keep track of connected areas and their directions
  private val neighbors = Map[String, Area]()
  // Store items that can be found in this area
  private val items = Map[String, Item]()
  
  // Properties to handle special area mechanics
  private var isDark = false          // Is this a dark area?
  private var isLit = false           // Is the area currently lit?
  private var passcodeProtected = false    // Does this area need a passcode?
  private var requiresCard = false         // Does this area need library card?
  private var hasBeenUnlocked = false      // Has this area been unlocked?
  private var originalDescription = description
  
  // Basic getter for finding connected areas
  def neighbor(direction: String) = this.neighbors.get(direction)
  
  // Methods to set up area connections
  def setNeighbor(direction: String, neighbor: Area) =
    this.neighbors(direction) = neighbor
    
  def setNeighbors(exits: Vector[(String, Area)]) =
    this.neighbors ++= exits
  
  // Methods for handling items in the area
  def addItem(item: Item) =
    items(item.name) = item
    
  def containsItem(itemName: String) = items.contains(itemName)
  
  // Remove item and update description if needed
  def removeItem(itemName: String) =
    val item = items.remove(itemName)
    if item.isDefined && itemName == "passcode" then
      this.description = this.description.replace(" revealing a piece of paper with numbers - the passcode!", "")
    item
  
  // Get appropriate description based on lighting
  def getDescription =
    if isDark && !isLit then
      "It's pitch black. You can't see anything."
    else
      this.description
      
  // Methods to handle area properties
  def setLit(lit: Boolean) = this.isLit = lit
  def isDarkArea = this.isDark
  def setDark(dark: Boolean) = this.isDark = dark
  def requiresLibraryCard = this.requiresCard
  def setRequiresCard(requires: Boolean) = this.requiresCard = requires
  def setUnlocked(unlocked: Boolean) = this.hasBeenUnlocked = unlocked
  def isUnlocked = this.hasBeenUnlocked
  def setPasscodeProtected(isProtected: Boolean) = this.passcodeProtected = isProtected
  def isPasscodeProtected = this.passcodeProtected
  
  // Special handling for visible items - flashlight can always be seen
  def getVisibleItems =
    if isDark && !isLit then
      if items.contains("flashlight") then
        Some("flashlight")
      else None
    else
      Some(items.keys.mkString(", "))
  
  // Combines all information for area description
  def fullDescription =
    val itemList = getVisibleItems.map(items => s"\nYou see here: $items").getOrElse("")
    val exitList = s"\n\nExits available: ${this.neighbors.keys.mkString(", ")}"
    s"${getDescription}$itemList$exitList"
    
  override def toString = this.name
end Area
