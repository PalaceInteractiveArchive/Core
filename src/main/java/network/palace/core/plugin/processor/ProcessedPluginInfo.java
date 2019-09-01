package network.palace.core.plugin.processor;

import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.util.*;

public class ProcessedPluginInfo {

    private String name;
    private String author;
    private String version;
    private String apiVersion;
    private String[] depend;
    private String[] softdepend;
    private String main;

    public ProcessedPluginInfo(String name, String author, String version, String apiVersion, String[] depend, String[] softdepend, String main) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.apiVersion = apiVersion;
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

    public Map<String, Object> toYamlMap() {
        return new LinkedHashMap<String, Object>() {{
            put("name", name);
            put("author", author);
            put("version", version);
            if (!apiVersion.isEmpty()) put("api-version", apiVersion);
            put("depend", depend);
            put("softdepend", softdepend);
            put("main", main);
        }};
    }

    private class UnsortedPropertyUtils extends PropertyUtils {
        @Override
        protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) {
            return new LinkedHashSet<>(getPropertiesMap(type, BeanAccess.FIELD).values());
        }
    }

    private class SkipEmptyRepresenter extends Representer {
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            Node valueNode = tuple.getValueNode();
            if (Tag.NULL.equals(valueNode.getTag())) return null;
            if (valueNode instanceof CollectionNode) {
                if (Tag.SEQ.equals(valueNode.getTag())) {
                    SequenceNode seq = (SequenceNode) valueNode;
                    if (seq.getValue().isEmpty()) return null;
                }
                if (Tag.MAP.equals(valueNode.getTag())) {
                    MappingNode seq = (MappingNode) valueNode;
                    if (seq.getValue().isEmpty()) return null;
                }
            }
            return tuple;
        }
    }

    @Override
    public String toString() {
        return name + " " + author + " " + version + " " + apiVersion + " " + Arrays.toString(depend) + " " + Arrays.toString(softdepend) + " " + main;
    }
}
