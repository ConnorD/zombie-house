# zombie-house
Authors: Ederin Igharoro & Connor Denman

CS351L at Univ of New Mexico

Prof. Castellanos

How to Play:

No command line arguments, simply run the "zombie_house.jar" file.

You are spawned in a spooky, dark house, and you are trying to find the exit before the zombies eat your brains. If you die in the process, you are re-spawned in the same spot as before, but you may observe the ghost of your past self move around as reference for which rooms you had visited already.

Once you find the exit, your player is spawned in the next level and zombies become more powerful.

Controls are "ASDW" along with holding "Shift" for sprint, "Esc" to pause the game, and left click for attacking a zombie when you're in range. Zombies have health status bars above their heads so you know how much damage you've inflicted.

What Works:

- User interface
  - Start/Pause/Game Over dialogs.
  - HUD that displays current player health and stamina.
  - Zombie health status bars.
- Zombie and Player health and stamina system with combat interaction.
- Zombie and Player collision detection.
- Record player movement and health to render the past player for single-player cooperative.
- Various audio additions including background music, player death sounds, attack sounds, etc.
- Smooth rendering of the house.
- Overall game logic.
- Various bug fixes and enhancements.

What Does Not Work:

- Zombie bifurcation (we started on the feature but simply ran out of time).
- Render a better 3D mesh for past player (instead, we are currently using a JavaFX Box).

What We're Most Happy With:

- This project can now actually be played as a game; combat, respawning, user interface, etc.
- Despite being only a group of 2, we accomplished a lot.
  - With another group member or more time we could've met absolutely all requirements.
  
Credits:

- Started with code posted on course site from Maxwell, Rob, and Stephen.
- Audio files downloaded for free from http://soundbible.com/ and https://www.freesound.org
