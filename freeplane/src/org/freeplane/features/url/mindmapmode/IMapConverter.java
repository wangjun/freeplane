package org.freeplane.features.url.mindmapmode;

import org.freeplane.features.map.NodeModel;

//DOCEAR
public interface IMapConverter {
	public void convert(NodeModel root) throws MapConversionException;
}
