package at.tugraz.ist.catroid.stage;

/**
 * 
 * Everyone who implements this can DrawObjects.
 * 
 * @author Thomas Holzmann
 *
 */
public interface IDraw {
	/**
	 * Processes a DrawObject which should be drawed on the stage.
	 */
	public void draw(DrawObject drawObject);
	
	/**
	 * Clears the drawing auf the stage.
	 */
	public void clear();
}
