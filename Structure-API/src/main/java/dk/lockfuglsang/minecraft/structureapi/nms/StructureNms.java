package dk.lockfuglsang.minecraft.structureapi.nms;

import dk.lockfuglsang.minecraft.reflection.ReflectionUtil;
import dk.lockfuglsang.minecraft.structureapi.Structure;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class StructureNms implements Structure {
    private String name;
    private Object definedStructure;
    private Class<?> blockPositionClass;

    public StructureNms(String name, Object definedStructure, Class<?> blockPositionClass) {
        this.name = name;
        this.definedStructure = definedStructure;
        this.blockPositionClass = blockPositionClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAuthor() {
        try {
            Method method = ReflectionUtil.findMethod(definedStructure.getClass(), String.class);
            return (String) method.invoke(definedStructure);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Vector getSize() {
        try {
            Method method = ReflectionUtil.findMethod(definedStructure.getClass(), blockPositionClass);
            Object blockPos = method.invoke(definedStructure);
            int x = (int) blockPositionClass.getMethod("getX").invoke(blockPos);
            int y = (int) blockPositionClass.getMethod("getY").invoke(blockPos);
            int z = (int) blockPositionClass.getMethod("getZ").invoke(blockPos);
            return new Vector(x, y, z);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object getNmsObject() {
        return definedStructure;
    }

    @Override
    public String toString() {
        return "Structure(name=" + getName() + ", size=" + getSize() + ")";
    }
}
