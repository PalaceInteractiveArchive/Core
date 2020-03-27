package network.palace.core.plugin.processor;

import network.palace.core.plugin.PluginInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

@SupportedAnnotationTypes("network.palace.core.plugin.*")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class PluginInfoProcessor extends AbstractProcessor {

    private boolean hasMainBeenFound = false;

    @Override
    public boolean process(Set<? extends TypeElement> annots, RoundEnvironment rEnv) {
        Element pluginInfo = null;
        for (Element el : rEnv.getElementsAnnotatedWith(PluginInfo.class)) {
            if (pluginInfo != null) {
                raiseError("More than one class with @PluginInfo found, aborting!");
                return false;
            }
            pluginInfo = el;
        }
        if (pluginInfo == null) return false;

        if (hasMainBeenFound) {
            raiseError("More than one class with @PluginInfo found, aborting!");
            return false;
        }
        hasMainBeenFound = true;

        TypeElement mainType;
        if (pluginInfo instanceof TypeElement) {
            mainType = (TypeElement) pluginInfo;
        } else {
            raiseError("Element annotated with @Main is not a type!");
            return false;
        }
        if (!(mainType.getEnclosingElement() instanceof PackageElement) && !mainType.getModifiers().contains(Modifier.STATIC)) {
            raiseError("Element annotated with @Main is not top-level or static nested!");
            return false;
        }
        if (!processingEnv.getTypeUtils().isSubtype(mainType.asType(), fromClass(JavaPlugin.class))) {
            raiseError("Class annotated with @Main is not an subclass of JavaPlugin!");
        }

        // Process All
        final String mainName = mainType.getQualifiedName().toString();

        String name = process("name", mainType, mainName.substring(mainName.lastIndexOf('.') + 1), PluginInfo.class, String.class);
        String author = "Palace Network";
        String version = process("version", mainType, "1.0.0", PluginInfo.class, String.class);
        String apiVersion = process("api-version", mainType, "", PluginInfo.class, String.class);
        String[] depend = process("depend", mainType, null, PluginInfo.class, String[].class);
        String[] softdepend = process("softdepend", mainType, null, PluginInfo.class, String[].class);
        final ProcessedPluginInfo processedPluginInfo = new ProcessedPluginInfo(name, author, version, apiVersion, depend, softdepend, mainName);

        // Save to plugin.yml
        try {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
            Writer writer = file.openWriter();
            try {
                Yaml yaml = new Yaml(processedPluginInfo.getRepresenter(), new DumperOptions());
                yaml.dump(processedPluginInfo.toYamlMap(), writer);
            } finally {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private void raiseError(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }

    private TypeMirror fromClass(Class<?> clazz) {
        return processingEnv.getElementUtils().getTypeElement(clazz.getName()).asType();
    }

    private <A extends Annotation, R> R process(String valueName, Element el, R defaultVal, Class<A> annotationType, Class<R> returnType) {
        R result;
        A ann = el.getAnnotation(annotationType);
        if (ann == null) {
            result = defaultVal;
        } else {
            try {
                Method value = annotationType.getMethod(valueName.replaceAll("-", ""));
                Object res = value.invoke(ann);
                result = (R) (returnType == String.class ? res.toString() : returnType.cast(res));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
