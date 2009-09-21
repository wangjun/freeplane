/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
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
package org.freeplane.features.common.addins.mapstyle;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessControlException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.freeplane.core.extension.IExtension;
import org.freeplane.core.io.MapReader;
import org.freeplane.core.io.ReadManager;
import org.freeplane.core.io.MapWriter.Mode;
import org.freeplane.core.modecontroller.ModeController;
import org.freeplane.core.model.MapModel;
import org.freeplane.core.model.NodeModel;
import org.freeplane.core.resources.NamedObject;
import org.freeplane.core.resources.ResourceBundles;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.LogTool;
import org.freeplane.features.mindmapmode.MMapModel;
import org.freeplane.features.mindmapmode.UMapModel;
import org.freeplane.n3.nanoxml.XMLException;
import org.freeplane.n3.nanoxml.XMLParseException;

/**
 * @author Dimitry Polivaev
 * Mar 12, 2009
 */
public class MapStyleModel implements IExtension {
	public static final String DEFAULT_STYLE = "OptionPanel.default";
	private static final String STYLES = "styles";
	private Map<Object, NodeModel> styleNodes; 
	private static boolean loadingStyleMap = false;
	private MapModel styleMap;
	public static MapStyleModel getExtension(final MapModel map) {
		return MapStyleModel.getExtension(map.getRootNode());
	}

	public MapModel getStyleMap() {
    	return styleMap;
    }

	public static MapStyleModel getExtension(final NodeModel node) {
		return (MapStyleModel) node.getExtension(MapStyleModel.class);
	}

	private Color backgroundColor;

	public MapStyleModel() {
	}

	void createStyleMap(ModeController modeController, String styleMapStr) {
	    if(loadingStyleMap){
			styleMap = null;
			styleNodes = null;
			return;
		}
		styleNodes = new HashMap<Object, NodeModel>();
		styleMap = new UMapModel(null, modeController){

			@Override
            public String getTitle() {
	            return ResourceBundles.getText(STYLES);
            }
			
		};
		final MapReader mapReader = modeController.getMapController().getMapReader();
		final NodeModel root;
		try {
			if(styleMapStr != null){
				final Reader styleReader; 
				styleReader = new StringReader(styleMapStr);
				root = mapReader.createNodeTreeFromXml(styleMap, styleReader, Mode.FILE);
			}
			else{
				loadingStyleMap = true;
				try{
				root = load(ResourceController.getResourceController().getResource("/styles/default.mm"),
					mapReader,styleMap);
				}
				finally{
					loadingStyleMap = false;
				}
			}
			styleMap.setRoot(root);
			MapStyleModel extension = getExtension(styleMap);
			if(extension == null){
				loadingStyleMap = true;
				try{
					extension = new MapStyleModel();
					styleMap.getRootNode().addExtension(extension);
				}
				finally{
					loadingStyleMap = false;
				}
			}
			extension.styleNodes = styleNodes;
			createNodeStyleMap(root);
			
			styleMap.setReadOnly(false);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void createNodeStyleMap(NodeModel node) {
	    if(node.hasChildren()){
	    	final Enumeration<NodeModel> children = node.children();
	    	while(children.hasMoreElements()){
	    		createNodeStyleMap(children.nextElement());
	    	}
	    	return;
	    }
	    final Object userObject = node.getUserObject();
	    if(userObject instanceof NamedObject){
	    	styleNodes.put(((NamedObject)userObject).getObject(), node);
	    }
	    else{
	    	styleNodes.put(userObject, node);
	    }
    }

	private NodeModel load(final URL url, final MapReader mapReader, final MapModel map) throws Exception {
		InputStreamReader urlStreamReader = null;
		urlStreamReader = new InputStreamReader(url.openStream());
		final NodeModel root = mapReader.createNodeTreeFromXml(map, urlStreamReader, Mode.FILE);
		urlStreamReader.close();
		return root;
	}

	public NodeModel getStyleNode(final Object style){
		return styleNodes.get(style);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(final Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
