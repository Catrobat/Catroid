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
package org.catrobat.catroid.xml.parser;

import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.REFERENCE_ATTRIBUTE;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.SPRITE;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.util.Log;

public class References {

	XPathFactory xPathFactory = XPathFactory.newInstance();
	XPath xPath = xPathFactory.newXPath();
	ObjectCreator objectGetter = new ObjectCreator();

	public static String getReferenceAttribute(Node brickValue) {
		Element brickElement = (Element) brickValue;
		String attributeString = null;
		if (brickValue.getNodeName().equals(SPRITE)) {
			return null;
		}
		if (brickElement != null) {
			NamedNodeMap attributes = brickElement.getAttributes();
			if (attributes != null) {
				Node referenceNode = attributes.getNamedItem(REFERENCE_ATTRIBUTE);
				if (referenceNode != null) {
					attributeString = referenceNode.getTextContent();
				}
			}
		}
		return attributeString;
	}

	public Object resolveReference(Object referencedObject, Node elementWithReference, String referenceString,
			Map<String, Object> referencedObjects, List<ForwardReference> forwardReferences)
			throws XPathExpressionException, IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException, ParseException {
		XPathExpression xPathExpression = xPath.compile(referenceString);
		Log.i("resolveRef", "XPath evaluated for :" + referenceString);
		Element referredElement = (Element) xPathExpression.evaluate(elementWithReference, XPathConstants.NODE);
		if (referredElement == null) {
			throw new ParseException("Element by reference not found");
		}
		String xPathFromRoot = ParserUtil.getElementXPath(referredElement);
		Object object = referencedObjects.get(xPathFromRoot);
		if (object == null) {
			referencedObject = objectGetter.getObjectOfClass(referencedObject.getClass(), "");
			ForwardReference forwardReference = new ForwardReference(referencedObject, xPathFromRoot, null);
			forwardReferences.add(forwardReference);
		} else {
			referencedObject = object;
		}
		return referencedObject;
	}

	public void resolveForwardReferences(Map<String, Object> referencedObjects, List<ForwardReference> forwardReferences)
			throws IllegalArgumentException, IllegalAccessException {
		for (ForwardReference reference : forwardReferences) {
			Field referenceField = reference.getFieldWithReference();
			String referenceString = reference.getReferenceString();
			if (!referencedObjects.containsKey(referenceString)) {
				Log.i("Forward referencing", "reference for " + referenceString + " not found");
			}
			if (referenceField != null) {
				Object parentObject = reference.getObjectWithReferencedField();
				Object valueObject = referencedObjects.get(reference.getReferenceString());
				if (!(valueObject.equals(referenceField.get(parentObject)))) {
					referenceField.set(parentObject, valueObject);
				}
			}
		}

	}
}
