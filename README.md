Beneath, for Ludum Dare 29! Theme: Beneath the Surface

You're trapped beneath the surface, and must escape. Unfortunately for you, the cavern underground in which you are in is slowly flooding with water, meaning you must find a way out as quickly as possible. Utilizing the available light you have, find the way out! 

So I made this game using LWJGL (OpenGL); no engines. I had to make the engine by myself, including the lighting and shadows with the help of GLSL. It was a great learning experience, but unfortunately hindered my ability to work on the actual game. I say this every time, but... no more writing games from scratch for Ludum Dare! 48 hours should be spent on the gameplay, not the engine! So hopefully next time I'll either be using my own engine, or something like LibGDX to speed things up, and enable me to work on gameplay most of the time. 

The game uses OpenGL 3.0, so it may not work on older graphics cards. The game also might not run too well for some of you: this is due to the tile rendering, NOT THE LIGHTING! Each tile is rendered separately instead of pushed into a Vertex Array and rendered simultaneously: that's the problem, and I may fix it later. 