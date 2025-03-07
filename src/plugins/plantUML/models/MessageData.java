package plugins.plantUML.models;


public class MessageData extends RelationshipData {

	private String sequenceNumber;
	private boolean isReply;
	private boolean isCreate;
	private boolean isDestroy;
	private boolean isRecursive;
	private LifelineData createdLifeline;
	private boolean isDuration;
	private int durationHeight;
	private boolean isLost;
	private boolean isFound;

	public MessageData(String source, String target, String type, String name) {
		super(source, target, type, name);
	}

	public String toExportFormat(boolean activate, String indent) {
		String symbol = isReply ? "-->" : "->";
		String activationString = "";
		if(this.isDuration) symbol = symbol.concat("(" + durationHeight + ")"); 
		if(this.isDestroy) symbol = symbol.concat("x"); 
		if(this.isRecursive || activate) activationString = " ++";
		String label = (sequenceNumber == null) ? "" : (sequenceNumber + " ");

		String prefix = (!label.isEmpty() || !getName().isEmpty()) ? " : " : "";

		String sourceFormatted = formatAlias(getSource());
		String targetFormatted = formatAlias(getTarget());

		// Special formatting for lost/found
		if ("[".equals(getSource())) {
			sourceFormatted = "[o";
			return indent + sourceFormatted + symbol + " " + targetFormatted + activationString + prefix + label + getName() + "\n";
		}
		if ("]".equals(getTarget())) {
			targetFormatted = "o]";
			return  indent + sourceFormatted + " " + symbol + targetFormatted + activationString + prefix + label + getName() + "\n";
		}
		
		String messageLine = indent + sourceFormatted + " " + symbol + " " + targetFormatted + activationString + prefix + label + getName() + "\n";
		
		if (this.isRecursive)
			return messageLine + indent + "deactivate " + targetFormatted + "\n";
		return messageLine;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public boolean isReply() {
		return isReply;
	}

	public void setReply(boolean isReply) {
		this.isReply = isReply;
	}

	public boolean isCreate() {
		return isCreate;
	}

	public void setCreate(boolean isCreate, LifelineData createdLifelineData) {
		this.isCreate = isCreate;
		setCreatedLifeline(createdLifelineData);
	}

	public void setCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}

	public boolean isDestroy() {
		return isDestroy;
	}

	public void setDestroy(boolean isDestroy) {
		this.isDestroy = isDestroy;
	}

	public LifelineData getCreatedLifeline() {
		return createdLifeline;
	}

	public void setCreatedLifeline(LifelineData createdLifeline) {
		this.createdLifeline = createdLifeline;
	}

	public void setDuration(int durationHeight) {
		this.isDuration = true;
		this.durationHeight = durationHeight;

	}

	public boolean isDuration() {
		return this.isDuration;
	}

	public int getDurationHeight() {
		return durationHeight;
	}

	public boolean isRecursive() {
		return isRecursive;
	}

	public void setRecursive(boolean isRecursive) {
		this.isRecursive = isRecursive;
	}

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean lost) {
        isLost = lost;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }
}
