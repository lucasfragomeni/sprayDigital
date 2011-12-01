package cc.bebop.spraydigital;

public class Cursor {

	float x;
	float y;
	int cursorID;

	public Cursor(float x, float y, int cursorID) {
		this.x = x;
		this.y = y;
		this.cursorID = cursorID;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getCursorID() {
		return cursorID;
	}

	public void setCursorID(int cursorID) {
		this.cursorID = cursorID;
	}

	@Override
	public String toString() {
		return "Cursor [x=" + x + ", y=" + y + ", objectId=" + super.toString() + "]";
	}
}