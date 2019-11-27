package outskirts.util;

import outskirts.client.GameSettings;
import outskirts.client.Loader;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceManager {

    static InputStream getInputStream(ResourceLocation resource) {
        try {
            InputStream inputStream = null;
            String path = "assets/" + resource.getResourceDomain() + "/" + resource.getResourcePath();

//            //ResourcePacks
//            for (int i = GameSettings.RESOURCE_PACKS.size() - 1; i >= 0; i--) {
//                if (inputStream == null) {
//                    ZipFile zipFile = GameSettings.RESOURCE_PACKS.get(i);
//                    ZipEntry zipPathEntry = zipFile.getEntry(path);
//                    if (zipPathEntry != null) {
//                        inputStream = zipFile.getInputStream(zipPathEntry);
//                    }
//                }
//            }

            //jar assets
            if (inputStream == null) {
                if (resource.getResourceDomain().equals("outskirts")) {
                    inputStream = Loader.class.getResourceAsStream("/" + path);
//                } else {
//                    if (Mods.REGISTRY.containsKey(resource.getResourceDomain())) {
//                        JarFile modJarFile = new JarFile(Mods.REGISTRY.get(resource.getResourceDomain()).getFile());
//                        inputStream = modJarFile.getInputStream(modJarFile.getEntry(path));
//                    }
                }
            }

            return inputStream;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
