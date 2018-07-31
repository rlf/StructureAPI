package dk.lockfuglsang.minecraft.structureapi;

import org.bukkit.util.Vector;

public interface Structure {
    String getName();
    String getAuthor();
    Vector getSize();
    Object getNmsObject();
}
