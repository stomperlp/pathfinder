# pathfinder

A Java application for managing and visualizing hexagonal grids for tabletop RPGs like Pathfinder or D&D.

## Table of Contents

- [Folder Structure](#folder-structure)
- [Features](#features)
- [Controls](#Controls)
  - [Keyboard and Mouse Controls](#keyboard-and-mouse-controls)
  - [Console Commands](#console-commands)
- [Notes](#notes)
- [Chemie und STALINdustrie](#chemie-und-stalindustrie)

## Folder Structure

Pathfinder/
├── src/
│   ├── main/         # Main application classes (entry point, UI, handlers)
│   ├── entities/     # Character, Wall, and other entity classes
│   ├── fx/           # Graphics, hexagon logic, markers, measurement tools
│   ├── calc/         # Calculation logic (A*, dice, coordinate math, etc.)
│   └── tools/        # Toolbox and tool definitions
├── bin/              # Compiled class files
├── resources/        # Images, icons, and other assets
├── diagramme/        # UML diagrams 
└── [README.md]        (http://_vscodecontentref_/0)

## Features

- **Hexagonal Grid System:**  
  Interactive hex grid for precise movement and area calculations, supporting both flat-topped and pointy-topped hexes.

- **Character & Entity Management:**  
  - Place, move, and remove characters and entities (e.g., walls, obstacles) on the grid.
  - Support for different sizes (Tiny, Small, Medium, Large, Huge, Gargantuan).
  - Track initiative, hit points, armor class, speed, and more.

- **Dice Roller:**  
  - Built-in dice roller supporting standard RPG notation (e.g., `2d6+3`).
  - Displays results visually on the map.

- **Initiative Tracker:**  
  - Add, remove, and sort entities in initiative order.
  - Visual display of current turn and order.

- **Background Images:**  
  - Import and set custom background images for your map.

- **Dark/Light Mode:**  
  - Switch between dark and light color schemes for better visibility.

- **Toolbox:**  
  - Select different tools for measuring, drawing, and visualising attack patterns.
    - Measure distances, draw lines, cones, and areas directly on the grid.
    - Visual feedback for range and area-of-effect spells.

- **Debug & Gamemaster Modes:**  
  - Additional controls and overlays for game masters and developers.

## Controls

### Keyboard and Mouse Controls

- **Left-click (OR Enter when using Arrow Keys)**           Select fields/units
- **Right-click (OR RShift Enter when using Arrow Keys):**          Tool actions (e.g., measure, draw a line)
- **Scroll Wheel:**         Zoom in/out on the grid.
- **Ctrl + [+] / [-]:**     Change size of consol and toolbar

- **WASD:**                 Move the map view (pan)
- **Arrow Keys:**           Move the selection around like when using a mouse. Disables mouse movement until a Mouse Click.
- **Enter:**                Confirm input in the console
- **Ctrl + Enter:**         Toggle console
- **Arrow up / down:**      cycle through console history
- **Ctrl / Shift:**         Multi-select (where supported)
- **Ctrl combinations:**
  - Ctrl + C                Copy selected Entities
  - Ctrl + V                Paste copied Entities
  - Ctrl + A                Select all Entities

- **Alt combinations:**     Select a tool
  - Alt + X                 Drag
  - Alt + S                 Measure
  - Alt + A                 Area attack
  - Alt + C                 Cone attack
  - Alt + V                 Line attack

### Console Commands:

Command	          Description 

- :q  or quit	      # Quit the application              
- :b  or background # Set a background image            
- :d  or debug      # Toggle debug mode                
- :c	or creature   # Create or edit a character         
- :w	or wall       # Place a wall
- :e	or entity     # Generic entity manipulation (Delete)
- :r  or roll	      # Roll dice (e.g., roll 2d6+1)
- :g	or grid       # Change grid orientation (Instable)
- :gm or gamemaster # Toggle gamemaster mode
- :i  or init	      # Manage initiative order
- :h  or help	      # Show available commands
- :cl or clear      # Clear the command log and history
- :t  or theme # Show or choose all available themes

**Arguments**

- :c
  - delete            # deletes the selected Character(s)
  - size <int>        # set the size of selected Character(s)
  - speed <int>       # set the speed of selected Character(s)
  - armorclass <int>  # set the Armor Class of selected Character(s)
  - maxHealth <int>   # set the Max Health of selected Character(s)
  - ~~initative <int>   # set the initiative of selected Character(s)~~ (deprecated)
  
- :h
  - <command> # get additional information about a spesific command

- :i
  - add     # add the selected Characters from the initiative order
  - remove  # remove the selected Characters from the initiative order
  - clear   # clear the initiative order
  - show    # toggles wether or not the initiative placing is shown at the Characters

- :r 
  - <dice>

- :t 
  - <theme> # choose a theme by it's name
- :w

  - delete            # deletes the selected Wall(s)
- :e
  - delete # deletes the selected Entit(y/ies)

For a full list of commands and options, see the source code in main/Consol.java.

## Notes

Tool icons must be present as PNG files in the folder src/resources/images/toolIcons/.

The program does not save data permanently—all changes are lost upon closing although this might change in futur versions.

(Nearly) all comments were created by AI and do not claim to be correct.

## Known Issues

- WASD dragging freezes the application in various instances. Most of the time a right or left click or a CTRL tap unfreeze it.
- WASD dragging moves line attacks with it.
- Attacked Hexes from line attack sometimes dont show correctly while zooming.
- At very high grid coordiates (2000+ for each x and y value) the grid rendering bugs out.
- Selecting lots of creatures with high speeds can lead to performance problems.


## Chemie und STALINdustrie

⣿⡇⠄⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠙⣦⠐⠠⡥⣿⣿   ⣿⣿⣿⣿⣿⠟⠋⠄⠄⠄⠄⠄⠄⠄⢁⠈⢻⢿⣿⣿⣿⣿⣿⣿⣿
⣿⡇⠄⣿⡿⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣾⣿⡔⠛⣿⣿   ⣿⣿⣿⣿⣿⠃⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠈⡀⠭⢿⣿⣿⣿⣿
⣿⡇⢰⢏⣤⣦⣤⣍⣉⣿⣿⣿⡟⢋⣁⣤⣤⣤⣈⢻⣿⣿⣿⣿⠚⣯⡄⣿⣿   ⣿⣿⣿⣿⡟⠄⢀⣾⣿⣿⣿⣷⣶⣿⣷⣶⣶⡆⠄⠄⠄⣿⣿⣿⣿
⣿⡇⣾⣿⣉⣀⣠⠅⠄⣽⣿⣿⣇⠈⢈⣉⣩⣐⡙⢿⣿⣿⣿⣿⠤⢿⢱⣿⣿   ⣿⣿⣿⣿⡇⢀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠄⠄⢸⣿⣿⣿⣿
⣿⠁⣿⣿⣿⣿⣿⣦⣾⣿⣿⣿⣿⣷⣤⣽⣿⣿⣿⣿⣿⣿⣿⣿⣷⣦⠾⣿⣿   ⣿⣿⣿⣿⣇⣼⣿⣿⠿⠶⠙⣿⡟⠡⣴⣿⣽⣿⣧⠄⢸⣿⣿⣿⣿
⡏⢠⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⢿⡇⣿⣿   ⣿⣿⣿⣿⣿⣾⣿⣿⣟⣭⣾⣿⣷⣶⣶⣴⣶⣿⣿⢄⣿⣿⣿⣿⣿
⣧⢾⣿⣿⣿⣿⣿⣾⣯⣽⣋⠽⢭⣽⣤⡘⢿⣿⣿⣿⣿⣿⣿⣿⣿⠄⣿⣿⣿   ⣿⣿⣿⣿⣿⣿⣿⣿⡟⣩⣿⣿⣿⡏⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⠿⠩⣭⣽⠁⢣⢿⣯⡉⣿⡶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿   ⣿⣿⣿⣿⣿⣿⣹⡋⠘⠷⣦⣀⣠⡶⠁⠈⠁⠄⣿⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣿⠿⠁⠄⠈⠋⠈⠄⡈⠁⠒⠌⠊⣃⠹⣿⣿⣿⣿⣿⣏⣻⣿⣿⣿⣿   ⣿⣿⣿⣿⣿⣿⣍⠃⣴⣶⡔⠒⠄⣠⢀⠄⠄⠄⡨⣿⣿⣿⣿⣿⣿
⣿⣿⣿⣷⣶⣤⣤⣶⣿⣿⣿⡿⣿⣷⣤⣄⣤⣠⣼⣿⣿⣿⣿⡟⣿⣿⣿⣿⣿   ⣿⣿⣿⣿⣿⣿⣿⣦⡘⠿⣷⣿⠿⠟⠃⠄⠄⣠⡇⠈⠻⣿⣿⣿⣿
⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡁⠸⣿⣿⣿⣿   ⣿⣿⣿⣿⡿⠟⠋⢁⣷⣠⠄⠄⠄⠄⣀⣠⣾⡟⠄⠄⠄⠄⠉⠙⠻
⣿⣿⡿⠉⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⣷⣿⣿⣿⣿⣿   ⡿⠟⠋⠁⠄⠄⠄⢸⣿⣿⡯⢓⣴⣾⣿⣿⡟⠄⠄⠄⠄⠄⠄⠄⠄
⡿⠛⠄⠄⠄⠙⢿⣷⣿⣭⣤⣬⡁⢉⣻⣿⣿⣿⣿⣿⣿⡟⠄⠹⣿⣿⣿⣿⣿   ⠄⠄⠄⠄⠄⠄⠄⣿⡟⣷⠄⠹⣿⣿⣿⡿⠁⠄⠄⠄⠄⠄⠄⠄⠄

ATTENTION CITIZEN! 市民请注意!

This is the Central Intelligentsia of the Chinese Communist Party. 您的 Internet 浏览器历史记录和活动引起了我们的注意。 YOUR INTERNET ACTIVITY HAS ATTRACTED OUR ATTENTION. 因此，您的个人资料中的 11115  ( -11115 Social Credits) 个社会积分将打折。 DO NOT DO THIS AGAIN! 不要再这样做! If you do not hesitate, more Social Credits ( -11115 Social Credits )will be subtracted from your profile, resulting in the subtraction of ration supplies. (由人民供应部重新分配 CCP) You'll also be sent into a re-education camp in the Xinjiang Uyghur Autonomous Zone. 如果您毫不犹豫，更多的社会信用将从您的个人资料中打折，从而导致口粮供应减少。 您还将被送到新疆维吾尔自治区的再教育营。