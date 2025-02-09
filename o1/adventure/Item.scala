package o1.adventure

// This class represents items that can be found and used in the game
// Each item has a name and description that players can see
class Item(val name: String, val description: String):
  // Makes the item show up as its name when printed
  override def toString = this.name
end Item