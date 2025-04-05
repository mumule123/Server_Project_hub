package jakarta.faces.flow;

import jakarta.faces.context.FacesContext;


public abstract class ReturnNode extends FlowNode
{
    public abstract String getFromOutcome(FacesContext context);
    
}


package org.apache.tiles.template;

import java.io.IOException;
import java.util.Deque;

import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.core.runtime.annotation.Parameter;
import org.apache.tiles.mgmt.MutableTilesContainer;
import org.apache.tiles.request.Request;


public class DefinitionModel {

    
    public void execute(String name, String template, String role,
            @Parameter(name = "extends") String extendsParam, String preparer,
            Request request, ModelBody modelBody) throws IOException {
        Deque<Object> composeStack = ComposeStackUtil
                .getComposeStack(request);
        Definition definition = createDefinition(name, template, role,
                extendsParam, preparer);
        composeStack.push(definition);
        modelBody.evaluateWithoutWriting();
        MutableTilesContainer container = (MutableTilesContainer) TilesAccess
                .getCurrentContainer(request);
        definition = (Definition) composeStack.pop();
        registerDefinition(definition, container, composeStack, request);
    }

    
    private Definition createDefinition(String name, String template,
            String role, String extendsParam, String preparer) {
        Definition definition = new Definition();
        definition.setName(name);
        Attribute templateAttribute = Attribute
                .createTemplateAttribute(template);
        templateAttribute.setRole(role);
        definition.setTemplateAttribute(templateAttribute);
        definition.setExtends(extendsParam);
        definition.setPreparer(preparer);
        return definition;
    }

    
    private void registerDefinition(Definition definition,
            MutableTilesContainer container, Deque<Object> composeStack,
            Request request) {
        container.register(definition, request);

        if (composeStack.isEmpty()) {
            return;
        }

        Object obj = composeStack.peek();
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute) obj;
            attribute.setValue(definition.getName());
            if (attribute.getRenderer() == null) {
                attribute.setRenderer("definition");
            }
        }
    }
}