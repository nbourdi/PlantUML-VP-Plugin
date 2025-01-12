package plugins.plantUML.models;

public class StateData extends BaseWithSemanticsData {

    private String Uid;
    private boolean isStart;
    private boolean isEnd;

    public StateData(String name) {
        super(name);
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
