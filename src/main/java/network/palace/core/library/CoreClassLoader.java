package network.palace.core.library;

import java.net.URL;
import java.net.URLClassLoader;

public class CoreClassLoader extends URLClassLoader {

    public CoreClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }
}
