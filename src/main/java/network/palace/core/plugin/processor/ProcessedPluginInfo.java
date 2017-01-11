package network.palace.core.plugin.processor;

import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.beans.IntrospectionException;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class ProcessedPluginInfo {

    private String name = "";
    private String version = "";
    private String[] depend = {};
    private String[] softdepend = {};
    private String main = "";

    public ProcessedPluginInfo(String name, String version, String[] depend, String[] softdepend, String main) {
        this.name = name;
        this.version = version;
        this.depend = depend;
        this.softdepend = softdepend;
        this.main = main;
    }

    public Representer getRepresenter() {
        Representer representer = new SkipEmptyRepresenter();
        representer.addClassTag(ProcessedPluginInfo.class, Tag.MAP);
        representer.setPropertyUtils(new UnsortedPropertyUtils());
        return representer;
    }

    private class UnsortedPropertyUtils extends PropertyUtils {
        @Override
        protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) throws IntrospectionException {
            return new LinkedHashSet<>(getPropertiesMap(type, BeanAccess.FIELD).values());
        }
    }

    private class SkipEmptyRepresenter extends Representer {
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            Node valueNode = tuple.getValueNode();
            if (Tag.NULL.equals(valueNode.getTag())) {
                return null;
            }
            if (valueNode instanceof CollectionNode) {
                if (Tag.SEQ.equals(valueNode.getTag())) {
                    SequenceNode seq = (SequenceNode) valueNode;
                    if (seq.getValue().isEmpty()) {
                        return null;
                    }
                }
                if (Tag.MAP.equals(valueNode.getTag())) {
                    MappingNode seq = (MappingNode) valueNode;
                    if (seq.getValue().isEmpty()) {
                        return null;
                    }
                }
            }
            return tuple;
        }
    }
}
