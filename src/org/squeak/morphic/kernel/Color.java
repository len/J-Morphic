package org.squeak.morphic.kernel;

import java.util.Random;

/**
 * A <code>Color</code> represented in 24-bits RGB (8 bits per component).
 * 
 * <p>The class includes handy static constants for <i>web colors</i>,
 * originally X11 color names that have been standardized in SVG 1.0 and
 * are now recognized by most web browsers.</p>
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Web_colors#X11_color_names">Web colors</a>
 */
public class Color {
	public final int red, green, blue;

	/*
	 *  Colors from http://en.wikipedia.org/wiki/Web_colors
	 */
	
	public static Color BLACK = new Color(0, 0, 0);
	public static Color WHITE = new Color(255, 255, 255);

	// White
	public static Color AZURE = new Color(240, 255, 255);
	public static Color WHITE_SMOKE = new Color(245, 245, 245);
	public static Color BEIGE = new Color(245, 245, 220);
	public static Color LINEN = new Color(250, 240, 230);

	// Gray
	public static Color GRAY = new Color(128, 128, 128);
	public static Color SILVER = new Color(192, 192, 192);
	public static Color LIGHT_GRAY = SILVER;
	public static Color DARK_GRAY = new Color(64, 64, 64);

	// Red
	public static Color RED = new Color(255, 0, 0);
	public static Color DARK_RED = new Color(139, 0, 0);
	public static Color CRIMSON = new Color(220, 20, 60);
	public static Color SALMON = new Color(250, 128, 114);
	public static Color LIGHT_SALMON = new Color(255, 160, 122);
	public static Color DARK_SALMON = new Color(233, 150, 122);
	public static Color LIGHT_CORAL = new Color(240, 128, 128);

	// Pink
	public static Color PINK = new Color(255, 192, 203);
	public static Color HOT_PINK = new Color(255, 105, 180);
	public static Color DEEP_PINK = new Color(255, 20, 147);

	// Orange
	public static Color CORAL = new Color(255, 127, 80);
	public static Color TOMATO = new Color(255, 99, 71);
	public static Color ORANGE_RED = new Color(255, 69, 0);
	public static Color DARK_ORANGE = new Color(255, 140, 0);
	public static Color ORANGE = new Color(255, 200, 0);

	// Yellow
	public static Color GOLD = new Color(255, 215, 0);
	public static Color YELLOW = new Color(255, 255, 0);
	public static Color KHAKI = new Color(240, 230, 140);
	public static Color DARK_KHAKI = new Color(189, 183, 107);
	
	// Purple
	public static Color LAVENDER = new Color(230, 230, 250);
	public static Color PLUM = new Color(221, 160, 221);
	public static Color VIOLET = new Color(238, 130, 238);
	public static Color BLUE_VIOLET = new Color(138, 43, 226);
	public static Color DARK_VIOLET = new Color(148, 0, 211);
	public static Color PURPLE = new Color(128, 0, 128);
	public static Color MEDIUM_PURPLE = new Color(147, 112, 219);
	public static Color AMETHYST = new Color(153, 102, 204);
	public static Color INDIGO = new Color(75, 0, 130);
	public static Color ORCHID = new Color(218, 112, 214);
	public static Color MEDIUM_ORCHID = new Color(186, 85, 211);
	public static Color DARK_ORCHID = new Color(153, 50, 204);
	public static Color FUCHSIA = new Color(255, 0, 255);
	public static Color MAGENTA = FUCHSIA;
	public static Color DARK_MAGENTA = new Color(139, 0, 139);

	// Green
	public static Color GREEN_YELLOW = new Color(173, 255, 47);
	public static Color LAWN_GREEN = new Color(124, 252, 0);
	public static Color LIME = new Color(0, 255, 0);
	public static Color LIME_GREEN = new Color(50, 205, 50);
	public static Color PALE_GREEN = new Color(152, 251, 152);
	public static Color LIGHT_GREEN = new Color(144, 238, 144);
	public static Color GREEN = new Color(0, 128, 0);
	public static Color DARK_GREEN = new Color(0, 100, 0);
	public static Color SPRING_GREEN = new Color(0, 255, 127);
	public static Color MEDIUM_SPRING_GREEN = new Color(0, 250, 154);
	public static Color OLIVE = new Color(128, 128, 0);

	// Blue
	public static Color AQUA = new Color(0, 255, 255);
	public static Color CYAN = AQUA;
	public static Color LIGHT_CYAN = new Color(224, 255, 255);
	public static Color DARK_CYAN = new Color(0, 139, 139);
	public static Color TURQUOISE = new Color(64, 224, 208);
	public static Color MEDIUM_TURQUOISE = new Color(72, 209, 204);
	public static Color PALE_TURQUOISE = new Color(175, 238, 238);
	public static Color LIGHT_TURQUOISE = PALE_TURQUOISE; // added by len
	public static Color DARK_TURQUOISE = new Color(0, 206, 209);
	public static Color AQUAMARINE = new Color(127, 255, 212);
	public static Color MEDIUM_AQUAMARINE = new Color(102, 205, 170);
	public static Color NAVY = new Color(0, 0, 128);
	public static Color BLUE = new Color(0, 0, 255);
	public static Color LIGHT_BLUE = new Color(173, 216, 230);
	public static Color MEDIUM_BLUE = new Color(0, 0, 205);
	public static Color DARK_BLUE = new Color(0, 0, 139);
	public static Color DODGER_BLUE = new Color(30, 144, 255);

	// Brown
	public static Color WHEAT = new Color(245, 222, 179);
	public static Color TAN = new Color(210, 180, 140);
	public static Color PERU = new Color(205, 133, 63);
	public static Color CHOCOLATE = new Color(210, 105, 30);
	public static Color SIENNA = new Color(160, 82, 45);
	public static Color BROWN = new Color(165, 42, 42);
	public static Color MAROON = new Color(128, 0, 0);
	

	public static Color random() {
		Random random = new Random();
		return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
	}
	
	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	/**
	* Constructs new Color with the given hue, saturation, and brightness.
	*
	* @param hue the hue value for the HSB color (from 0 to 360)
	* @param saturation the saturation value for the HSB color (from 0 to 1)
	* @param brightness the brightness value for the HSB color (from 0 to 1)
	*
	* @exception IllegalArgumentException if the hue is not between 0 and 360 or
	*    the saturation or brightness is not between 0 and 1
	*/
	public Color(float hue, float saturation, float brightness) {
		// This method was copy&pasted from org.eclipse.swt.graphics.RGB
		if (hue < 0 || hue > 360 || saturation < 0 || saturation > 1 || 
			brightness < 0 || brightness > 1) {
			throw new IllegalArgumentException();
		}
		float r, g, b;
		if (saturation == 0) {
			r = g = b = brightness; 
		} else {
			if (hue == 360) hue = 0;
			hue /= 60;	
			int i = (int)hue;
			float f = hue - i;
			float p = brightness * (1 - saturation);
			float q = brightness * (1 - saturation * f);
			float t = brightness * (1 - saturation * (1 - f));
			switch(i) {
				case 0:
					r = brightness;
					g = t;
					b = p;
					break;
				case 1:
					r = q;
					g = brightness;
					b = p;
					break;
				case 2:
					r = p;
					g = brightness;
					b = t;
					break;
				case 3:
					r = p;
					g = q;
					b = brightness;
					break;
				case 4:
					r = t;
					g = p;
					b = brightness;
					break;
				case 5:
				default:
					r = brightness;
					g = p;
					b = q;
					break;
			}
		}
		red = (int)(r * 255 + 0.5);
		green = (int)(g * 255 + 0.5);
		blue = (int)(b * 255 + 0.5);	
	}

	public boolean equals(Object o) {
		if (!(o instanceof Color))
			return false;
		return ((Color)o).red == red && ((Color)o).green == green && ((Color)o).blue == blue;
	}

	public int hashCode() {
		return red & (green << 8) & (blue << 16);
	}

	public String toString() {
		return "Color ("+red+", "+green+", "+blue+")";
	}
}
