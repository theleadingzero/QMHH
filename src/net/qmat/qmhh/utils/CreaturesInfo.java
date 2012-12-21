package net.qmat.qmhh.utils;

import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.models.creatures.CreatureBase;
import net.qmat.qmhh.models.creatures.CreaturesModel;

public class CreaturesInfo {
	public int nrCreatures;
	public int nrEvs[] = {0, 0, 0};
	
	public CreaturesInfo() {
		CreaturesModel cm = Models.getCreaturesModel();
		nrCreatures = cm.creatures.size();
		for(CreatureBase creature : cm.creatures) {
			nrEvs[creature.stage]++;
		}
	}
}
