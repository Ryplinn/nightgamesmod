package nightgames.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nightgames.global.DebugFlags;
import nightgames.global.Global;

public class ResourceLoader {
    public static List<InputStream> getFileResourcesFromDirectory(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            List<InputStream> streams = new ArrayList<InputStream>();
            try {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (!f.isDirectory()) {
                            streams.add(new FileInputStream(f));
                            if (Global.isDebugOn(DebugFlags.DEBUG_LOADING)) {
                                System.out.println("Using " + f.getAbsolutePath());
                            }
                        }
                    }
                }
                return streams;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    public static InputStream getFileResourceAsStream(String path) {
        // first check in the working directory
        File f = new File(path);
        return getFileResourceAsStream(f);
    }

    public static InputStream getFileResourceAsStream(File f) {
        try {
            InputStream res = new FileInputStream(f);
            return res;
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file " + f.getPath());
            e.printStackTrace();
            System.err.println("Current active directory is " + System.getProperty("user.dir"));
        }
        // then check in the class directory
        return ResourceLoader.class.getClassLoader().getResourceAsStream("resources/" + f.getPath());
    }
}
