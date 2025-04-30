package Main;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;


import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.PickResult;

//kulsum khan :3

public class BlackHole extends JFrame implements ChangeListener, MouseListener {

	private static final long serialVersionUID = 1L;

	//random object used to make black hole appear in random location
    Random rand = new Random();
    
    //main scenegraph transform group (used to remove/add things to frame)
    private static TransformGroup mainTG;
    private static BranchGroup objRoot;
    
    
    //main gui components
    private static JLayeredPane layeredPane;
    private JPanel controlPanel;
    private JSlider zoomSlider;
    private JSlider rotationSlider;
    private JSlider heightSlider;
    private Canvas3D canvas3D;
    private TransformGroup camera;
	private static PickTool pickTool;

    //navigation 
    private double currentDistance = 800.0;
    private double currentRotation = 0.0;
    private double currentHeight = 0.0;
    
    //black hole things
    private static Alpha rotation;
    private static Sphere blackHoleSphere;

    private static BranchGroup blackHoleGroup;
    private static TransformGroup blackHoleTG;
    private static TransformGroup blackHoleRG;
    private static float rad;
    
    //to change background when black hole clicked:
    private static TextureLoader backgroundTextureLoader;
    private static Texture backgroundTexture;
    private static Sphere backSphere;
    private static List<BufferedImage> tunnelImages;
    private static JLabel tunnelLabel;
    private static Timer tunnelTimer;
    private static int currentImageIndex;
    private static SoundUtilityJOAL blackHoleSound;
    private static String blackHoleSoundName = "blackHoleTransition";

    public BlackHole() {
        // Set up the JFrame
        setTitle("Cosmic Explorer");
        setSize(1200, 800); // Size of solar system panel, adjustable
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        //using a layeredpane so everything is shown in order
        layeredPane = new JLayeredPane();
        setContentPane(layeredPane);
        layeredPane.setLayout(new BorderLayout());
        
        
        
        
        //canvas3D to add to the JFrame
        canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas3D.addMouseListener(this);
       
        //canvas 3D is added at the bottom layer, in the most bottom position
        layeredPane.add(canvas3D, BorderLayout.CENTER);
        layeredPane.setLayer(canvas3D, JLayeredPane.DEFAULT_LAYER);
        layeredPane.setPosition(canvas3D, -1);

        
        //control panel
        createControlPanel();
        //this ones added to the layeredPane on the palette layer, at the topmost position
        layeredPane.add(controlPanel, BorderLayout.SOUTH);
        layeredPane.setLayer(controlPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.setPosition(controlPanel, 0);
        
        // The scene graph
        BranchGroup scene = createSceneGraph();

        // Universe setup
        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        universe.getViewingPlatform().setNominalViewingTransform();
        camera = universe.getViewingPlatform().getViewPlatformTransform();
        
        
        updateCamera();
        universe.addBranchGraph(scene);
        universe.getViewer().getView().setBackClipDistance(5000);
    }

    public BranchGroup createSceneGraph() {

        //bounding sphere
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);

        //main branch group
        objRoot = new BranchGroup();
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        //main transform group
        mainTG = new TransformGroup();
     
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        mainTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);

        
        //setting background image onto the sphere:
        backgroundTextureLoader = new TextureLoader("background.jpg", new Container());
        backgroundTexture = backgroundTextureLoader.getTexture();
        Appearance backgroundAppearance = new Appearance();
        backgroundAppearance.setTexture(backgroundTexture);
        
        
        backSphere = new Sphere(500, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD, 1000, backgroundAppearance);
        backSphere.getShape(Sphere.BODY).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        mainTG.addChild(backSphere);
        
        //adding mainTG to the objRoot branch group
        objRoot.addChild(mainTG);
        
        
        //mouse behaviours
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
        
        //creating the blackhole
        createBlackHole(mainTG, bounds);
    
        objRoot.compile();
        return objRoot;
    }
    
 
	@SuppressWarnings("deprecation")
	public void createBlackHole(TransformGroup main, BoundingSphere bounds) {
    	rad = 10f; //radius of black hole (small enough so it can be seen but not easily caught)
    	
    	//black hole is completely dark
    	Appearance a = new Appearance();
    	Material blackHoleMaterial = new Material(new Color3f(0f,0f,0f), new Color3f(0f,0f,0f), new Color3f(0f,0f,0f), new Color3f(0f,0f,0f),0f);
    	a.setMaterial(blackHoleMaterial);
    	
    	//creating a sphere that is pickable
    	blackHoleSphere = new Sphere(rad, Sphere.ALLOW_PICKABLE_WRITE | Sphere.GENERATE_TEXTURE_COORDS | Sphere.GENERATE_NORMALS, a);
    	blackHoleSphere.setCapability(Geometry.ALLOW_INTERSECT);

//    	blackHoleSphere.setUserData(3); //setting user data so we can check for it when picking the object
    	
    	GeometryArray sphereGeom = (GeometryArray)blackHoleSphere.getShape().getGeometry();

    	
    	//morph the sphere by changing its shape:
    	GeometryArray morphedBlackHole1 = morphBlackHole(sphereGeom, 1.0f, 1.5f, 2.0f);
    	GeometryArray morphedBlackHole2 = morphBlackHole(sphereGeom, 0.7f, 1.0f, 1.0f);
    	GeometryArray morphedBlackHole3 = morphBlackHole(sphereGeom, 1.5f, 3.0f, 1.0f);
    	
    	GeometryArray[] geomArrays  = new GeometryArray[] {
    			sphereGeom,
    			morphedBlackHole1,
    			morphedBlackHole2,
    			morphedBlackHole3,
    			sphereGeom
    	};
    	Morph morph = new Morph(geomArrays, a);
    	morph.setCapability(Morph.ALLOW_WEIGHTS_WRITE);
    	Alpha morphAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE |
				Alpha.DECREASING_ENABLE, 0, 0, 4000, 0000, 1000, 4000, 0000, 1000);
   
		MorphBehavior m = new MorphBehavior(morph, morphAlpha);
		m.setSchedulingBounds(bounds);
		
    	blackHoleGroup = new BranchGroup();
    	blackHoleGroup.setCapability(BranchGroup.ALLOW_DETACH);
    	
    	//random coordinates for the translation (this is basically how much the black hole moves when the user explores space)
    	Transform3D t = new Transform3D();
        t.setTranslation(new Vector3d(-300, 0, 0));
        
        
        //transformGroup for translation:
        blackHoleTG = new TransformGroup(t);

    	blackHoleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    	
    	//transformGroup for rotation:
    	blackHoleRG = new TransformGroup();
    	blackHoleRG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    	morph.setUserData(3);
    	blackHoleRG.addChild(morph);
    	blackHoleTG.addChild(m);
    	
    	//add rotation
    	rotation = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000, 0, 0, 0, 0, 0);
    	RotationInterpolator r = new RotationInterpolator(rotation, blackHoleRG); 
    	r.setSchedulingBounds(bounds);
    	
    	//add rotation group to translation group
    	blackHoleTG.addChild(blackHoleRG);
    	
    	//add the rotationInterpolator to the translation group
    	blackHoleTG.addChild(r);
    	
    	//add translation group to the branch group
    	blackHoleGroup.addChild(blackHoleTG);
    	
    	//compile branch group
    	blackHoleGroup.compile();
    	
    	//after compiling add the pick tool
    	pickTool = new PickTool(blackHoleGroup); 
		pickTool.setMode(PickTool.GEOMETRY); //picking by geometry
		
		//add the group to the mainTG
    	main.addChild(blackHoleGroup);
    	
    }
    

	private GeometryArray morphBlackHole(GeometryArray originalGeom, float scaleX, float scaleY, float scaleZ) {
		int vertexCount = originalGeom.getVertexCount();
		int format = originalGeom.getVertexFormat();
		
		Point3f[] originalVerts = new Point3f[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
		    originalVerts[i] = new Point3f();
		}
		originalGeom.getCoordinates(0, originalVerts);
		Point3f[] newVerts = new Point3f[vertexCount];
		
		//getting the vertices of the original sphere
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
		
		newGeom = new TriangleStripArray(vertexCount, format, stripVertexCounts);
		newGeom.setCoordinates(0, newVerts);
		
		
		
		return newGeom;
	}


    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Navigation Controls"));

        // Main controls panel with sliders
        JPanel sliderPanel = new JPanel(new GridLayout(1, 3));
        
        // Zoom slider
        zoomSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 250);
        zoomSlider.setMajorTickSpacing(200);
        zoomSlider.setMinorTickSpacing(50);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);
        zoomSlider.addChangeListener(this);

        // Rotation slider
        rotationSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
        rotationSlider.setMajorTickSpacing(90);
        rotationSlider.setMinorTickSpacing(30);
        rotationSlider.setPaintTicks(true);
        rotationSlider.setPaintLabels(true);
        rotationSlider.addChangeListener(this);

        // Height slider
        heightSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        heightSlider.setMajorTickSpacing(50);
        heightSlider.setMinorTickSpacing(10);
        heightSlider.setPaintTicks(true);
        heightSlider.setPaintLabels(true);
        heightSlider.addChangeListener(this);

        // Adding sliders to control panel
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

    // Camera position
    private void updateCamera() {
        Transform3D trans = new Transform3D();
        trans.rotY(Math.toRadians(currentRotation));
        trans.setTranslation(new Vector3d(0, currentHeight, currentDistance));
        camera.setTransform(trans);
    }

    public static void main(String[] args) {
	    
        BlackHole frame = new BlackHole();
        frame.setVisible(true);
        
        //load images of black hole on startup
	    tunnelImages = new ArrayList<>();
	    for(int i = 1; i<=50; i++) {
	    	try {
	    		BufferedImage img = ImageIO.read(new File("lightTunnel/tunnel"+i+".png")); //can remove println, its just to see that the images have loaded
	    		System.out.println("tunnel"+i);
	    		tunnelImages.add(img);
	    	}
	    	catch(Exception e1) {
	    		e1.printStackTrace();
	    	}
	    }
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		
		int x = e.getX(); int y = e.getY();        // mouse coordinates
		Point3d point3d = new Point3d(), center = new Point3d();
		canvas3D.getPixelLocationInImagePlate(x, y, point3d); // obtain AWT pixel in ImagePlate coordinates
		canvas3D.getCenterEyeInImagePlate(center);         // obtain eye's position in IP coordinates
		
		Transform3D transform3D = new Transform3D();       // matrix to relate ImagePlate coordinates to VW coordinates
		canvas3D.getImagePlateToVworld(transform3D); 
		
		//transforming point3d and center
		transform3D.transform(point3d);                   
		transform3D.transform(center);                     

		Vector3d mouseVec;
		mouseVec = new Vector3d();
		mouseVec.sub(point3d, center);
		mouseVec.normalize();

		pickTool.setShapeRay(point3d, mouseVec);   //send pick ray
		
		if (pickTool.pickClosest() != null) {
			PickResult pickResult = pickTool.pickClosest();//get the closest hit
			javax.media.j3d.Node pickedNode = pickResult.getNode(PickResult.MORPH); //assign a node to the result morph

				Morph tempSphere = (Morph)pickedNode;
				int userData = (int) tempSphere.getUserData(); //get the user data of the sphere
				
				if(userData == 3) {
					lightTunnel(); //if the sphere chosen is the blackhole we commence light tunnel
					
	
			}

		}
				
			
		
	}
	
	public void lightTunnel() {
	    
		//first, make the background (stars) transparent
	    Appearance newAppearance = new Appearance();
	    TransparencyAttributes transparency = new TransparencyAttributes();
	    transparency.setTransparencyMode(TransparencyAttributes.BLENDED); //using blended transparency
	    transparency.setTransparency(1.0f); //completely transparent
	    
	    
	    newAppearance.setTransparencyAttributes(transparency);
	    
	    backSphere.setAppearance(newAppearance); //setting the new appearance for the background

	    mainTG.removeChild(mainTG.indexOfChild(blackHoleGroup)); //removing the blackhole from the main transformgroup
	    
	    currentImageIndex = 0; //starting the images 
	    
	    tunnelLabel = new JLabel(); //creating a JLabel to display the images on
	    tunnelLabel.setOpaque(true);
	    tunnelLabel.setHorizontalAlignment(JLabel.CENTER);
	    
	    //adding the label to the layeredPane on same layer as canvas3D (but opposite end)
	    layeredPane.add(tunnelLabel, BorderLayout.CENTER);
	    layeredPane.setLayer(tunnelLabel, JLayeredPane.DEFAULT_LAYER);
	    layeredPane.setPosition(tunnelLabel, 0); //0 means topmost part of the layer
	    
	    //loading the sound
	    blackHoleSound = new SoundUtilityJOAL();
		if (!blackHoleSound.load(blackHoleSoundName, 0f, 0f, 10f, true)) 
			System.out.println("Could not load " + blackHoleSoundName);
		else
			blackHoleSound.play(blackHoleSoundName);  //when timer starts we play the sound
	    //if there are images we commence displaying images
	    if(!tunnelImages.isEmpty()) {
	    	displayCurrentImage();
	    }
	    
	    tunnelTimer = new Timer(95, new ActionListener( ) { //the 95 is the delay
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		nextImage();
	    	}
	    });
	    tunnelTimer.start(); //timer for the images

	    
	    
	    
	}
	
	public void nextImage() {
		currentImageIndex = currentImageIndex + 1; //move to next image
		if(currentImageIndex != tunnelImages.size()) {
			displayCurrentImage(); 
		}
		else {
			tunnelTimer.stop(); //at end of the images we stop, no wraparound
			blackHoleSound.stop(blackHoleSoundName); //when the photos stop, we also stop the sound
		}
		
	}
	
	public void displayCurrentImage() {
		if(!tunnelImages.isEmpty()) {
			tunnelLabel.setIcon(new ImageIcon(tunnelImages.get(currentImageIndex))); //display image at index (getting image from the List)
		}
	}
	

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
