../../../../../../../../../src/net/sourceforge/fidocadj/circuit/model/DrawingModel.java
package jakarta.faces.flow;

import jakarta.faces.context.FacesContext;


public abstract class ReturnNode extends FlowNode
{
    public abstract String getFromOutcome(FacesContext context);
    
}