//class for the MorphBehavior as the import doesnt work on javax Morph
package Main;

import java.util.*;
import javax.media.j3d.*;

public class MorphBehavior extends Behavior {
	   private Morph targetMorph;
	           private Alpha alpha;
	          private double[] weights = {0,0,0,0,0};
	           private WakeupCondition trigger = new WakeupOnElapsedFrames(0);
	         
	          //create MorphBeavior
	          MorphBehavior(Morph targetMorph, Alpha alpha){
	                  this.targetMorph = targetMorph;
	                  this.alpha = alpha;
	          }
	          
	          public void initialize(){
	                 //set initial wakeup condition
	                  this.wakeupOn(trigger);
	          }
	          
	          public void processStimulus(Enumeration criteria){
	                  
	                 weights[0] = 0; weights[1] = 0; 
	                 
	                  float alphaValue = 2f*alpha.value();    //get alpha
	                  int alphaIndex = (int) alphaValue;      //which Geom object
	                  weights[alphaIndex] = (double) alphaValue - (double) alphaIndex;
	                  if(alphaIndex < 1){
	                         weights[alphaIndex + 1] = 1.0 - weights[alphaIndex];
	                 }else{
	                         weights[0] = 1.0 - weights[alphaIndex];
	                 }
	                 targetMorph.setWeights(weights);
	                 
	                 this.wakeupOn(trigger);         //set next wakeup condition
	        }

}
