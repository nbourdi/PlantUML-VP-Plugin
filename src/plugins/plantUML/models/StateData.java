package plugins.plantUML.models;

import com.jniwrapper.Str;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;

public class StateData extends BaseWithSemanticsData {

    private String Uid;
    private boolean isStart;
    private boolean isEnd;
    private List<StateRegion> regions = new ArrayList<>();
    private boolean isInState;
    private String id;
    private String alias;


    public StateData(String name) {
        super(name);
    }

    private String generateAlias() {
        // if (this.name == null) {
        if(this.id != null)
            return "state_" + id.replaceAll("[^a-zA-Z0-9]", "_");
        //}
        // return name;
        return null;
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

    public List<StateRegion> getRegions() {
        return regions;
    }

    public void setRegions(List<StateRegion> regions) {
        this.regions = regions;
    }

    public boolean isInState() {
        return isInState;
    }

    public void setInState(boolean inState) {
        isInState = inState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.alias = generateAlias();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }


    public static class StateRegion {
        private List<StateData> subStates = new ArrayList<>();

        private  List<History> histories = new ArrayList<>();

        public StateRegion() {

        }

        public List<StateData> getSubStates() {
            return subStates;
        }

        public void setSubStates(List<StateData> subStates) {
            this.subStates = subStates;
        }

        public List<History> getHistories() {
            return histories;
        }

        public void setHistories(List<History> histories) {
            this.histories = histories;
        }
    }
}
