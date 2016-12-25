import Anotation.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by lenovo on 2016/12/23.
 */
public class ClassScaner {
    public static Map<Class<?>, Object> serviceMap = new HashMap<>();

    static {
        try {
            List<Class<?>> clazzs = scanClass("serviceInterfaceImpl");
            for (Class<?> clazz : clazzs) {
                if (clazz.isAnnotationPresent(Service.class)) {
                    String key = clazz.getAnnotation(Service.class).value();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Class<?>> scanClass(String pckName) throws IOException {
        List<Class<?>> clazzs = new ArrayList<>();

        // 是否循环搜索子包
        boolean recursive = false;
        String packageDirName = pckName.replace('.', '/');
        Enumeration<URL> dirs = null;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();

                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    System.out.println("file类型的扫描");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassInPackageByFile(pckName, filePath, recursive, clazzs);
                } else if ("jar".equals(protocol)) {
                    System.out.println("jar类型的扫描");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazzs;
    }

    public static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive, List<Class<?>> clazzs) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
