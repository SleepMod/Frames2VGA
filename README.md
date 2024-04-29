# Frames2VGA
This project is STILL being fixed (classes, architecture...)

# Project
A script that transforms multiple pictures that are saved in a folder that gets converted into a ".txl" file.
The .txl doesn't have any particular identifier or signature header, this file is designed to be read "as is"
in order to pass them into the framebuffer (otherwise you would have some unecessary color information & you
would need to rewrite your code in order to skip the signature).

# Other palettes
Modify as you wish the vga13 class since its the ones that contains the color information. You can adapt it to
your own necessities. In my case it is designed to support the "old" 320x200 13h mode.

# Usage:
1. Input: Path folder where all pictures are saved (if its a gif, they must be splitted beforehand)
2. Input: Output folder where all .txl files will be saved.
3. Bytes to bound: If the input is less than 8192 bytes, it will default it to 64000 bytes (this script is designed for microkernels, thoogh, can be changed)
4. & 5. Resolutions: Set 0 for auto on both and the program will try to adapt the picture the best way possible, or just set it manually by you. 
