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


---

## ✨ Features  
| System           | Highlights                                                                 |
|------------------|---------------------------------------------------------------------------|
| Solar System     | Procedural orbits with `TransformGroup`, Saturn's 700-particle ring       |
| Black Hole       | Morphing geometry, Blender-rendered light tunnel (50 PNG sequence)        |
| Farm World       | Cartoon animals (Procreate → Blender Grease Pencil), terrain blending     |
| UI/UX            | Triple navigation (mouse/keyboard/sliders), comic sans narration          |


![Demo Farm](assets/spacegif3.gif) 
*Delta spacecraft cockpit arriving at the final farm location*


---

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

### Video Demo  
[![Watch the Demo](assets/screenshots/yt_thumbnail.png)](https://www.youtube.com/watch?v=8HnaP_dmnYU)  
*Full gameplay walkthrough showing solar system exploration and farm world transition*

### Behind-the-Scenes Website  
Explore our **[interactive development portal](https://cosmicexplorer2800.my.canva.site/)** featuring:  
- **Blender → Java3D pipeline** breakdown  
- **Scrum sprint retrospectives** with task boards  
- **Concept art evolution** from sketches to 3D models  

---

   ## 📂 Repository Structure

```
Cosmic-Explorer/
├── assets/
│   ├── audio/           # Sound files (e.g. blackHoleTransition.wav)
│   ├── cockpit/         # Blender spacecraft files (.blend)
│   ├── lightTunnel/     # Black hole PNG sequence (tunnel01-50.png)
│   └── res/             # Farm world textures
├── docs/
│   ├── Cosmic Explorer_Report.pdf
│   └── README.md        # Technical specs
├── lib/
│   ├── jars/            # Java3D/LWJGL libraries
│   └── natives/         # OS-specific binaries
├── src/                 # Java source code
└── README.md            # Project overview
```

---

## 📄 Documentation

- **Full Technical Report** (35 pages)
  - **Section 3**: Class diagrams & implementation
  - **Appendix A**: Complete backlog items
  - **Appendix B**: Team contributions
  - Located at: `docs/Cosmic Explorer_Report.pdf`

- **Scrum Artifacts**
  - **Azure DevOps Board**: Full sprint backlog
  - **Sprint Retrospectives**: Lessons learned
  - **Daily Standup Notes**: Development progress

- **Design Documents**
  - **Blender Model Specifications**: Cockpit/black hole assets
  - **Sound Design Script**: Announcer dialogue
  - **Farm World Concept Art**: Procreate sketches

---

## 👥 Team Credits

| **Role**          | **Member**           | **Key Contributions** |
|-------------------|----------------------|-----------------------|
| Scrum Master      | Kulsum Khan          | Black hole physics, Azure DevOps management |
| Product Owner     | Makhsuma Khamzaliyeva| Solar system rendering, UI design |
| 3D Artist         | Mahnoz Akhtari       | Blender models (cockpit, farm assets) |
| Audio Engineer    | Simbarashe Mamvura   | JOAL sound implementation |
| Farm Developer    | Hannah Riaz          | LWJGL terrain generation |

---

## 📜 License

**MIT License**  
Copyright © 2025 BCIT COMP2800 Team 4

   
   
