package net.morher.alarmus.plugins.wiz;

public class WizBulbParam {
    private Integer r;
    private Integer g;
    private Integer b;
    private Integer sceneId;
    private Integer dimming;

    public static WizBulbParam rgb(int r, int g, int b) {
        WizBulbParam param = new WizBulbParam();
        param.r = r;
        param.g = g;
        param.b = b;
        return param;
    }

    public static WizBulbParam warmwhite() {
        return scene(11);
    }

    public static WizBulbParam scene(int sceneId) {
        WizBulbParam param = new WizBulbParam();
        param.sceneId = sceneId;
        return param;
    }

    public Integer getR() {
        return r;
    }

    public Integer getG() {
        return g;
    }

    public Integer getB() {
        return b;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public Integer getDimming() {
        return dimming;
    }

    public void setDimming(Integer dimming) {
        this.dimming = dimming;
    }

    public WizBulbParam dimmed(int dimming) {
        this.dimming = dimming;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        if (r != null && g != null && b != null) {
            sb.append("\"r\":").append(r).append(",");
            sb.append("\"g\":").append(g).append(",");
            sb.append("\"b\":").append(b).append(",");
            sb.append("\"dimming\":").append(dimming);

        } else if (sceneId != null) {
            sb.append("\"sceneId\":").append(sceneId).append(",");
            sb.append("\"dimming\":").append(dimming);

        } else {
            sb.append("\"state\":false");

        }
        sb.append("}");

        return sb.toString();
    }

    public static WizBulbParam off() {
        return new WizBulbParam();
    }

}
