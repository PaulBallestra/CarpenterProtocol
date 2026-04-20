package fr.seeeek.carpenterprotocol.registry;


import fr.seeeek.carpenterprotocol.enums.MiniGameType;
import fr.seeeek.carpenterprotocol.interfaces.MiniGameLogic;
import fr.seeeek.carpenterprotocol.logic.LaserTagMiniGameLogic;

import java.util.HashMap;
import java.util.Map;

public class MiniGameLogicRegistry {
    private static final Map<MiniGameType, MiniGameLogic> LOGICS = new HashMap<>();

    static {
        LOGICS.put(MiniGameType.LASERTAG, new LaserTagMiniGameLogic());
    }

    public static MiniGameLogic get(MiniGameType type) {
        return LOGICS.get(type);
    }
}