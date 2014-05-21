/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.io;

import android.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter.UnknownFieldException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.catrobat.catroid.content.bricks.Brick.BrickField;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeGhostEffectByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XStreamToSupportCatrobatLanguageVersion091AndBefore extends XStream {

	private static final String TAG = XStreamToSupportCatrobatLanguageVersion091AndBefore.class.getSimpleName();

	private HashMap<String, BrickInfo> brickInfoMap;
	private Transformer serializer;

	public XStreamToSupportCatrobatLanguageVersion091AndBefore(PureJavaReflectionProvider reflectionProvider) {
		super(reflectionProvider);
	}

	public Object getProjectFromXML(File file) {
		Object parsedObject = null;
		try {
			parsedObject = super.fromXML(file);
		} catch (UnknownFieldException exception) {
			Log.e(TAG, "Unknown field found" + exception.getLocalizedMessage());
			modifyXMLToSupportUnknownFields(file);
			parsedObject = super.fromXML(file);
		}
		return parsedObject;
	}

	private void initSerializer() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		if (serializer == null) {
			serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		}
	}

	private void initializeBrickInfoMap() {
		if (brickInfoMap != null) {
			return;
		}

		brickInfoMap = new HashMap<String, BrickInfo>();

		BrickInfo brickInfo = new BrickInfo(ChangeBrightnessByNBrick.class.getName());
		brickInfo.addBrickFieldToMap("changeBrightness", BrickField.BRIGHTNESS_CHANGE);
		brickInfoMap.put("changeBrightnessByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeGhostEffectByNBrick.class.getName());
		brickInfo.addBrickFieldToMap("changeGhostEffect", BrickField.TRANSPARENCY_CHANGE);
		brickInfoMap.put("changeGhostEffectByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeSizeByNBrick.class.getName());
		brickInfo.addBrickFieldToMap("size", BrickField.SIZE_CHANGE);
		brickInfoMap.put("changeSizeByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeVariableBrick.class.getName());
		brickInfo.addBrickFieldToMap("variableFormula", BrickField.VARIABLE_CHANGE);
		brickInfoMap.put("changeVariableBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeVolumeByNBrick.class.getName());
		brickInfo.addBrickFieldToMap("volume", BrickField.VOLUME_CHANGE);
		brickInfoMap.put("changeVolumeByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeXByNBrick.class.getName());
		brickInfo.addBrickFieldToMap("xMovement", BrickField.X_POSITION_CHANGE);
		brickInfoMap.put("changeXByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeYByNBrick.class.getName());
		brickInfo.addBrickFieldToMap("yMovement", BrickField.Y_POSITION_CHANGE);
		brickInfoMap.put("changeYByNBrick", brickInfo);

		brickInfo = new BrickInfo(GlideToBrick.class.getName());
		brickInfo.addBrickFieldToMap("xDestination", BrickField.X_DESTINATION);
		brickInfo.addBrickFieldToMap("yDestination", BrickField.Y_DESTINATION);
		brickInfo.addBrickFieldToMap("durationInSeconds", BrickField.DURATION_IN_SECONDS);
		brickInfoMap.put("glideToBrick", brickInfo);

		brickInfo = new BrickInfo(GoNStepsBackBrick.class.getName());
		brickInfo.addBrickFieldToMap("steps", BrickField.STEPS);
		brickInfoMap.put("goNStepsBackBrick", brickInfo);

		brickInfo = new BrickInfo(IfLogicBeginBrick.class.getName());
		brickInfo.addBrickFieldToMap("ifCondition", BrickField.IF_CONDITION);
		brickInfoMap.put("ifLogicBeginBrick", brickInfo);

		brickInfo = new BrickInfo(LegoNxtMotorActionBrick.class.getName());
		brickInfo.addBrickFieldToMap("speed", BrickField.LEGO_NXT_SPEED);
		brickInfoMap.put("legoNxtMotorActionBrick", brickInfo);

		brickInfo = new BrickInfo(LegoNxtMotorTurnAngleBrick.class.getName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.LEGO_NXT_DEGREES);
		brickInfoMap.put("legoNxtMotorTurnAngleBrick", brickInfo);

		brickInfo = new BrickInfo(LegoNxtPlayToneBrick.class.getName());
		brickInfo.addBrickFieldToMap("frequency", BrickField.LEGO_NXT_FREQUENCY);
		brickInfo.addBrickFieldToMap("durationInSeconds", BrickField.LEGO_NXT_DURATION_IN_SECONDS);
		brickInfoMap.put("legoNxtPlayToneBrick", brickInfo);

		brickInfo = new BrickInfo(MoveNStepsBrick.class.getName());
		brickInfo.addBrickFieldToMap("steps", BrickField.STEPS);
		brickInfoMap.put("moveNStepsBrick", brickInfo);

		brickInfo = new BrickInfo(PlaceAtBrick.class.getName());
		brickInfo.addBrickFieldToMap("xPosition", BrickField.X_POSITION);
		brickInfo.addBrickFieldToMap("yPosition", BrickField.Y_POSITION);
		brickInfoMap.put("placeAtBrick", brickInfo);

		brickInfo = new BrickInfo(PointInDirectionBrick.class.getName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.DEGREES);
		brickInfoMap.put("pointInDirectionBrick", brickInfo);

		brickInfo = new BrickInfo(RepeatBrick.class.getName());
		brickInfo.addBrickFieldToMap("timesToRepeat", BrickField.TIMES_TO_REPEAT);
		brickInfoMap.put("repeatBrick", brickInfo);

		brickInfo = new BrickInfo(SetBrightnessBrick.class.getName());
		brickInfo.addBrickFieldToMap("brightness", BrickField.BRIGHTNESS);
		brickInfoMap.put("setBrightnessBrick", brickInfo);

		brickInfo = new BrickInfo(SetGhostEffectBrick.class.getName());
		brickInfo.addBrickFieldToMap("transparency", BrickField.TRANSPARENCY);
		brickInfoMap.put("setGhostEffectBrick", brickInfo);

		brickInfo = new BrickInfo(SetGhostEffectBrick.class.getName());
		brickInfo.addBrickFieldToMap("transparency", BrickField.TRANSPARENCY);
		brickInfoMap.put("setGhostEffectBrick", brickInfo);

		brickInfo = new BrickInfo(SetSizeToBrick.class.getName());
		brickInfo.addBrickFieldToMap("size", BrickField.SIZE);
		brickInfoMap.put("setSizeToBrick", brickInfo);

		brickInfo = new BrickInfo(SetSizeToBrick.class.getName());
		brickInfo.addBrickFieldToMap("size", BrickField.SIZE);
		brickInfoMap.put("setSizeToBrick", brickInfo);

		brickInfo = new BrickInfo(SetVariableBrick.class.getName());
		brickInfo.addBrickFieldToMap("variableFormula", BrickField.VARIABLE);
		brickInfoMap.put("setVariableBrick", brickInfo);

		brickInfo = new BrickInfo(SetVolumeToBrick.class.getName());
		brickInfo.addBrickFieldToMap("volume", BrickField.VOLUME);
		brickInfoMap.put("setVolumeToBrick", brickInfo);

		brickInfo = new BrickInfo(SetXBrick.class.getName());
		brickInfo.addBrickFieldToMap("xPosition", BrickField.X_POSITION);
		brickInfoMap.put("setXBrick", brickInfo);

		brickInfo = new BrickInfo(SetYBrick.class.getName());
		brickInfo.addBrickFieldToMap("yPosition", BrickField.Y_POSITION);
		brickInfoMap.put("setYBrick", brickInfo);

		brickInfo = new BrickInfo(TurnLeftBrick.class.getName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.TURN_LEFT_DEGREES);
		brickInfoMap.put("turnLeftBrick", brickInfo);

		brickInfo = new BrickInfo(TurnRightBrick.class.getName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.TURN_RIGHT_DEGREES);
		brickInfoMap.put("turnRightBrick", brickInfo);

		brickInfo = new BrickInfo(WaitBrick.class.getName());
		brickInfo.addBrickFieldToMap("timeToWaitInSeconds", BrickField.TIME_TO_WAIT_IN_SECONDS);
		brickInfoMap.put("waitBrick", brickInfo);
	}

	private void modifyXMLToSupportUnknownFields(File file) {
		initializeBrickInfoMap();

		try {
			initSerializer();

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			normalizeWhiteSpaces(doc);

			NodeList brickListNodes = doc.getElementsByTagName("brickList");
			for (int brickListIndex = 0; brickListIndex < brickListNodes.getLength(); brickListIndex++) {
				Node brickListNode = brickListNodes.item(brickListIndex);

				NodeList brickNodes = brickListNode.getChildNodes();
				for (int brickNodeIndex = 0; brickNodeIndex < brickNodes.getLength(); brickNodeIndex++) {
					Node brickNode = brickNodes.item(brickNodeIndex);

					String newBrickContent = createNewBrickIfModified(brickNode);
					if (newBrickContent != null) {
						Document newBrickDoc = docBuilder.parse(new ByteArrayInputStream(newBrickContent.getBytes()));
						normalizeWhiteSpaces(newBrickDoc);
						Node newBrickRootNode = newBrickDoc.getDocumentElement();

						// add other child brick nodes to newly created brick
						NodeList childNodes = brickNode.getChildNodes();
						for (int index = 0; index < childNodes.getLength(); index++) {
							Node node = childNodes.item(index);
							Node childNode = node.getFirstChild();
							if (childNode != null && childNode.getNodeName().equals("formulaTree")) {
								continue;
							} else {
								Node otherNode = newBrickDoc.adoptNode(node);
								newBrickRootNode.appendChild(otherNode);
							}
						}

						Node replacementNode = doc.adoptNode(newBrickRootNode);
						brickListNode.replaceChild(replacementNode, brickNode);
					}
				}
			}

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file.getPath());
			serializer.transform(source, result);

		} catch (IllegalArgumentException exception) {
			Log.e(TAG, "Unknown brick", exception);
		} catch (ParserConfigurationException exception) {
			Log.e(TAG, "XML parsing failed", exception);
		} catch (XPathExpressionException exception) {
			Log.e(TAG, "Failed white space evaulation", exception);
		} catch (TransformerConfigurationException exception) {
			Log.e(TAG, "Failed white space evaulation", exception);
		} catch (TransformerException exception) {
			Log.e(TAG, "Failed white space evaulation", exception);
		} catch (SAXException exception) {
			Log.e(TAG, "SAX exception", exception);
		} catch (IOException exception) {
			Log.e(TAG, "IO exception", exception);
		}

	}

	private void normalizeWhiteSpaces(Document document) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList emptyTextNodeList = (NodeList) xPath.evaluate("//text()[normalize-space(.)='']", document,
				XPathConstants.NODESET);

		for (int index = 0; index < emptyTextNodeList.getLength(); ++index) {
			Node emptyTextNode = emptyTextNodeList.item(index);
			emptyTextNode.getParentNode().removeChild(emptyTextNode);
		}
	}

	private String createNewBrickIfModified(Node brickNode) {
		String brickXML = null;
		String brickNodeName = brickNode.getNodeName();
		HashMap<String, Formula> formulas = extractFormulas(brickNode);

		BrickInfo brickInfo = brickInfoMap.get(brickNodeName);
		if (brickInfo != null) {
			try {
				Class<?> brickClass = Class.forName(brickInfo.getBrickClassName());
				BrickBaseType brickBase = (BrickBaseType) brickClass.newInstance();
				replaceFormulaMap(brickBase, brickInfo, formulas);
				brickXML = toXML(brickBase);
			} catch (ClassNotFoundException exception) {
				Log.e(TAG, "Brick class not found", exception);
			} catch (InstantiationException exception) {
				Log.e(TAG, "Instantiation error", exception);
			} catch (IllegalAccessException exception) {
				Log.e(TAG, "Illegal access to class", exception);
			}
		}

		return brickXML;
	}

	private HashMap<String, Formula> extractFormulas(Node brickNode) {
		HashMap<String, Formula> formulas = new HashMap<String, Formula>();
		NodeList childNodes = brickNode.getChildNodes();
		for (int index = 0; index < childNodes.getLength(); index++) {
			Node node = childNodes.item(index);
			Node childNode = node.getFirstChild();
			if (childNode != null && childNode.getNodeName().equals("formulaTree")) {
				try {
					StringWriter writer = new StringWriter();
					DOMSource formulaElementSource = new DOMSource(childNode);
					serializer.transform(formulaElementSource, new StreamResult(writer));
					String xmlString = writer.toString();

					FormulaElement formulaElement = (FormulaElement) fromXML(xmlString);
					Formula formula = new Formula(formulaElement);
					formulas.put(node.getNodeName(), formula);
				} catch (TransformerConfigurationException exception) {
					Log.e(TAG, "Serializer configuration error", exception);
				} catch (TransformerFactoryConfigurationError exception) {
					Log.e(TAG, "Serializer factory configuration error", exception);
				} catch (TransformerException exception) {
					Log.e(TAG, "Serializer error", exception);
				}
			}
		}
		return formulas;
	}

	private void replaceFormulaMap(BrickBaseType baseBrick, BrickInfo brickInfo, HashMap<String, Formula> formulaMap) {
		Iterator<String> itKey = formulaMap.keySet().iterator();
		while (itKey.hasNext()) {
			String oldFormulaNode = itKey.next();
			Formula formula = formulaMap.get(oldFormulaNode);
			BrickField brickField = brickInfo.getBrickFieldForOldFieldName(oldFormulaNode);

			if (formula == null) {
				throw new IllegalArgumentException(oldFormulaNode + " node not found");
			} else if (brickField == null) {
				throw new IllegalArgumentException("Brick field for " + oldFormulaNode + " not found");
			} else {
				baseBrick.setFormulaWithBrickField(brickField, formula);
			}
		}
	}

	private class BrickInfo {
		private String brickClassName;
		private HashMap<String, BrickField> brickFieldMap;

		BrickInfo(String brickClassName) {
			this.brickClassName = brickClassName;
		}

		void addBrickFieldToMap(String oldFiledName, BrickField brickField) {
			if (brickFieldMap == null) {
				brickFieldMap = new HashMap<String, BrickField>();
			}
			brickFieldMap.put(oldFiledName, brickField);
		}

		BrickField getBrickFieldForOldFieldName(String oldFiledName) {
			if (brickFieldMap != null) {
				return brickFieldMap.get(oldFiledName);
			}
			return null;
		}

		String getBrickClassName() {
			return brickClassName;
		}
	}

}
