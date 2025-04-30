//Team 4- Cosmic Explorer

package Main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.Morph;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.QuadArray;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class SSEngine extends JFrame implements ChangeListener, MouseListener {

    private static final long serialVersionUID = 1L;
    private Random rand = new Random();

    // UI Components
    private JPanel controlPanel;
    private JSlider zoomSlider;
    private JSlider rotationSlider;
    private JSlider heightSlider;
    private Canvas3D canvas3D;
    private TransformGroup camera;

    // Navigation state
    private double currentDistance = 250.0;
    private double currentRotation = 0.0;
    private double currentHeight = 0.0;

    // Black Hole components
    private static Alpha blackHoleRotation;
    private static Sphere blackHoleSphere;
    private static BranchGroup blackHoleGroup;
    private static TransformGroup blackHoleTG;
    private static TransformGroup blackHoleRG;
    private static float blackHoleRad;
    private static PickTool pickTool;
    private static List<BufferedImage> tunnelImages;
    private static JLabel tunnelLabel;
    private static Timer tunnelTimer;
    private static int currentImageIndex;
    private static SoundUtilityJOAL blackHoleSound;
    private static String blackHoleSoundName = "blackHoleTransition";
    private BranchGroup scene; // Store reference to scene graph
    
    //Audio files
    private static List<String> audioFiles;
    private static SoundUtilityJOAL audioPlayer;
    private static Timer audioTimer;
    private static int currentAudioIndex = 0;
    private static String currentlyPlaying = null;

    public SSEngine() {
        setTitle("Cosmic Explorer");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas3D.addMouseListener(this);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(canvas3D, BorderLayout.CENTER);

        createControlPanel();
        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        scene = createSceneGraph();

        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        universe.getViewingPlatform().setNominalViewingTransform();
        camera = universe.getViewingPlatform().getViewPlatformTransform();
        updateCamera();
        universe.addBranchGraph(scene);
        universe.getViewer().getView().setBackClipDistance(5000);
    }

    public BranchGroup createSceneGraph() {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
        BranchGroup objRoot = new BranchGroup();

        TransformGroup mainTG = new TransformGroup();
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        TextureLoader backgroundTextureLoader = new TextureLoader("background.jpg", new Container());
        Texture backgroundTexture = backgroundTextureLoader.getTexture();
        Appearance backgroundAppearence = new Appearance();
        backgroundAppearence.setTexture(backgroundTexture);
        Sphere backgroundSphere = new Sphere(500,
                Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD,
                1000, backgroundAppearence);
        backgroundSphere.getShape(Sphere.BODY).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        mainTG.addChild(backgroundSphere);

        objRoot.addChild(mainTG);

        TransformGroup cockpit = createCockpit();
        mainTG.addChild(cockpit);
        
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(mainTG);
        objRoot.addChild(behavior);
        behavior.setSchedulingBounds(bounds);

        MouseZoom behavior2 = new MouseZoom();
        behavior2.setTransformGroup(mainTG);
        objRoot.addChild(behavior2);
        behavior2.setSchedulingBounds(bounds);

        MouseTranslate behavior3 = new MouseTranslate();
        behavior3.setTransformGroup(mainTG);
        objRoot.addChild(behavior3);
        behavior3.setSchedulingBounds(bounds);

        createPlanets(mainTG, bounds);
        createBlackHole(mainTG, bounds);

        objRoot.compile();
        return objRoot;
    }
    
    private void initializeAudio() {
        audioFiles = new ArrayList<>();
        
        // Load audio file paths
        for (int i = 1; i <= 9; i++) {
            audioFiles.add("audio/part" + i + ".wav");
        }
        
        audioPlayer = new SoundUtilityJOAL();
        
        // Create timer to play audio every 15 seconds
        audioTimer = new Timer(15000, e -> playNextAudio());
        audioTimer.start();
    }

    private void playNextAudio() {
        if (audioFiles == null || audioFiles.isEmpty()) return;
        
        // Stop currently playing audio if any
        if (currentlyPlaying != null) {
            audioPlayer.stop(currentlyPlaying);
        }
        
        // Play the current audio file
        currentlyPlaying = audioFiles.get(currentAudioIndex);
        if (!audioPlayer.load(currentlyPlaying, 0f, 0f, 10f, false)) {
            System.out.println("Could not load " + currentlyPlaying);
        } else {
            audioPlayer.play(currentlyPlaying);
        }
        
        // Move to next audio file (loop back to start after last file)
        currentAudioIndex = (currentAudioIndex + 1) % audioFiles.size();
    }

    public void createPlanets(TransformGroup mainTG, BoundingSphere bounds) {
        int[][] orbitals = {
                {0, 0, 0},    // Sun
                {2, 0, 16000}, {4, 0, 14000}, {6, 0, 18000}, {8, 0, 15000},
                {10, 0, 20000}, {12, 0, 16000}, {14, 0, 17000}, {16, 0, 15000},
                {18, 0, 16000}, {20, 0, 13000}
        };

        int[][] translations = {
                {0, 0, 0},        // Sun
                {150, 0, 0},    {-80, 0, 20},    {20, 0, -90},    {-90, 0, -40},
                {10, 0, 100},    {-50, 0, 40},    {60, 0, -30},    {-20, 0, -70},
                {40, 0, 80},    {-30, 0, 120}
        };

        int[] axis = {
                3300, 12000, 4250, 4100, 52000, 11800, 7900, 9750, 6200, 8600, 11700
        };

        int[][] planets = {
                {20, 75}, {7, 85}, {4, 89}, {5, 83}, {4, 84},
                {5, 80}, {3, 81}, {12, 40}, {2, 86}, {4, 82}, {6, 87}
        };

        for (int i = 0; i < orbitals.length; i++) {
            BranchGroup background = new BranchGroup();
            mainTG.addChild(background);

            int[] thisOrbital = orbitals[i];
            TransformGroup orbitalRotation = createOrbitalRotation(bounds,
                    thisOrbital[0], thisOrbital[1], thisOrbital[2]);
            background.addChild(orbitalRotation);

            int[] thisTranslation = translations[i];
            TransformGroup translation = createTranslation(
                    thisTranslation[0], thisTranslation[1], thisTranslation[2]);
            orbitalRotation.addChild(translation);

            int thisAxis = axis[i];
            TransformGroup axisRotation = createAxisRotation(bounds, thisAxis);
            translation.addChild(axisRotation);

            int[] planetAttributes = planets[i];
            Sphere planet = createPlanet(
                    "plan" + i + ".jpg",
                    planetAttributes[0], planetAttributes[1]);
            axisRotation.addChild(planet);

            if (i == 7) {
                generateParticleRing(axisRotation);
            }
        }
    }
    
    private TransformGroup createCockpit() {
        TransformGroup cockpitGroup = new TransformGroup();
        
        // Load cockpit texture
        Texture cockpitTexture = new TextureLoader("cockpit.png", canvas3D).getTexture();
        if (cockpitTexture == null) {
            System.err.println("Error loading cockpit texture");
            return cockpitGroup;
        }

        // Create quad geometry for cockpit
        QuadArray cockpitGeometry = new QuadArray(4, 
            GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
        
        float width = 2.0f;
        float height = 1.5f;
        float depth = -1.0f; // Negative z to place in front of viewer
        
        cockpitGeometry.setCoordinate(0, new Point3f(-width, -height, depth));
        cockpitGeometry.setCoordinate(1, new Point3f(width, -height, depth));
        cockpitGeometry.setCoordinate(2, new Point3f(width, height, depth));
        cockpitGeometry.setCoordinate(3, new Point3f(-width, height, depth));
        
        cockpitGeometry.setTextureCoordinate(0, 0, new TexCoord2f(0, 0));
        cockpitGeometry.setTextureCoordinate(0, 1, new TexCoord2f(1, 0));
        cockpitGeometry.setTextureCoordinate(0, 2, new TexCoord2f(1, 1));
        cockpitGeometry.setTextureCoordinate(0, 3, new TexCoord2f(0, 1));

        // Configure appearance
        Appearance cockpitAppearance = new Appearance();
        cockpitAppearance.setTexture(cockpitTexture);
        
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        cockpitAppearance.setTextureAttributes(texAttr);
        
        // Ensure cockpit is always visible
        TransparencyAttributes transAttr = new TransparencyAttributes();
        transAttr.setTransparencyMode(TransparencyAttributes.NONE);
        cockpitAppearance.setTransparencyAttributes(transAttr);

        // Create and position cockpit
        Shape3D cockpitShape = new Shape3D(cockpitGeometry, cockpitAppearance);
        
        Transform3D cockpitTransform = new Transform3D();
        cockpitTransform.setTranslation(new Vector3f(0, 0, 0));
        
        TransformGroup positionGroup = new TransformGroup(cockpitTransform);
        positionGroup.addChild(cockpitShape);
        cockpitGroup.addChild(positionGroup);
        
        return cockpitGroup;
    }

    public void createBlackHole(TransformGroup main, BoundingSphere bounds) {
        blackHoleRad = 10f;
        
        // Create unique appearance only for black hole
        Appearance blackHoleAppearance = new Appearance();
        blackHoleAppearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        
        // Set up black hole material (completely non-reflective)
        Material blackHoleMaterial = new Material(
            new Color3f(0.01f, 0.01f, 0.01f),  // Ambient (very low)
            new Color3f(0.0f, 0.0f, 0.0f),     // Emissive (none)
            new Color3f(0.0f, 0.0f, 0.0f),     // Diffuse (none)
            new Color3f(0.0f, 0.0f, 0.0f),     // Specular (none)
            0.0f);                             // Shininess (none)
        blackHoleMaterial.setLightingEnable(true);
        blackHoleAppearance.setMaterial(blackHoleMaterial);
        
        // Create black hole sphere with proper capabilities
        blackHoleSphere = new Sphere(blackHoleRad, 
                Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS,
                50, blackHoleAppearance);
        
        // Enable all necessary capabilities for interaction
        blackHoleSphere.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        blackHoleSphere.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        blackHoleSphere.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        blackHoleSphere.setCapability(Shape3D.ALLOW_PICKABLE_WRITE);
        blackHoleSphere.setPickable(true);
        
        // Get geometry and prepare for morphing
        GeometryArray sphereGeom = (GeometryArray)blackHoleSphere.getShape(Sphere.BODY).getGeometry();
        sphereGeom.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
        
        // Create morphed versions
        GeometryArray morphedBlackHole1 = morphBlackHole(sphereGeom, 1.0f, 1.5f, 2.0f);
        GeometryArray morphedBlackHole2 = morphBlackHole(sphereGeom, 0.7f, 1.0f, 1.0f); 
        GeometryArray morphedBlackHole3 = morphBlackHole(sphereGeom, 1.5f, 3.0f, 1.0f);
        
        GeometryArray[] geomArrays = new GeometryArray[] {
            sphereGeom, morphedBlackHole1, morphedBlackHole2, morphedBlackHole3, sphereGeom
        };
        
        // Create morph with black hole appearance
        Morph morph = new Morph(geomArrays, blackHoleAppearance);
        morph.setCapability(Morph.ALLOW_WEIGHTS_WRITE);
        morph.setUserData(3); // Identifier for black hole
        
        // Set up morph behavior
        Alpha morphAlpha = new Alpha(-1, 
            Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 
            0, 0, 4000, 0, 1000, 4000, 0, 1000);
        MorphBehavior m = new MorphBehavior(morph, morphAlpha);
        m.setSchedulingBounds(bounds);
        
        // Create black hole branch group
        blackHoleGroup = new BranchGroup();
        blackHoleGroup.setCapability(BranchGroup.ALLOW_DETACH);
        
        // Position in far left corner (-300,0,0)
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3d(-300, 0, 0));
        
        blackHoleTG = new TransformGroup(t);
        blackHoleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        // Rotation group
        blackHoleRG = new TransformGroup();
        blackHoleRG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        blackHoleRG.addChild(morph);
        
        // Add morph behavior
        blackHoleTG.addChild(m);
        blackHoleTG.addChild(blackHoleRG);
        
        // Set up rotation
        blackHoleRotation = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000, 0, 0, 0, 0, 0);
        RotationInterpolator r = new RotationInterpolator(blackHoleRotation, blackHoleRG); 
        r.setSchedulingBounds(bounds);
        blackHoleTG.addChild(r);
        
        // Add to hierarchy
        blackHoleGroup.addChild(blackHoleTG);
        blackHoleGroup.compile();
        
        // Set up picking
        pickTool = new PickTool(blackHoleGroup);
        pickTool.setMode(PickTool.GEOMETRY);
        
        // Add to main scene
        main.addChild(blackHoleGroup);
    }

    private GeometryArray morphBlackHole(GeometryArray originalGeom, float scaleX, float scaleY, float scaleZ) {
        int vertexCount = originalGeom.getVertexCount();
        Point3f[] originalVerts = new Point3f[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            originalVerts[i] = new Point3f();
        }
        originalGeom.getCoordinates(0, originalVerts);
        Point3f[] newVerts = new Point3f[vertexCount];
        
        for(int i = 0; i<vertexCount; i++) {
            newVerts[i] = new Point3f(
                originalVerts[i].x * scaleX,
                originalVerts[i].y * scaleY,
                originalVerts[i].z * scaleZ);
        }
        
        TriangleStripArray newGeom = null;
        TriangleStripArray ogGeomTSA = (TriangleStripArray)originalGeom;
        int numStrips = ogGeomTSA.getNumStrips();
        int[] stripVertexCounts = new int[numStrips];
        ogGeomTSA.getStripVertexCounts(stripVertexCounts);
        
        newGeom = new TriangleStripArray(vertexCount, originalGeom.getVertexFormat(), stripVertexCounts);
        newGeom.setCoordinates(0, newVerts);
        
        return newGeom;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point3d clickPoint = new Point3d();
        Point3d eyePoint = new Point3d();
        
        // Convert mouse coordinates to 3D points
        canvas3D.getPixelLocationInImagePlate(e.getX(), e.getY(), clickPoint);
        canvas3D.getCenterEyeInImagePlate(eyePoint);
        
        // Transform to world coordinates
        Transform3D transform = new Transform3D();
        canvas3D.getImagePlateToVworld(transform);
        transform.transform(clickPoint);
        transform.transform(eyePoint);
        
        // Create pick ray
        Vector3d rayDir = new Vector3d();
        rayDir.sub(clickPoint, eyePoint);
        rayDir.normalize();
        
        // Setup pick tool
        pickTool.setShapeRay(eyePoint, rayDir);
        PickResult result = pickTool.pickClosest();
        
        if (result != null) {
            javax.media.j3d.Node node = result.getObject();
            if (node != null && node.getUserData() != null && 
                ((Integer)node.getUserData()) == 3) {
                lightTunnel();
            }
        }
    }


    public void lightTunnel() {
        Appearance newAppearance = new Appearance();
        TransparencyAttributes transparency = new TransparencyAttributes();
        transparency.setTransparencyMode(TransparencyAttributes.BLENDED);
        transparency.setTransparency(1.0f);
        newAppearance.setTransparencyAttributes(transparency);
        
        // Access the background sphere through our stored scene reference
        TransformGroup mainTG = (TransformGroup)scene.getChild(0);
        BranchGroup bgGroup = (BranchGroup)mainTG.getChild(0);
        Sphere bgSphere = (Sphere)bgGroup.getChild(0);
        bgSphere.setAppearance(newAppearance);

        mainTG.removeChild(mainTG.indexOfChild(blackHoleGroup));
        
        currentImageIndex = 0;
        tunnelLabel = new JLabel();
        tunnelLabel.setOpaque(true);
        tunnelLabel.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(tunnelLabel, BorderLayout.CENTER);
        
        blackHoleSound = new SoundUtilityJOAL();
        if (!blackHoleSound.load(blackHoleSoundName, 0f, 0f, 10f, true)) {
            System.out.println("Could not load " + blackHoleSoundName);
        } else {
            blackHoleSound.play(blackHoleSoundName);
        }
        
        if(!tunnelImages.isEmpty()) {
            displayCurrentImage();
        }
        
        tunnelTimer = new Timer(95, e -> nextImage());
        tunnelTimer.start();
    }

    public void nextImage() {
        currentImageIndex++;
        if(currentImageIndex != tunnelImages.size()) {
            displayCurrentImage();
        } else {
            tunnelTimer.stop();
            if (blackHoleSound != null) {
                blackHoleSound.stop(blackHoleSoundName);
            }
        }
    }

    public void displayCurrentImage() {
        if(!tunnelImages.isEmpty() && currentImageIndex < tunnelImages.size()) {
            tunnelLabel.setIcon(new ImageIcon(tunnelImages.get(currentImageIndex)));
        }
    }

    // Other mouse event handlers
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // Original planet creation methods
    public TransformGroup createOrbitalRotation(BoundingSphere bounds, double xAngle, double zAngle, int rotationSpeed) {
        TransformGroup transformGroup = new TransformGroup();
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        Transform3D yAxis = new Transform3D();
        Transform3D newAxis = new Transform3D();
        yAxis.rotX((Math.PI / 180.0) * xAngle);
        newAxis = yAxis;

        Alpha rotation = new Alpha(-1, rotationSpeed);
        RotationInterpolator rotatorTGA = new RotationInterpolator(
                rotation, transformGroup, yAxis, 0, (float) Math.PI * (2.0f));
        rotatorTGA.setSchedulingBounds(bounds);
        transformGroup.addChild(rotatorTGA);

        return transformGroup;
    }

    public TransformGroup createTranslation(double x, double y, double z) {
        Transform3D temp = new Transform3D();
        Vector3d translationVector = new Vector3d(x, y, z);
        temp.setTranslation(translationVector);

        TransformGroup translation = new TransformGroup();
        translation.setTransform(temp);
        return translation;
    }

    public TransformGroup createAxisRotation(BoundingSphere bounds, int rotationSpeed) {
        TransformGroup transformGroup = new TransformGroup();
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        Transform3D yAxis = new Transform3D();
        Alpha rotation = new Alpha(-1, rotationSpeed);
        RotationInterpolator rotatorTGA = new RotationInterpolator(
                rotation, transformGroup, yAxis, 0, (float) Math.PI * (2.0f));
        rotatorTGA.setSchedulingBounds(bounds);
        transformGroup.addChild(rotatorTGA);

        return transformGroup;
    }

    public Sphere createPlanet(String fileName, int radius, int roughness) {
        TextureLoader textureLoader = new TextureLoader(fileName, new Container());
        Texture texture = textureLoader.getTexture();
        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        Appearance appearance = new Appearance();
        appearance.setTexture(texture);
        appearance.setTextureAttributes(textureAttributes);
        int primFlags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;
        return new Sphere(radius, primFlags, roughness, appearance);
    }

    public void generateParticleRing(TransformGroup toAddTo) {
        int numOfParticles = 700;
        for (int j = 0; j < numOfParticles; j++) {
            int radius = rand.nextInt(6) + 20;
            int xDisplacement, zDisplacement;

            if (j < (numOfParticles / 2)) {
                xDisplacement = rand.nextInt(26);
                if (xDisplacement > radius) xDisplacement = radius;
                double z = Math.sqrt(Math.pow(radius, 2) - Math.pow(xDisplacement, 2));
                zDisplacement = (int) Math.round(z);
            } else {
                zDisplacement = rand.nextInt(26);
                if (zDisplacement > radius) zDisplacement = radius;
                double z = Math.sqrt(Math.pow(radius, 2) - Math.pow(zDisplacement, 2));
                xDisplacement = (int) Math.round(z);
            }

            if (j % 2 == 0) xDisplacement = -xDisplacement;
            if ((j / 2) % 2 == 0) zDisplacement = -zDisplacement;

            TransformGroup particleTranslation = createTranslation(xDisplacement, 0, zDisplacement);
            toAddTo.addChild(particleTranslation);

            Sphere particle = createPlanet("comet.jpg", 1, 5);
            particleTranslation.addChild(particle);
        }
    }

    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Navigation Controls"));

        JPanel sliderPanel = new JPanel(new GridLayout(1, 3));
        
        zoomSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 250);
        zoomSlider.setMajorTickSpacing(200);
        zoomSlider.setMinorTickSpacing(50);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);
        zoomSlider.addChangeListener(this);

        rotationSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
        rotationSlider.setMajorTickSpacing(90);
        rotationSlider.setMinorTickSpacing(30);
        rotationSlider.setPaintTicks(true);
        rotationSlider.setPaintLabels(true);
        rotationSlider.addChangeListener(this);

        heightSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        heightSlider.setMajorTickSpacing(50);
        heightSlider.setMinorTickSpacing(10);
        heightSlider.setPaintTicks(true);
        heightSlider.setPaintLabels(true);
        heightSlider.addChangeListener(this);

        JPanel zoomPanel = new JPanel(new BorderLayout());
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomPanel.add(zoomSlider, BorderLayout.CENTER);

        JPanel rotationPanel = new JPanel(new BorderLayout());
        rotationPanel.setBorder(BorderFactory.createTitledBorder("Rotation"));
        rotationPanel.add(rotationSlider, BorderLayout.CENTER);

        JPanel heightPanel = new JPanel(new BorderLayout());
        heightPanel.setBorder(BorderFactory.createTitledBorder("Height"));
        heightPanel.add(heightSlider, BorderLayout.CENTER);

        sliderPanel.add(zoomPanel);
        sliderPanel.add(rotationPanel);
        sliderPanel.add(heightPanel);
        controlPanel.add(sliderPanel, BorderLayout.CENTER);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == zoomSlider) {
            currentDistance = zoomSlider.getValue();
        } else if (e.getSource() == rotationSlider) {
            currentRotation = rotationSlider.getValue();
        } else if (e.getSource() == heightSlider) {
            currentHeight = heightSlider.getValue();
        }
        updateCamera();
    }

    private void updateCamera() {
        Transform3D trans = new Transform3D();
        trans.rotY(Math.toRadians(currentRotation));
        trans.setTranslation(new Vector3d(0, currentHeight, currentDistance));
        camera.setTransform(trans);
    }

    public static void main(String[] args) {
        SSEngine frame = new SSEngine();
        frame.setVisible(true);
        
        // Load tunnel images
        tunnelImages = new ArrayList<>();
        for(int i = 1; i <= 50; i++) {
            try {
                BufferedImage img = ImageIO.read(new File("lightTunnel/tunnel"+i+".png"));
                tunnelImages.add(img);
            } catch(Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}