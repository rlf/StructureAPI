package dk.lockfuglsang.minecraft.structureapi;

import dk.lockfuglsang.minecraft.reflection.ReflectionUtil;
import dk.lockfuglsang.minecraft.structureapi.nms.StructureApiCommon;
import dk.lockfuglsang.minecraft.structureapi.nms.StructureApi_v1_12_R1;
import dk.lockfuglsang.minecraft.structureapi.nms.StructureApi_v1_13_R1;
import dk.lockfuglsang.minecraft.util.VersionUtil;
import dk.lockfuglsang.minecraft.util.VersionUtil.Version;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dk.lockfuglsang.minecraft.po.I18nUtil.tr;

/**
 * Small util to paste and save structures
 * <p>
 * Snagged and adapted from https://gist.github.com/MiniDigger/ce2b0b0446d53d3bb94f24f1c708c657
 * Credit goes primarily to MiniDigger for the initial "digging" :D
 */
@SuppressWarnings({"FieldCanBeLocal", "unchecked"})
public class StructureUtil {
    private static Map<String, Class<? extends StructureApiCommon>> apiFactoryMap = new LinkedHashMap<>();

    static {
        apiFactoryMap.put("v1_10", StructureApi_v1_12_R1.class);
        apiFactoryMap.put("v1_11", StructureApi_v1_12_R1.class);
        apiFactoryMap.put("v1_12", StructureApi_v1_12_R1.class);
        apiFactoryMap.put("v1_13", StructureApi_v1_13_R1.class);
    }

    private static StructureApi api = null;

    public static StructureApi getAPI() {
        if (api != null) {
            return api;
        }
        String version = ReflectionUtil.getCraftBukkitVersion();
        Version release = VersionUtil.getVersion(version);
        String rel = release.toReleaseString();
        Class<? extends StructureApiCommon> apiClass = apiFactoryMap.get(rel);
        if (apiClass == null) {
            // Try latest
            apiClass = new ArrayList<>(apiFactoryMap.values()).get(apiFactoryMap.size()-1);
        }
        try {
            api = apiClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.getLogger("StructureAPI").log(Level.WARNING, "Unable to instantiate StructureAPI", e);
            api = null;
        }
        return api;
    }

    public static List<String> dumpMethods(String className) {
        List<String> list = new ArrayList<>();
        list.add(tr("NMS version is {0}", ReflectionUtil.getCraftBukkitVersion()));
        list.add(tr("StructureAPI is {0}", getAPI().getClass().getSimpleName()));
        Class c = ReflectionUtil.getField(getAPI(), className);
        list.addAll(ReflectionUtil.dumpMethods(c));
        return list;
    }

}