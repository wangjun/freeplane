/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2018 dimitry
 *
 *  This file author is dimitry
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.plugin.collaboration.client.event.children;

import org.freeplane.collaboration.event.children.NodeRemoved;
import org.freeplane.features.map.MapModel;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.map.mindmapmode.SingleNodeStructureManipulator;
import org.freeplane.plugin.collaboration.client.event.UpdateProcessor;

/**
 * @author Dimitry Polivaev
 * Jan 5, 2018
 */
public class NodeRemovedProcessor implements UpdateProcessor<NodeRemoved>{
	private SingleNodeStructureManipulator manipulator;

	public NodeRemovedProcessor(SingleNodeStructureManipulator manipulator) {
		this.manipulator = manipulator;
	}

	@Override
	public void onUpdate(MapModel map, NodeRemoved event) {
		NodeModel child = map.getNodeForID(event.nodeId());
		manipulator.deleteNode(child);
	}

	@Override
	public Class<NodeRemoved> eventClass() {
		return NodeRemoved.class;
	}
}