package at.tugraz.ist.catroid.utils.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;

public class Parser {
	private DocumentBuilder builder;
	private Document doc;

	final static String STAGE = "stage";
	final static String SPRITE = "sprite";
	final static String TYPE = "type";
	final static String ID = "id";
	final static String PATH = "path";
	final static String PATH_THUMB = "path_thumb";
	final static String NAME = "name";

	final static String PROJECT = "project";
	final static String VERSION_CODE = "versionCode";
	final static String VERSION_NAME = "versionName";
	final static String BRICK = "brick";
	final static String SOUND = "sound";
	final static String IMAGE = "image";
	final static String X = "x";
	final static String Y = "y";

	final static String EMPTY_STRING = "";

	public Parser() {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Log.e("PARSER", e.getMessage());
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			Log.e("PARSER", e.getMessage());
			e.printStackTrace();
		}
	}

	public ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> parse(
			InputStream stream, Context context) {
		ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> spritesList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>();
		try {
			doc = builder.parse(stream);
		} catch (Exception e) {
			Log.e("Parser", "A parser error occured");
			e.printStackTrace();
		}
		
		Node project = doc.getElementsByTagName(PROJECT).item(0);
		NamedNodeMap attributes = project.getAttributes();
		
		int versionCode = Integer.parseInt(attributes.getNamedItem(VERSION_CODE).getNodeValue());
		String versionName = attributes.getNamedItem(VERSION_NAME).getNodeValue();
		
		Log.v("at.tugraz.ist.catroid.utils.parser.parse", "Loading Project with version code \"" +
				versionCode + "\" and version name \"" + versionName + "\"");
		// TODO: Add version check here
		
		Node stage = doc.getElementsByTagName(STAGE).item(0);
		NodeList sprites = doc.getElementsByTagName(SPRITE);

		// first add stage with its localized name to the spritesList
		spritesList.add(new Pair<String, ArrayList<HashMap<String, String>>>(
				context.getString(R.string.stage),
				getBricksListFromSprite(stage)));

		// then add all sprites
		for (int j = 0; j < sprites.getLength(); j++) {
			String name = sprites.item(j).getAttributes().getNamedItem(NAME)
					.getNodeValue();
			spritesList
					.add(new Pair<String, ArrayList<HashMap<String, String>>>(
							name, getBricksListFromSprite(sprites.item(j))));
		}
		return spritesList;
	}

	public String toXml(
			ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> spritesMap,
			Context context) {
		ArrayList<ArrayList<HashMap<String, String>>> spriteBrickList = new ArrayList<ArrayList<HashMap<String, String>>>();
		ArrayList<String> spriteNameList = new ArrayList<String>();

		for (int i = 0; i < spritesMap.size(); i++) {
			spriteBrickList.add(spritesMap.get(i).second);
			spriteNameList.add(spritesMap.get(i).first);
		}

		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();

		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo("at.tugraz.ist.catroid", 0);
			serializer.startTag(EMPTY_STRING, PROJECT);
			serializer.attribute(null, "versionCode", String.valueOf(packageInfo.versionCode));
			serializer.attribute(null, "versionName", packageInfo.versionName);
		} catch (Exception e) {
			Log.e("Parser", "An error occured in toXml 1" + e.getMessage());
			e.printStackTrace();
		}

		// first write stage
		if (!spriteNameList.get(0).equals(context.getString(R.string.stage))) {
			Log.e("Parser",
					"Stage seems not to be the first element in the data structure. No valid XML will be created");
			return "";
		}
		try {
			serializer.startTag(EMPTY_STRING, STAGE);
			writeSpriteContentToXml(spriteBrickList.get(0), serializer);
			serializer.endTag(EMPTY_STRING, STAGE);
		} catch (Exception e) {
			Log.e("Parser", "An error occured in creating stage xml structure");
			e.printStackTrace();
		}

		// then all other sprites
		for (int j = 1; j < spriteBrickList.size(); j++) {
			ArrayList<HashMap<String, String>> sprite = spriteBrickList.get(j);
			try {
				serializer.startTag(EMPTY_STRING, SPRITE);
				serializer.attribute(EMPTY_STRING, NAME, spriteNameList.get(j));
				writeSpriteContentToXml(sprite, serializer);
				serializer.endTag(EMPTY_STRING, SPRITE);
			} catch (Exception e) {
				Log.e("Parser", "An error occured in toXml 2 " + e.getMessage());
				e.printStackTrace();
			}
		}

		try {
			serializer.endTag(EMPTY_STRING, PROJECT);
			serializer.endDocument();
		} catch (Exception e) {
			Log.e("Parser", "An error occured in toXml 3" + e.getMessage());
			e.printStackTrace();
		}

		return writer.toString();
	}

	private HashMap<String, String> getBrickMap(String id, String name,
			String value, String value1, int type) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_ID, id);
		map.put(BrickDefine.BRICK_TYPE, String.valueOf(type));
		map.put(BrickDefine.BRICK_NAME, name);
		map.put(BrickDefine.BRICK_VALUE, value);
		map.put(BrickDefine.BRICK_VALUE_1, value1);

		return map;
	}

	private ArrayList<HashMap<String, String>> getBricksListFromSprite(
			Node spriteNode) {
		NodeList bricks = spriteNode.getChildNodes();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < bricks.getLength(); i++) {
			int brickType = Integer.parseInt(bricks.item(i).getAttributes()
					.getNamedItem(TYPE).getNodeValue());
			String value = EMPTY_STRING;
			String value1 = EMPTY_STRING;
			String title = EMPTY_STRING;
			String id = bricks.item(i).getAttributes().getNamedItem(ID)
					.getNodeValue();
			switch (brickType) {
			case BrickDefine.SET_BACKGROUND:
			case BrickDefine.SET_COSTUME:
				value = bricks.item(i).getFirstChild().getAttributes()
						.getNamedItem(PATH).getNodeValue();
				value1 = bricks.item(i).getFirstChild().getAttributes()
						.getNamedItem(PATH_THUMB).getNodeValue();
				break;
			case BrickDefine.PLAY_SOUND:
				value = bricks.item(i).getFirstChild().getAttributes()
						.getNamedItem(PATH).getNodeValue();
				title = bricks.item(i).getFirstChild().getAttributes()
						.getNamedItem(NAME).getNodeValue();
				break;
			case BrickDefine.WAIT:
				value = bricks.item(i).getFirstChild().getNodeValue();
				break;
			case BrickDefine.GO_TO:
				value = bricks.item(i).getFirstChild().getFirstChild()
						.getNodeValue();
				value1 = bricks.item(i).getLastChild().getFirstChild()
						.getNodeValue();
				break;
			case BrickDefine.SCALE_COSTUME:
				value = bricks.item(i).getFirstChild().getNodeValue();
				break;
			}
			HashMap<String, String> map = getBrickMap(id, title, value, value1,
					brickType);
			list.add(map);
		}
		return list;

	}

	private void writeSpriteContentToXml(
			ArrayList<HashMap<String, String>> sprite, XmlSerializer serializer)
			throws IOException {
		for (int i = 0; i < sprite.size(); i++) {
			HashMap<String, String> brick = sprite.get(i);
			serializer.startTag(EMPTY_STRING, BRICK);
			serializer.attribute(EMPTY_STRING, ID,
					brick.get(BrickDefine.BRICK_ID));
			switch (Integer.parseInt(brick.get(BrickDefine.BRICK_TYPE))) {
			case BrickDefine.SET_BACKGROUND:
				serializer.attribute(EMPTY_STRING, TYPE,
						Integer.toString(BrickDefine.SET_BACKGROUND));
				serializer.startTag(EMPTY_STRING, IMAGE);
				serializer.attribute(EMPTY_STRING, PATH,
						brick.get(BrickDefine.BRICK_VALUE));
				serializer.attribute(EMPTY_STRING, PATH_THUMB,
						brick.get(BrickDefine.BRICK_VALUE_1));
				serializer.endTag(EMPTY_STRING, IMAGE);
				break;
			case BrickDefine.PLAY_SOUND:
				serializer.attribute(EMPTY_STRING, TYPE,
						Integer.toString(BrickDefine.PLAY_SOUND));
				serializer.startTag(EMPTY_STRING, SOUND);
				serializer.attribute(EMPTY_STRING, PATH,
						brick.get(BrickDefine.BRICK_VALUE));
				serializer.attribute(EMPTY_STRING, NAME,
						brick.get(BrickDefine.BRICK_NAME));
				serializer.endTag(EMPTY_STRING, SOUND);
				break;
			case BrickDefine.WAIT:
				serializer.attribute(EMPTY_STRING, TYPE,
						Integer.toString(BrickDefine.WAIT));
				serializer.text(brick.get(BrickDefine.BRICK_VALUE));
				break;
			case BrickDefine.HIDE:
				serializer.attribute(EMPTY_STRING, TYPE,
						Integer.toString(BrickDefine.HIDE));
				break;
			case BrickDefine.SHOW:
				serializer.attribute(EMPTY_STRING, TYPE,
						Integer.toString(BrickDefine.SHOW));
				break;
			case BrickDefine.SET_COSTUME:
				serializer.attribute(EMPTY_STRING, TYPE,
						Integer.toString(BrickDefine.SET_COSTUME));
				serializer.startTag(EMPTY_STRING, IMAGE);
				serializer.attribute(EMPTY_STRING, PATH,
						brick.get(BrickDefine.BRICK_VALUE));
				serializer.attribute(EMPTY_STRING, PATH_THUMB,
						brick.get(BrickDefine.BRICK_VALUE_1));
				serializer.endTag(EMPTY_STRING, IMAGE);
				break;
			case BrickDefine.GO_TO:
				serializer.attribute(EMPTY_STRING, TYPE,
						Integer.toString(BrickDefine.GO_TO));
				serializer.startTag(EMPTY_STRING, X);
				serializer.text(brick.get(BrickDefine.BRICK_VALUE));
				serializer.endTag(EMPTY_STRING, X);
				serializer.startTag(EMPTY_STRING, Y);
				serializer.text(brick.get(BrickDefine.BRICK_VALUE_1));
				serializer.endTag(EMPTY_STRING, Y);
				break;
			case BrickDefine.SCALE_COSTUME:
				serializer.attribute(EMPTY_STRING, TYPE,
						Integer.toString(BrickDefine.SCALE_COSTUME));
				serializer.text(brick.get(BrickDefine.BRICK_VALUE));
			}
			serializer.endTag(EMPTY_STRING, BRICK);
		}

	}
}
