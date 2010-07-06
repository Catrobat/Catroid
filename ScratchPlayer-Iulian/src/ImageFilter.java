import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.Graphics;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

class ImageFilter {
	BufferedImage filteredImage, tempImage;

	void setSourceImage(BufferedImage src) {
		if ((filteredImage == null) ||
				(filteredImage.getWidth(null) != src.getWidth(null)) ||
				(filteredImage.getHeight(null) != src.getHeight(null)))
		{
			if (filteredImage != null) filteredImage.flush();
			filteredImage = new BufferedImage(src.getWidth(null), src.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		}
		filteredImage.getRaster().setDataElements(0, 0, src.getData()); // initialize to src pixels
	}

	BufferedImage makeOutputImage(BufferedImage src) {
		if ((tempImage == null) ||
				(tempImage.getWidth(null) != src.getWidth(null)) ||
				(tempImage.getHeight(null) != src.getHeight(null)))
		{
			if (tempImage != null) tempImage.flush();
			tempImage = new BufferedImage(src.getWidth(null), src.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		}
		return tempImage;
	}

	void applyHueShift(int hueShift) {
		BufferedImage src = filteredImage;
		BufferedImage dst = makeOutputImage(src);
		int pix, outPix, alpha, r, g, b, min, max, hue, s, v;
		int w = src.getWidth();
		int h = src.getHeight();

		for (int x = 0; x < w; x += 1) {
			for (int y = 0; y < h; y += 1) {
				pix = outPix = src.getRGB(x, y);
				alpha = pix & 0xFF000000;
				if (alpha != 0) {
					r = (pix >> 16) & 0xFF;
					g = (pix >> 8) & 0xFF;
					b = pix & 0xFF;
					min = (r < g) ? r : g; if (b < min) min = b;
					max = (r > g) ? r : g; if (b > max) max = b;

					v = (max * 1000) / 255;
					if (v < 150) v = 150;		// force away from black

					s = (max == 0) ? 0 : ((max - min) * 1000) / max;
					if (s < 150) s = 150;		// force a small color change even on grays

					hue = rgb2hue(r, g, b, min, max) + ((180 * hueShift) / 100); // compute new hue
					outPix = alpha | hsv2rgb(hue, s, v);
				}
				dst.setRGB(x, y, outPix);
			}
		}
		tempImage = filteredImage;
		filteredImage = dst;
	}

	void applyBrightnessShift(int brightnessShift) {
		BufferedImage src = filteredImage;
		BufferedImage dst = makeOutputImage(src);
		int pix, outPix, alpha, r, g, b, min, max, hue, s, v;
		int w = src.getWidth();
		int h = src.getHeight();

		for (int x = 0; x < w; x += 1) {
			for (int y = 0; y < h; y += 1) {
				pix = outPix = src.getRGB(x, y);
				alpha = pix & 0xFF000000;
				if (alpha != 0) {
					r = (pix >> 16) & 0xFF;
					g = (pix >> 8) & 0xFF;
					b = pix & 0xFF;
					min = (r < g) ? r : g; if (b < min) min = b;
					max = (r > g) ? r : g; if (b > max) max = b;
					hue = rgb2hue(r, g, b, min, max);
					s = (max == 0) ? 0 : ((max - min) * 1000) / max;
					v = ((max * 1000) / 255) + (10 * brightnessShift);
					if (v > 1000) v = 1000;
					if (v < 0) v = 0;

					outPix = alpha | hsv2rgb(hue, s, v);
				}
				dst.setRGB(x, y, outPix);
			}
		}
		tempImage = filteredImage;
		filteredImage = dst;
	}

	int rgb2hue(int r, int g, int b, int min, int max) {
		int span = max - min;
		if (span == 0) return 0;
		if (r == max) return (60 * (g - b)) / span;
		if (g == max) return 120 + ((60 * (b - r)) / span);
		return 240 + ((60 * (r - g)) / span);
	}

	int hsv2rgb(int rawHue, int saturation, int brightness) {
		int hue = rawHue % 360;
		if (hue < 0) hue = hue + 360;
		int hI = hue / 60;		// integer part of hue [0..5]
		int hF = hue % 60;		// fractional part of hue [0..59]
		int p = ((1000 - saturation) * brightness) / 3922;
		int q = ((1000 - ((saturation * hF) / 60)) * brightness) / 3922;
		int t = ((1000 - ((saturation * (60 - hF)) / 60)) * brightness) / 3922;
		int v = (brightness * 1000) / 3922;

		switch (hI) {
			case 0: return (v << 16) | (t << 8) | p;
			case 1: return (q << 16) | (v << 8) | p;
			case 2: return (p << 16) | (v << 8) | t;
			case 3: return (p << 16) | (q << 8) | v;
			case 4: return (t << 16) | (p << 8) | v;
			case 5: return (v << 16) | (p << 8) | q;
		}
		return 0;
	}

	void applyFisheye(double fisheye) {
		BufferedImage src = filteredImage;
		BufferedImage dst = makeOutputImage(src);
		int w = src.getWidth();
		int h = src.getHeight();
		double centerX = w / 2;
		double centerY = h / 2;
		double scaledPower = (fisheye + 100.0) / 100.0;
		double dx, dy, r, ang, srcX, srcY;

		for (int x = 0; x < w; x += 1) {
			for (int y = 0; y < h; y += 1) {
				dx = (x - centerX) / centerX;
				dy = (y - centerY) / centerY;
				r = Math.pow((Math.sqrt((dx * dx) + (dy * dy))), scaledPower);
				if (r <= 1.0) {
					ang = Math.atan2(dy, dx);
					srcX = centerX + (r * Math.cos(ang) * centerX);
					srcY = centerY + (r * Math.sin(ang) * centerY);
				} else {
					srcX = x;
					srcY = y;
				}
				dst.setRGB(x, y, interpolate(src, srcX, srcY));
			}
		}
		tempImage = filteredImage;
		filteredImage = dst;
	}

	int interpolate(BufferedImage img, double x, double y) {
		int rndX = (int) Math.round(x);
		if (rndX < 0) rndX = 0;
		if (rndX >= img.getWidth(null)) rndX = img.getWidth(null) - 1;
		int rndY = (int) Math.round(y);
		if (rndY < 0) rndY = 0;
		if (rndY >= img.getHeight(null)) rndY = img.getHeight(null) - 1;
		return img.getRGB(rndX, rndY);
	}

	void applyWhirl(double whirl) {
		BufferedImage src = filteredImage;
		BufferedImage dst = makeOutputImage(src);
		double radius, radiusSquared, scaleX, scaleY, dx, dy, d, factor, ang, sina, cosa, srcX, srcY;
		double whirlRadians = Math.toRadians(-whirl);
		int w = src.getWidth();
		int h = src.getHeight();
		double centerX = w / 2;
		double centerY = h / 2;

		if (centerX < centerY) {
			radius = centerX;
			scaleX = centerY / centerX;
			scaleY = 1.0;
		} else {
			radius = centerY;
			scaleX = 1.0;
			if (centerY < centerX) {
				scaleY = centerX / centerY;
			} else {
				scaleY = 1.0;
			}
		}
		radiusSquared = radius * radius;
		for (int x = 0; x < w; x += 1) {
			for (int y = 0; y < h; y += 1) {
				dx = scaleX * (x - centerX);
				dy = scaleY * (y - centerY);
				d = (dx * dx) + (dy * dy);
				if (d < radiusSquared) {
					factor = 1.0 - ((Math.sqrt(d)) / radius);
					ang = whirlRadians * (factor * factor);
					sina = Math.sin(ang);
					cosa = Math.cos(ang);
					srcX = (((cosa * dx) - (sina * dy)) / scaleX) + centerX;
					srcY = (((sina * dx) + (cosa * dy)) / scaleY) + centerY;
					dst.setRGB(x, y, src.getRGB((int) srcX, (int) srcY));
				} else {
					dst.setRGB(x, y, src.getRGB(x, y));
				}
			}
		}
		tempImage = filteredImage;
		filteredImage = dst;
	}

	void applyMosaic(double mosaic) {
		BufferedImage src = filteredImage;
		int factor = (int)(Math.abs(mosaic) + 10) / 10;
		factor = Math.min(factor, Math.min(src.getWidth(null), src.getHeight(null)) - 1);
		if (factor <= 1) return;

		// scale down
		AffineTransform transform = AffineTransform.getScaleInstance(1.0/factor, 1.0/factor);
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		BufferedImage small = op.filter(src, null);

		// make tiled image w/ integral number of copies
		int w = factor * small.getWidth(null);
		int h = factor * small.getHeight(null);
		BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		tmp.getRaster();
		Graphics g = tmp.getGraphics();
		int dx = small.getWidth(null);
		int dy = small.getHeight(null);
		for (int y = 0; y < tmp.getHeight(null); y += dy) {
			for (int x = 0; x < tmp.getWidth(null); x += dx) {
				g.drawImage(small, x, y, null);
			}
		}
		g.dispose();
		small.flush();
		if (filteredImage != null) filteredImage.flush();

		// scale tiled image into filteredImage
		transform = AffineTransform.getScaleInstance(
			(double) src.getWidth(null) / (double) tmp.getWidth(null),
			(double) src.getHeight(null) / (double) tmp.getHeight(null));
		op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		filteredImage = op.filter(tmp, null);
		tmp.flush();
	}

	void applyPixelate(double pixelate) {
		// The absolute value of the raw pixelateCount plus 10 is divided by 10, so 10 gives a 2x2 pixels.
		// This is to make the range scale of this filter similar to other filters such as whirl.

		BufferedImage src = filteredImage;
		double factor = (Math.abs(pixelate) + 10) / 10;
		factor = Math.min(factor, Math.min(src.getWidth(null), src.getHeight(null)));
		if (factor <= 1.0) return;

		// scale down
		AffineTransform transform = AffineTransform.getScaleInstance(1/factor, 1/factor);
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage small = op.filter(src, null);

		// scale back up
		transform = AffineTransform.getScaleInstance(factor, factor);
		op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		filteredImage = op.filter(small, filteredImage);
		small.flush();
	}
}
