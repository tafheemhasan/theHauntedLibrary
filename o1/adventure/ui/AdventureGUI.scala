package o1.adventure.ui

import scala.swing.*
import scala.swing.event.*
import javax.swing.UIManager
import o1.adventure.Adventure
import java.awt.{Point, Insets, Dimension}

// Graphical user interface for the game
object AdventureGUI extends SimpleSwingApplication:
  // Use system's look and feel
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  
  def top = new MainFrame:
    // Create game instance
    private val game = Adventure()
    private val player = game.player
    
    title = game.title
    
    // Text area to show current location info
    val locationInfo = new TextArea(7, 80):
      editable = false
      wordWrap = true
      lineWrap = true
      
    // Text area to show game events
    val turnOutput = new TextArea(7, 80):
      editable = false
      wordWrap = true
      lineWrap = true
      
    // Input field for player commands
    val input = new TextField(40):
      minimumSize = preferredSize
      
    // Listen for keyboard input
    this.listenTo(input.keys)
    val turnCounter = new Label
    
    // Handle Enter key press
    this.reactions += {
      case keyEvent: KeyPressed if keyEvent.source == this.input && keyEvent.key == Key.Enter && !this.game.isOver =>
        val command = this.input.text.trim
        if command.nonEmpty then
          this.input.text = ""
          this.playTurn(command)
    }
    
    // Layout the GUI components
    this.contents = new GridBagPanel:
      import scala.swing.GridBagPanel.Anchor.*
      import scala.swing.GridBagPanel.Fill
      layout(new Label("Location:")) = Constraints(0, 0, 1, 1, 0, 1, NorthWest.id, Fill.None.id, new Insets(8, 5, 5, 5), 0, 0)
      layout(new Label("Command:")) = Constraints(0, 1, 1, 1, 0, 0, NorthWest.id, Fill.None.id, new Insets(8, 5, 5, 5), 0, 0)
      layout(new Label("Events:")) = Constraints(0, 2, 1, 1, 0, 0, NorthWest.id, Fill.None.id, new Insets(8, 5, 5, 5), 0, 0)
      layout(turnCounter) = Constraints(0, 3, 2, 1, 0, 0, NorthWest.id, Fill.None.id, new Insets(8, 5, 5, 5), 0, 0)
      layout(locationInfo) = Constraints(1, 0, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, new Insets(5, 5, 5, 5), 0, 0)
      layout(turnOutput) = Constraints(1, 2, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, new Insets(5, 5, 5, 5), 0, 0)
      layout(input) = Constraints(1, 1, 1, 1, 1, 0, NorthWest.id, Fill.Horizontal.id, new Insets(5, 5, 5, 5), 0, 0)
    
    // Add quit option to menu
    menuBar = new MenuBar:
      contents += new Menu("Program"):
        contents += new MenuItem(Action("Quit") { dispose() })
    
    // Set up window properties
    this.location = Point(50, 50)
    this.minimumSize = Dimension(200, 200)
    this.pack()
    this.input.requestFocusInWindow()
    
    // Handle each turn of the game
    def playTurn(command: String) =
      val turnReport = this.game.playTurn(command)
      if this.player.hasQuit then
        this.dispose()
      else
        this.updateInfo(turnReport)
    
    // Update the display with new game information
    def updateInfo(info: String) =
      if !this.game.isOver then
        this.turnOutput.text = info
      else
        this.turnOutput.text = info + "\n" + this.game.goodbyeMessage
      this.locationInfo.text = this.player.location.fullDescription
      this.turnCounter.text = s"Turns played: ${this.game.turnCount}"
    
    // Show welcome message when game starts
    this.updateInfo(this.game.welcomeMessage)
    
    // Required for Scala 3 type checking
    private given CanEqual[Component, Component] = CanEqual.derived
    private given CanEqual[Key.Value, Key.Value] = CanEqual.derived
end AdventureGUI