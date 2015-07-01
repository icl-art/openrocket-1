package net.sf.openrocket.gui.adaptors;

import java.util.EventObject;
import java.util.Iterator;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.openrocket.rocketcomponent.Rocket;
import net.sf.openrocket.rocketcomponent.RocketComponent;
import net.sf.openrocket.rocketcomponent.Stage;
import net.sf.openrocket.util.ArrayList;
import net.sf.openrocket.util.ChangeSource;
import net.sf.openrocket.util.Reflection;
import net.sf.openrocket.util.StateChangeListener;

public class StageSelectModel extends AbstractListModel<Stage> implements ComboBoxModel<Stage>, StateChangeListener {
	private static final long serialVersionUID = 1311302134934033684L;
	private static final Logger log = LoggerFactory.getLogger(StageSelectModel.class);

//	protected final String nullText;
	
	protected Stage sourceStage = null;
	protected ArrayList<Stage> displayValues = new ArrayList<Stage>();
	protected Stage selectedStage = null;

	//@SuppressWarnings("unchecked")
	public StageSelectModel( final Stage _stage) {
		this.sourceStage = _stage;
//		this.nullText = nullText;
		
		populateDisplayValues();
		
		stateChanged(null);  // Update current value
		this.sourceStage.addChangeListener(this);
	}
	
	private void populateDisplayValues(){
		Rocket rocket = this.sourceStage.getRocket();
		
		this.displayValues.clear();
		Iterator<RocketComponent> stageIter = rocket.getChildren().iterator();
		while( stageIter.hasNext() ){
			RocketComponent curComp = stageIter.next();
			if( curComp instanceof Stage ){
				Stage curStage = (Stage)curComp;
				if( curStage.equals( this.sourceStage )){
					continue;
				}else{
					displayValues.add( curStage );
				}
			}else{
				throw new IllegalStateException("Rocket has a child which is something other than a Stage: "+curComp.getClass().getCanonicalName()+"(called: "+curComp.getName()+")");
			}
					
		}
		
	}

	@Override
	public int getSize() {
		return this.displayValues.size();
	}

	@Override
	public Stage getElementAt(int index) {
		return this.displayValues.get(index);
	}

	@Override
	public void setSelectedItem(Object newItem) {
		if (newItem == null) {
			// Clear selection - huh?
			return;
		}
		
		if (newItem instanceof String) {
			log.error("setStage to string?  huh? (unexpected value type");
			return;
		}
		
		if( newItem instanceof Stage ){
			Stage nextStage = (Stage) newItem;
	
			if (nextStage.equals(this.selectedStage)){
				return; // i.e. no change
			}
			
			this.selectedStage = nextStage;
			this.sourceStage.setRelativeToStage(nextStage.getStageNumber());		
			return;
		}
		
	}

	@Override
	public Stage getSelectedItem() {
		return this.selectedStage;
		//return "StageSelectModel["+this.selectedIndex+": "+this.displayValues.get(this.selectedIndex).getName()+"]";
	}

	@Override
	public void stateChanged(EventObject eo) {
		if( null == this.sourceStage){
			return;
		}
		Rocket rkt = sourceStage.getRocket();
		int sourceRelToIndex = this.sourceStage.getRelativeToStage();
		int selectedStageIndex = -1;
		if( null != this.selectedStage ){
			selectedStageIndex = this.selectedStage.getStageNumber();
		}
		if ( selectedStageIndex != sourceRelToIndex){
			this.selectedStage = (Stage)rkt.getChild(sourceRelToIndex);
		}
	}
	
	@Override
	public String toString() {
		return "StageSelectModel["+this.selectedStage.getName()+" ("+this.selectedStage.getStageNumber()+")]";
	}



}
