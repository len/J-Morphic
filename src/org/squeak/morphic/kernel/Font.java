package org.squeak.morphic.kernel;

public class Font {
	public static final int NORMAL = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;
	
	public String name;
	public int style;
	public float height;
	
	public Font(String name, int style, float height) {
		this.name = name;
		this.style = style;
		this.height = height;
	}
}
