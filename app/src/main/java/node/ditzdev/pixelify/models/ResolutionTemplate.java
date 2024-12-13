package node.ditzdev.pixelify.models;

public class ResolutionTemplate {
    private String deviceName;
    private int logicalWidth;
    private int logicalHeight;
    private int ppi;
    private float scaleFactor;
    private String screenDiagonal;

    public ResolutionTemplate(String deviceName, int logicalWidth, int logicalHeight, 
                               int ppi, float scaleFactor, String screenDiagonal) {
        this.deviceName = deviceName;
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        this.ppi = ppi;
        this.scaleFactor = scaleFactor;
        this.screenDiagonal = screenDiagonal;
    }

    public String getDeviceName() { return deviceName; }
    public int getLogicalWidth() { return logicalWidth; }
    public int getLogicalHeight() { return logicalHeight; }
    public int getPpi() { return ppi; }
    public float getScaleFactor() { return scaleFactor; }
    public String getScreenDiagonal() { return screenDiagonal; }
}