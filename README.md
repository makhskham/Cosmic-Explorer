# 🚀 Cosmic Explorer  
*A 3D Java Space Adventure*  

[![YouTube Demo](https://img.shields.io/badge/YouTube-Demo-red)](https://www.youtube.com/watch?v=8HnaP_dmnYU)

[![Behind the Scenes](https://img.shields.io/badge/Website-Behind_the_Scenes-blue)](https://cosmicexplorer2800.my.canva.site/)

[![Report PDF](https://img.shields.io/badge/Download-Report-green)](docs/Cosmic%20Explorer_Report.pdf)

![Demo GIF](assets/spacegif1.gif) 
*Delta spacecraft cockpit with solar system view*

## 🌌 About  
**Cosmic Explorer** is an immersive 3D space adventure developed for COMP2800 at BCIT. Players:  
- Pilot the **Delta spacecraft** through a realistic solar system  
- Discover a morphing black hole triggering a dimensional warp  
- Transition to a whimsical 2D farm world with mutant animals  
- Built with **Java3D** (solar system) + **LWJGL** (farm world)  

**Key Achievements**:  
✔ Seamless Java3D → LWJGL engine transition  
✔ Scrum-managed development (2 sprints)  
✔ Blend of scientific and stylized 3D rendering  


![Demo BlackHole](assets/spacegif2.gif) 
*Delta spacecraft cockpit going through the blackhole sequence*


## ✨ Features  
| System           | Highlights                                                                 |
|------------------|---------------------------------------------------------------------------|
| Solar System     | Procedural orbits with `TransformGroup`, Saturn's 700-particle ring       |
| Black Hole       | Morphing geometry, Blender-rendered light tunnel (50 PNG sequence)        |
| Farm World       | Cartoon animals (Procreate → Blender Grease Pencil), terrain blending     |
| UI/UX            | Triple navigation (mouse/keyboard/sliders), comic sans narration          |

---

![Demo Farm](assets/spacegif3.gif) 
*Delta spacecraft cockpit arriving at the final farm location*

## 🛠️ Tech Stack  
- **Core**: Java 8, Java3D, LWJGL  
- **Design**: Blender 4.3.2 (models), Procreate (concept art)  
- **Audio**: JOAL (spatial sound), VoiceMod (announcer)  
- **Management**: Azure DevOps (Scrum) 

---

## 📦 Installation  
1. **Requirements**:  
   - Java 8 JDK  
   - Java3D 1.6+  
   - LWJGL 3.x  

2. **Run**:  
   ```bash
   git clone https://github.com/your-username/Cosmic-Explorer.git
   cd Cosmic-Explorer
   ./gradlew run # or use included IDE configs and import as Maven project in IDE

---

   ## 🎥 Media Showcase
**Video Demo**  
   https://www.youtube.com/watch?v=8HnaP_dmnYU
   
   *Full gameplay walkthrough (1:45 min)*
   
**Behind the Scenes**  
Explore our interactive development website presentation

https://cosmicexplorer2800.my.canva.site/
   - Blender → Java3D pipeline
   - Scrum sprint retrospectives
   - Concept art revolution

---

   ## 📂 Repository Structure

   Cosmic-Explorer/
├── java3d/               # Solar system module
│   ├── src/              # SSEngine, planet generation
│   └── assets/           # Planet textures, cockpit.png
├── lwjgl/                # Farm world module
│   ├── entities/         # Camera, Light classes  
│   └── textures/         # Terrain blend maps  
├── docs/
│   └── Cosmic Explorer_Report.pdf   # 72-page technical report  
└── README.md             # This file #

---

   ## 📄 Documentation
-**Full Technical Report** (72 pages)
   (docs/Cosmic%20Explorer_Report.pdf)
      - Section 3: Class diagrams & implementation
      - Appendix A: Complete backlog items

---

   ## 👥 Team Credits
   | Role           | Member                                                                 |
|------------------|---------------------------------------------------------------------------|
| Product Owner     | Makhsuma Khamzaliyeva       |
| Scrum Master       | Kulsum Khan        |
| 3D Artist       | Mahnoz Akhtari     |
| Special Effects and Audio            | Simbarashe Mamvura          |
| Farm Creator       | Hamnah Riaz     |

---
   
   
