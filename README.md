# 🚀 Cosmic Explorer  
*A 3D Java Space Adventure*  

[![YouTube Demo](https://img.shields.io/badge/YouTube-Demo-red)](https://www.youtube.com/watch?v=8HnaP_dmnYU)
[![Behind the Scenes](https://img.shields.io/badge/Website-Behind_the_Scenes-blue)](https://cosmicexplorer2800.my.canva.site/)
[![Report] (docs/Cosmic Explorer_Report.pdf)

![Demo GIF](assets/spacegif1.gif) 

## 🌌 About  
**Cosmic Explorer** is an immersive 3D space adventure developed for COMP2800 at BCIT. The game takes players on a journey through a meticulously crafted solar system, where they:  
- Pilot the **Delta spacecraft** with realistic cockpit controls  
- Explore 10 unique planets with orbital physics  
- Discover a morphing black hole triggering a dimensional warp  
- Transition to a whimsical 2D farm world with mutant animals  

Built with **Java3D** (solar system) and **LWJGL** (farm world), this project demonstrates advanced 3D rendering, agile development practices, and seamless engine integration.

## ✨ Key Features  
| System           | Highlights                                                                 |
|------------------|---------------------------------------------------------------------------|
| Solar System     | Procedural orbits with `TransformGroup`, realistic textures               |
| Black Hole       | Morphing geometry via `Morph` class, Blender-rendered light tunnel       |
| Farm World       | LWJGL terrain with blended textures, interactive 2D animals               |
| Audio Design     | Spatial sound effects (JOAL) for immersion                                |
| UI/UX            | Multiple navigation modes (mouse, keyboard, sliders)                      |  

![Demo BlackHole](assets/spacegif2.gif) 
![Demo Farm](assets/spacegif3.gif) 

## 🛠️ Technologies  
- **Core**: Java 8, Java3D, LWJGL  
- **Design**: Blender (3D models), Procreate (concept art)  
- **Audio**: JOAL, VoiceMod  
- **Management**: Scrum (Azure DevOps)  

## 📦 Installation  
1. **Requirements**:  
   - Java 8 JDK  
   - Java3D 1.6+  
   - LWJGL 3.x  

2. **Run**:  
   ```bash
   git clone https://github.com/your-username/Cosmic-Explorer.git
   cd Cosmic-Explorer
   ./gradlew run # or use included IDE configs
