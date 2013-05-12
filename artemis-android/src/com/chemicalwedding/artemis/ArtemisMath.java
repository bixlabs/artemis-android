package com.chemicalwedding.artemis;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.graphics.Color;
import android.util.Log;

import com.chemicalwedding.artemis.database.Camera;
import com.chemicalwedding.artemis.database.Lens;
import com.chemicalwedding.artemis.database.ZoomLens;

public class ArtemisMath {

	private static ArtemisMath instance = new ArtemisMath();

	private static final float WALL_DISTANCE = 1000;

	private static final String logTag = "ArtemisMath";
	private Camera _selectedCamera;
	private ArrayList<Lens> _selectedLenses;
	private Lens _selectedLens;
	private ArtemisRectF selectedLensBox, currentGreenBox;
	private int _selectedLensIndex = 0;
	int screenWidth, screenHeight;

	public static float deviceHorizontalWidth, deviceVerticalWidth;
	private float _pixelDensity;
	private boolean isFullscreen = false;
	private Float _touchX, _touchY;
	private float _minTouchX, _minTouchY, _maxTouchX, _maxTouchY;
	private boolean isInitializedFirstTime = false;

	private int firstMeaningfulLens = -1;
	private float _largestViewableFocalLength;
	private ArrayList<ArtemisRectF> _currentLensBoxes = new ArrayList<ArtemisRectF>();

	private boolean isHAngleMode = true;
	public float[] selectedLensAngleData;

	public static int scaledPreviewWidth, scaledPreviewHeight;
	public static float horizViewAngle, vertViewAngle;

	public ZoomLens selectedZoomLens;
	public static int orangeBoxColor;

	private static final int SPACING = 9;

	protected float zoomLensFullScreenFL;

	// private double androidScale;

	public ArtemisMath() {
	}

	public void setupDeviceCameraDetails() {

	}

	public void resetTouchToCenter() {
		if (currentGreenBox != null) {
			_touchX = currentGreenBox.centerX();
			_touchY = currentGreenBox.centerY();
		} else {
			_touchX = (float) (screenWidth / 2);
			_touchY = (float) (screenHeight / 2);
		}
	}

	public void setDeviceSpecificDetails(int screenWidth, int screenHeight,
			float pixelDensity, float horizViewAngle, float vertViewAngle) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		ArtemisMath.horizViewAngle = horizViewAngle;
		ArtemisMath.vertViewAngle = vertViewAngle;
		_pixelDensity = pixelDensity;

		resetTouchToCenter(); // just initialize to default value (no green box)

		deviceHorizontalWidth = (float) (2 * (Math.tan(Math
				.toRadians(horizViewAngle / 2)) * WALL_DISTANCE));
		deviceVerticalWidth = (float) (2 * (Math.tan(Math
				.toRadians(vertViewAngle / 2)) * WALL_DISTANCE));

		Log.v(logTag, "Pixel Density: " + _pixelDensity + " Horiz:"
				+ deviceHorizontalWidth + " Vert:" + deviceVerticalWidth);
	}

	public void setCustomViewAngle(float horizViewAngle, float vertViewAngle) {
		ArtemisMath.horizViewAngle = horizViewAngle;
		ArtemisMath.vertViewAngle = vertViewAngle;
		deviceHorizontalWidth = (float) (2 * (Math.tan(Math
				.toRadians(horizViewAngle / 2)) * WALL_DISTANCE));
		deviceVerticalWidth = (float) (2 * (Math.tan(Math
				.toRadians(vertViewAngle / 2)) * WALL_DISTANCE));
	}

	public float[] calculateViewingAngle(float lensFocalLength) {
		float Hfraction = (_selectedCamera.getHoriz() * _selectedCamera
				.getSqz()) / (2 * lensFocalLength);
		float Vfraction = (_selectedCamera.getVertical() * 1)
				/ (2 * lensFocalLength);
		float HviewingAngle = (float) (2 * (Math
				.toDegrees(Math.atan(Hfraction))));
		float VviewingAngle = (float) (2 * (Math
				.toDegrees(Math.atan(Vfraction))));
		float HStandard = calculateWidthAndHeightLens(HviewingAngle);
		float VStandard = calculateWidthAndHeightLens(VviewingAngle);
		float hprop = (HStandard / deviceHorizontalWidth);
		float vprop = (VStandard / deviceVerticalWidth);
		float[] angles = { HviewingAngle, VviewingAngle, HStandard, VStandard,
				hprop, vprop };
		return angles;
	}

	/**
	 * this function calculates what the widest lens that the phone COULD show
	 * for this format. We use it to show a green box on the wireframe mode
	 * 
	 * @return largest focal length
	 */
	public void calculateLargestLens() {
		float hva = (float) (2 * Math.toDegrees(Math.atan(deviceHorizontalWidth
				/ (WALL_DISTANCE * 2))));
		float hfraction = (float) Math.tan(Math.toRadians(hva / 2));
		float lensa = ((_selectedCamera.getHoriz() * _selectedCamera.getSqz()) / hfraction) / 2;
		float vva = (float) (2 * Math.toDegrees(Math.atan(deviceVerticalWidth
				/ (WALL_DISTANCE * 2))));
		float vfraction = (float) Math.tan(Math.toRadians(vva / 2));
		float lensb = ((_selectedCamera.getVertical() * (scaledPreviewWidth / scaledPreviewHeight)) / vfraction) / 2;
		if (lensa < lensb) {
			_largestViewableFocalLength = lensb;
		} else {
			_largestViewableFocalLength = lensa;
		}
	}

	private static float calculateWidthAndHeightLens(float angle) {
		float updown = (float) (WALL_DISTANCE * 2 * (Math.tan(Math
				.toRadians(angle / 2))));
		return updown;
	}

	/**
	 * Used in calculating the view angle for a custom camera
	 * 
	 * @param distance
	 *            The distance from the wall
	 * @param width
	 *            The width the camera can see
	 * @return The calculated viewing angle
	 */
	public static float calculateAngleForCustomCamera(float widthOrHeight) {
		return (float) (2f * (Math.toDegrees(Math.atan(widthOrHeight / 2
				/ WALL_DISTANCE))));
	}

	public void setSelectedCamera(Camera selectedCamera) {
		_selectedCamera = selectedCamera;
	}

	public void setSelectedLenses(ArrayList<Lens> selectedLenses) {
		_selectedLenses = selectedLenses;
	}

	public void setSelectedLens(Lens selectedLens) {
		_selectedLens = selectedLens;
	}

	NumberFormat lensFLNumberFormat = NumberFormat.getNumberInstance();

	public void calculateRectBoxesAndLabelsForLenses() {

		int lowerMargin = (int) (screenHeight * 0.885f);
		int topMargin = (int) (screenHeight * 0.05f);

		_currentLensBoxes.clear();

		float[] angleData = calculateViewingAngle(_largestViewableFocalLength);

		/**
		 * Draw Green box
		 */

		int maximumWidth = (int) ((lowerMargin - topMargin) * angleData[2] / angleData[3]);

		if (maximumWidth > screenWidth - 2) {
			float hprop = angleData[4];
			float myprop = angleData[3] / angleData[2];

			// Hack to give tablets more width
			final float REQUESTED_WIDTH_RATIO = 0.99f;
			int hwidth = (int) (screenWidth * REQUESTED_WIDTH_RATIO * hprop);
			int vheight = (int) (hwidth * myprop);

			int xmin = 2;

			int x = 2, y = topMargin;
			ArtemisRectF greenBox = new ArtemisRectF(""
					+ (int) _largestViewableFocalLength, xmin, y,
					xmin + hwidth, y + vheight);
			if (!isFullscreen) {
				greenBox.setColor(Color.GREEN);
			} else
				greenBox.setColor(Color.RED);

			ArtemisRectF blackBox1 = new ArtemisRectF(null, 0, 0, screenWidth,
					greenBox.top);
			blackBox1.setColor(Color.BLACK);
			blackBox1.setSolid(true);

			ArtemisRectF blackBox2 = new ArtemisRectF(null, xmin + hwidth, 0,
					screenWidth, screenHeight);
			blackBox2.setColor(Color.BLACK);
			blackBox2.setSolid(true);

			ArtemisRectF blackBox3 = new ArtemisRectF(null, 0, 0, screenWidth,
					y);
			blackBox3.setColor(Color.BLACK);
			blackBox3.setSolid(true);

			ArtemisRectF blackBox4 = new ArtemisRectF(null, 0, greenBox.bottom,
					screenWidth, screenHeight);
			blackBox4.setColor(Color.BLACK);
			blackBox4.setSolid(true);

			currentGreenBox = greenBox;
			_currentLensBoxes.add(blackBox1);
			_currentLensBoxes.add(blackBox2);
			_currentLensBoxes.add(blackBox3);
			_currentLensBoxes.add(blackBox4);

			_minTouchX = x;
			_minTouchY = y;
			_maxTouchX = x + hwidth;
			_maxTouchY = y + vheight;

		} else {
			float hwidth = angleData[2];
			float vheight = angleData[3];
			float myprop = hwidth / vheight;
			maximumWidth = (int) ((lowerMargin - topMargin) * myprop);

			// center 4:3 view
			int xmin = (screenWidth - maximumWidth) / 2;
			int xmax = xmin + maximumWidth;
			ArtemisRectF greenBox = new ArtemisRectF(""
					+ (int) _largestViewableFocalLength, xmin, topMargin, xmax,
					lowerMargin);
			if (!isFullscreen) {
				greenBox.setColor(orangeBoxColor);
			} else
				greenBox.setColor(Color.RED);

			ArtemisRectF blackBox1 = new ArtemisRectF(null, 0, 0, xmin,
					screenHeight);
			blackBox1.setSolid(true);
			ArtemisRectF blackBox2 = new ArtemisRectF(null, xmax, 0,
					screenWidth, screenHeight);
			blackBox2.setSolid(true);

			ArtemisRectF blackBox3 = new ArtemisRectF(null, 0, 0, screenWidth,
					topMargin);
			blackBox3.setColor(Color.BLACK);
			blackBox3.setSolid(true);

			ArtemisRectF blackBox4 = new ArtemisRectF(null, 0, lowerMargin,
					screenWidth, screenHeight);
			blackBox4.setColor(Color.BLACK);
			blackBox4.setSolid(true);

			currentGreenBox = greenBox;
			_currentLensBoxes.add(blackBox1);
			_currentLensBoxes.add(blackBox2);
			_currentLensBoxes.add(blackBox3);
			_currentLensBoxes.add(blackBox4);

			_minTouchX = xmin;
			_minTouchY = topMargin;
			_maxTouchX = xmax;
			_maxTouchY = lowerMargin;
		}

		/**
		 * Add all the white boxes
		 */

		firstMeaningfulLens = -1;
		// loop over each lens selected in the db and create boxes/labels
		int lensIndex = 0;
		int validcount = 0;
		// if (!isFullscreen) {
		for (Lens currentLens : _selectedLenses) {
			int x;
			int y;
			float lensFocalLength = currentLens.getFL();

			angleData = calculateViewingAngle(lensFocalLength);
			float myprop = currentGreenBox.height() / currentGreenBox.width();

			int hwidth = (int) (scaledPreviewWidth * angleData[0] / horizViewAngle);
			if (myprop < 1.4f) {
				hwidth *= currentGreenBox.width() / scaledPreviewWidth;
			}

			int vheight = (int) (hwidth * myprop);

			boolean ignoreThisOne = true;

			if (lensFocalLength > _largestViewableFocalLength) {
				validcount++;
				ignoreThisOne = false;
				if (firstMeaningfulLens == -1) {
					firstMeaningfulLens = lensIndex;
				}
			}
			// moveboxes according to touch
			float newx = (_touchX - (hwidth / 2f));
			float newy = (_touchY - (vheight / 2f));
			int xmin = (int) currentGreenBox.left;
			int ymin = (int) currentGreenBox.top;
			int xmax = (int) currentGreenBox.right - hwidth;
			int ymax = (int) currentGreenBox.bottom - (SPACING * (validcount))
					- vheight;

			x = (int) newx;
			y = (int) newy;

			if (newx < xmin) {
				x = xmin;
			}
			if (newy < ymin) {
				y = ymin;
			}

			if (newx > xmax) {
				x = xmax;
			}
			if (newy > ymax) {
				y = ymax;
			}
			int bottomy = vheight + y;

			if (!ignoreThisOne || selectedZoomLens != null) {
				// add box 1
				ArtemisRectF box1 = new ArtemisRectF(
						lensFLNumberFormat.format(lensFocalLength), x, y, x
								+ hwidth, bottomy);
				Integer rectColor = null;
				if (lensIndex == _selectedLensIndex) {
					rectColor = Color.RED;
					_selectedLens = currentLens;
					selectedLensBox = box1;
					selectedLensAngleData = angleData;
				} else {
					rectColor = Color.WHITE;
				}
				box1.setColor(rectColor);

				if (!isFullscreen && !ignoreThisOne) {
					_currentLensBoxes.add(box1);
				}
			}
			lensIndex++;
		}
		// }

		// Log.v(logTag, "**************** All boxes: ");
		// for (ArtemisRectF box : _currentLensBoxes) {
		// Log.v(logTag, box.toString());
		// }
		// Log.v(logTag, "************* Selected box: ");
		// Log.v(logTag,
		// selectedLensBox == null ? "null" : selectedLensBox.toString());
	}

	public String get_selectedLensFocalLength() {
		if (_selectedLens != null) {
			return NumberFormat.getInstance().format(_selectedLens.getFL());
		} else
			return "";
	}

	public ArrayList<ArtemisRectF> get_currentLensBoxes() {
		return _currentLensBoxes;
	}

	public boolean isFullscreen() {
		return isFullscreen;
	}

	public void setFullscreen(boolean isFullscreen) {
		this.isFullscreen = isFullscreen;
	}

	public void setTouchX(float touchX) {
		_touchX = touchX;
	}

	public float get_minTouchX() {
		return _minTouchX;
	}

	public void set_minTouchX(float minTouchX) {
		_minTouchX = minTouchX;
	}

	public float get_minTouchY() {
		return _minTouchY;
	}

	public void set_minTouchY(float minTouchY) {
		_minTouchY = minTouchY;
	}

	public float get_maxTouchX() {
		return _maxTouchX;
	}

	public void set_maxTouchX(float maxTouchX) {
		_maxTouchX = maxTouchX;
	}

	public float get_maxTouchY() {
		return _maxTouchY;
	}

	public void set_maxTouchY(float maxTouchY) {
		_maxTouchY = maxTouchY;
	}

	public void setTouchY(float touchY) {
		_touchY = touchY;
	}

	public Camera getSelectedCamera() {
		return _selectedCamera;
	}

	public ArrayList<Lens> getSelectedLenses() {
		return _selectedLenses;
	}

	public int get_selectedLensIndex() {
		return _selectedLensIndex;
	}

	public void set_selectedLensIndex(int selectedLensIndex) {
		_selectedLensIndex = selectedLensIndex;
	}

	public static ArtemisMath getInstance() {
		return instance;
	}

	public boolean selectNextLens() {
		if (_selectedLensIndex + 1 < _selectedLenses.size()) {
			++_selectedLensIndex;
			_selectedLens = _selectedLenses.get(_selectedLensIndex);
			return true;
		}
		return false;
	}

	public boolean hasNextLens() {
		if (_selectedLensIndex + 1 < _selectedLenses.size()) {
			return true;
		}
		return false;
	}

	public boolean selectPreviousLens() {
		if (_selectedLensIndex > 0) {
			--_selectedLensIndex;
			_selectedLens = _selectedLenses.get(_selectedLensIndex);
			return true;
		}
		return false;
	}

	public boolean hasPreviousLens() {
		if (!isFullscreen && firstMeaningfulLens > 0
				&& _selectedLensIndex > firstMeaningfulLens) {
			return true;
		} else if (isFullscreen && _selectedLensIndex > 0) {
			return true;
		}
		return false;
	}

	public boolean hasPreviousZoomLens() {
		if (isFullscreen && zoomLensFullScreenFL > selectedZoomLens.getMinFL()) {
			return true;
		} else if (!isFullscreen) {
			return hasPreviousLens();
		}
		return false;
	}

	public boolean hasNextZoomLens() {
		if (isFullscreen && zoomLensFullScreenFL < selectedZoomLens.getMaxFL()) {
			return true;
		} else if (!isFullscreen) {
			return hasNextLens();
		}
		return false;
	}

	public void onFullscreenOffSelectLens() {
		// when we are coming out of fullscreen, make sure we have at least
		// first meaningful lens
		if (_selectedLensIndex < firstMeaningfulLens)
			selectFirstMeaningFullLens();
	}

	public void selectFirstMeaningFullLens() {
		if (firstMeaningfulLens > 0) {
			_selectedLensIndex = firstMeaningfulLens;
		} else {
			_selectedLensIndex = _selectedLenses.size() - 1;
		}
		_selectedLens = _selectedLenses.get(_selectedLensIndex);
	}

	/**
	 * Used while in fullscreen for calculating the rect box on the fly
	 */
	public float calculateFullscreenZoomRatio() {
		float lensFocalLength;
		if (selectedZoomLens == null) {
			lensFocalLength = _selectedLens.getFL();
		} else {
			lensFocalLength = zoomLensFullScreenFL;
			selectedLensBox = this
					.calculateFullscreenLensBoxForFocalLength(lensFocalLength);
		}
		selectedLensAngleData = calculateViewingAngle(lensFocalLength);

		float hwidth = (scaledPreviewWidth * selectedLensAngleData[0] / horizViewAngle);
		float myprop = currentGreenBox.height() / currentGreenBox.width();
		if (myprop < 1.4f) {
			hwidth *= currentGreenBox.width() / scaledPreviewWidth;
		}

		return currentGreenBox.width() / hwidth;
	}

	public void setHAngleMode(boolean isHAngleMode) {
		this.isHAngleMode = isHAngleMode;
	}

	public boolean isHAngleMode() {
		return isHAngleMode;
	}

	public float[] getSelectedLensAngleData() {
		return selectedLensAngleData;
	}

	public ArtemisRectF getSelectedLensBox() {
		return selectedLensBox;
	}

	public ArtemisRectF getCurrentGreenBox() {
		return currentGreenBox;
	}

	public void setCurrentGreenBox(ArtemisRectF rect) {
		currentGreenBox = rect;
	}

	public void setInitializedFirstTime(boolean isInitializedFirstTime) {
		this.isInitializedFirstTime = isInitializedFirstTime;
	}

	public boolean isInitializedFirstTime() {
		return isInitializedFirstTime;
	}

	public float getPixelDensity() {
		return _pixelDensity;
	}

	public void calculateZoomLenses() {
		Lens l = new Lens();
		l.setFL(selectedZoomLens.getMinFL());
		_selectedLenses.add(l);

		l = new Lens();
		l.setFL(selectedZoomLens.getMaxFL());
		_selectedLenses.add(l);
	}

	public void onFullscreenSetupZoomLens() {
		zoomLensFullScreenFL = _selectedLens.getFL();
	}

	public ArtemisRectF calculateFullscreenLensBoxForFocalLength(
			float lensFocalLength) {
		selectedLensAngleData = calculateViewingAngle(lensFocalLength);

		float hwidth = (scaledPreviewWidth * selectedLensAngleData[0] / horizViewAngle);
		float myprop = currentGreenBox.height() / currentGreenBox.width();
		if (myprop < 1.4f) {
			hwidth *= currentGreenBox.width() / scaledPreviewWidth;
		}

		int vheight = (int) (hwidth * myprop);

		float newx = (_touchX - (hwidth / 2f));
		float newy = (_touchY - (vheight / 2f));

		int validcount = 0;
		if (selectedZoomLens != null) {
			if (selectedZoomLens.getMinFL() > _largestViewableFocalLength) {
				++validcount;
			}
			if (selectedZoomLens.getMaxFL() > _largestViewableFocalLength) {
				++validcount;
			}
		}

		int xmin = (int) currentGreenBox.left;
		int ymin = (int) currentGreenBox.top;
		int xmax = (int) currentGreenBox.right - (int) hwidth;
		int ymax = (int) currentGreenBox.bottom - (SPACING * (validcount))
				- vheight;

		int x = (int) newx;
		int y = (int) newy;

		if (newx < xmin) {
			x = xmin;
		}
		if (newy < ymin) {
			y = ymin;
		}

		if (newx > xmax) {
			x = xmax;
		}
		if (newy > ymax) {
			y = ymax;
		}
		int bottomy = vheight + y;

		return new ArtemisRectF("", x, y, x + hwidth, bottomy);
	}

	final static float ZOOM_LENS_CLICK_FACTOR = 1;

	public void decrementFullscreenZoomLens() {
		if (zoomLensFullScreenFL > selectedZoomLens.getMinFL())
			zoomLensFullScreenFL -= ZOOM_LENS_CLICK_FACTOR;
	}

	public void incrementFullscreenZoomLens() {
		if (zoomLensFullScreenFL < selectedZoomLens.getMaxFL())
			zoomLensFullScreenFL += ZOOM_LENS_CLICK_FACTOR;
	}
}
