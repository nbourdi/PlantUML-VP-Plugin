package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

/*
 * PUML description diagrams do not support component and interface operations or fields.
 * To address this, a JSON structure containing these details, if applicable, will be
 * embedded within the same PUML diagram using the `allowmixing` parameter.
 * On import, this JSON structure will be parsed to restore the additional information
 * into Visual Paradigm (VP), alongside the supported features.
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY) // Annotation to omit empty or null fields when constructing the json
public class FieldAndOperationInfo {
    private String elementName; // owner element name
    private String elementType;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OperationData> operations = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<AttributeData> attributes = new ArrayList<>();

    public List<OperationData> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationData> operations) {
        this.operations = operations;
    }

    public List<AttributeData> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeData> attributes) {
        this.attributes = attributes;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }
}
