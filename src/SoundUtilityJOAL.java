package Main;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALConstants;
import com.jogamp.openal.ALException;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

public class SoundUtilityJOAL {

	private AL al; // to access the JOAL API

	private HashMap<String, int[]> buffersMap;
	private HashMap<String, int[]> sourcesMap;

	// listener info
	private float xLis, yLis, zLis; // current position
	private float[] oriLis; 
	private int angleLis = 0;
	// anti-clockwise rotation anyway from -z axis

	public static void main(String[] args) { 
		String soundName = "blackHoleTransition";
		//the listener is at (0,0,0) facing along the -z axis
		SoundUtilityJOAL soundMan = new SoundUtilityJOAL();
		soundMan.moveListener(0, 0.20f); //placing the sound 

		if (!soundMan.load(soundName, 1, 0, 0, true)) 
			System.exit(1);
		soundMan.play(soundName);  //playing the sound

		float x = -31f;
		for (int i = 0; i < 60; i++) {
			soundMan.setPos(soundName, ++x / 10, 0, 0);
			try {
				Thread.sleep(250); // sleep for 0.25 secs
			} catch (InterruptedException ex) {
			}
		}

		soundMan.cleanUp();
	}

	/* construction linking to OpenAL via JOAL and positioning listener at origin */
	public SoundUtilityJOAL() {
		buffersMap = new HashMap<>();
		sourcesMap = new HashMap<>();

		initOpenAL();
		initListener();
	}

	/* function to set up the link to OpenAL (OpenAL Utility Toolkit) via JOAL*/
	private void initOpenAL() { //creates and initializes the openAL
		try {  
			ALut.alutInit();
			al = ALFactory.getAL();  //get OpenAL
			al.alGetError(); //clear any error bits
		} catch (ALException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/* function to position the listener at (0, 0, 0) facing towards (0, 0, -1) */
	private void initListener()	{
		xLis = yLis = 0; zLis = 0.0f;   //position listener
		al.alListener3f(ALConstants.AL_POSITION, xLis, yLis, zLis);
		al.alListener3i(ALConstants.AL_VELOCITY, 0, 0, 0);  //no velocity so no movement

		oriLis = new float[] { xLis, yLis, zLis - 1.0f, 0.0f, 1.0f, 0.0f};
		al.alListenerfv(ALConstants.AL_ORIENTATION, oriLis, 0);
	}

// --------------- source methods -----------------------------

	/* function to load sound as a buffer and use it to initialize OpenAL source */
	public boolean load(String nm, boolean toLoop) {
		if (sourcesMap.get(nm) != null) {
			System.out.println(nm + " already loaded");
			return true;
		}
		int[] buffer = initBuffer(nm);
		if (buffer == null)
			return false;

		int[] source = initSource(nm, buffer, toLoop);
		if (source == null) {
			al.alDeleteBuffers(1, buffer, 0); // no need for the buffer anymore
			return false;
		}

		buffersMap.put(nm, buffer);    // store sound name and buffer its hash map
		sourcesMap.put(nm, source);    // store sound name and source its hash map
		return true;
	}

	/* function to create a buffer for the named (without ".wav") sound */
	private int[] initBuffer(String nm) {
		ByteBuffer[] data = new ByteBuffer[1];  // create arrays to hold WAV file info
		int[] format = new int[1], size = new int[1], freq = new int[1], loop = new int[1];

		String fnm = nm + ".wav";
		try {                               // load WAV file into data array first
			ALut.alutLoadWAVFile(fnm, format, data, size, freq, loop);
		} catch (ALException e) {
			System.out.println("Error loading WAV file: " + fnm);
			return null;
		}
		int[] buffer = new int[1];  // create an empty buffer to hold the sound data
		al.alGenBuffers(1, buffer, 0);     // initialize the buffer with loaded data
		if (al.alGetError() != ALConstants.AL_NO_ERROR) {
			System.out.println("Could not create a buffer for " + nm);
			return null;
		}                                   // store data in the buffer
		al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

		return buffer;
	}

	/* function to use the sound buffer to initialize a sound source at (0,0,0)
	 * with no velocity. he sound may play looping, depending on 'toLoop'. */
	private int[] initSource(String nm, int[] buf, boolean toLoop) {
		// create a source (a point in space that emits a sound)
		int[] source = new int[1];
		al.alGenSources(1, source, 0);
		if (al.alGetError() != ALConstants.AL_NO_ERROR) {
			System.out.println("Error creating source for " + nm);
			return null;
		}

		// configure source, positioned at (0,0,0)
		al.alSourcei(source[0], ALConstants.AL_BUFFER, buf[0]);              // bind buffer
		al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
		al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
		al.alSource3f(source[0], ALConstants.AL_POSITION, 0.0f, 0.0f, 0.0f); // position at origin
		al.alSource3i(source[0], ALConstants.AL_VELOCITY, 0, 0, 0);          // no velocity
		if (toLoop)
			al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);     // looping sound
		else
			al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_FALSE);    // not looping

		if (al.alGetError() != ALConstants.AL_NO_ERROR) {
			System.out.println("Error configuring source for " + nm);
			return null;
		}
		return source;
	}

	/* function to move the sound named 'nm' to position at (x,y,z) */
	public boolean setPos(String nm, float x, float y, float z) {
		int[] source = sourcesMap.get(nm);
		if (source == null) {
			System.out.println("No source found for " + nm);
			return false;
		}
		al.alSource3f(source[0], ALConstants.AL_POSITION, x, y, z);
		return true;
	}

	/* function to load the sound named 'nm' to position at (x,y,z)*/
	public boolean load(String nm, float x, float y, float z, boolean toLoop) {
		if (load(nm, toLoop))                      // load the sound
			return setPos(nm, x, y, z);            // position it at (x,y,z)
		else
			return false;
	}

	/* function to play or resume the sound named 'nm' */
	public boolean play(String nm) {
		int[] source = sourcesMap.get(nm);
		if (source == null) {
			System.out.println("No source found for " + nm);
			return false;
		}

		al.alSourcePlay(source[0]);
		return true;
	}

	/* function to stop the playing of the sound named 'nm' */
	public boolean stop(String nm) {
		int[] source = sourcesMap.get(nm);
		if (source == null) {
			System.out.println("No source found for " + nm);
			return false;
		}
	
		al.alSourceStop(source[0]);
		return true;
	}

	/* function to pause the playing of the sound named 'nm' */
	public boolean pause(String nm)
	{
		int[] source = sourcesMap.get(nm);
		if (source == null) {
			System.out.println("No source found for " + nm);
			return false;
		}
	
		al.alSourcePause(source[0]);
		return true;
	} // end of pause()

// --------------------- listener methods ---------------------

	/* function to move the listener by (x,z) step (y is always 0, on the floor) */
	public void moveListener(float xStep, float zStep) {
		float x = xLis + xStep;
		float z = zLis + zStep;
		setListenerPos(x, z);
	}

	/* function to position the listener at (xNew,zNew) (y is always 0, on the floor) */
	public void setListenerPos(float xNew, float zNew) {
		float xOffset = xNew - xLis;
		float zOffset = zNew - zLis;

		xLis = xNew;
		zLis = zNew;
		al.alListener3f(ALConstants.AL_POSITION, xLis, yLis, zLis);


		oriLis[0] += xOffset;
		oriLis[2] += zOffset;
		al.alListenerfv(ALConstants.AL_ORIENTATION, oriLis, 0);
	}

	public float getX() { return xLis; }

	public float getZ() { return zLis; }

	/* function to turn the listener anti-clockwise by degrees */
	public void turnListener(int degrees) {
		setListenerOri(angleLis + degrees);
	}

	public int getAngle() { return angleLis; }


	public void setListenerOri(int ang) {
		angleLis = ang;

		double angle = Math.toRadians(angleLis);
		float xLen = -1.0f * (float) Math.sin(angle);
		float zLen = -1.0f * (float) Math.cos(angle);

		oriLis[0] = xLis + xLen;        // face in the (xLen, zLen) direction by adding
		oriLis[2] = zLis + zLen;        // the values to the listener position
		al.alListenerfv(ALConstants.AL_ORIENTATION, oriLis, 0);
	}

// -------------------------- finish --------------------------

	/* function to delete all the source and buffers in the hash maps */
	public void cleanUp() {
		Set<String> keys = sourcesMap.keySet();
		Iterator<String> iter = keys.iterator();

		String nm;
		int[] buffer, source;
		while (iter.hasNext()) {
			nm = iter.next();

			source = sourcesMap.get(nm);
			System.out.println("Stopping " + nm);
			al.alSourceStop(source[0]);
			al.alDeleteSources(1, source, 0);

			buffer = buffersMap.get(nm);
			al.alDeleteBuffers(1, buffer, 0);
		}

		ALut.alutExit();
	}
}