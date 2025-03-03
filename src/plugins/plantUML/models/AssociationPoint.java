package plugins.plantUML.models;

import java.util.List;

public class AssociationPoint {
	private String Uid; //apoint id
	
	private String FromUid1; 
	private String FromUid2;
	private String ToUid;
	
	private AssociationData associationData;
	

	public AssociationPoint(String Uid) {
		setUid(Uid);
	}

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}

	public String getFromUid1() {
		return FromUid1;
	}

	public void setFromUid1(String fromUid1) {
		FromUid1 = fromUid1;
	}

	public String getToUid() {
		return ToUid;
	}

	public void setToUid(String toUid) {
		ToUid = toUid;
	}

	public String getFromUid2() {
		return FromUid2;
	}

	public void setFromUid2(String fromUid2) {
		FromUid2 = fromUid2;
	}

	public void setAssociationData(AssociationData associationData) {
		this.associationData = associationData;
	}
	public AssociationData getAssociationData() {
		return associationData;
	}
}
