# MC_LostWoods
MC Plugin for the Lost Woods minigame

# Summary
This is a MineCraft Bukkit plugin I developed for playing a co-op maze / capture-the-flag game on a multiplayer server. This accompanies a custom map I made, which is necessary for playing this game. The game logic is tied closely with the materials and architecture of the arena I made, so I highly recommend you use that map. You can download a copy here: https://mega.nz/file/u851yS4L#IR7k9AulfDk52VO-fc_-9MNJkBuOYzGIlTR5elOpyus

# Rules
Lava Wall is a co-op game in which players move through a 9x9x9 matrix of identical rooms looking for a gold block. Each room has openings to all adjacent rooms, including vertically via a water column. If a player enters the vertical column where the gold block is, the block tries to flee to an unoccupied adjacent vertical column, spawning in at a random height.

Mathematically, this means the game cannot be solved alone, and is very difficult to solve with two players. There are also fun adverse incentives at play here: being lower in the column makes it much easier to detect when you've just entered a room where the block lies (the block stops the water column so you can see when it has recently left) - however, being lower than your opponent/teammate decreases your odds of claiming the prize, as, again, the gold block blocks the water column, making vertical travel from underneath impossible. When you finally surround the block, you absolutely want to be higher than your opponent. 

The first player to touch the blocks wins the game. This one is a blast - hope you enjoy it!



