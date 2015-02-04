package remoteio.core.mapping;

import remoteio.core.MappingHelper;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author dmillerw
 */
public class MappingConstants {

    public static class Type {
        // [0]: Deobfuscated
        // [1]: Obfuscated
        public static final String[] WORLD = new String[] {"net/minecraft/world/World", "ahb"};
        public static final String[] TESSELLATOR = new String[] {"net/minecraft/client/renderer/Tessellator", "bmh"};
        public static final String[] TESSELLATOR_VERTEX_STATE = new String[] {"net/minecraft/client/shader/TesselatorVertexState", "bmi"};

        public static String get(String[] array) {
            if (MappingHelper.obfuscated) {
                return array[1];
            } else {
                return array[0];
            }
        }
    }

    public static class Method {
        // [0]: Deobfuscated
        // [1]: Searge
        // [2]: Notch
        public static final String[] GET_VERTEX_STATE = new String[] {"getVertexState", "func_147564_a", "a"};
        public static final String[] ADD_VERTEX = new String[] {"addVertex", "func_78377_a", "a"};
        public static final String[] SET_NORMAL = new String[] {"setNormal", "func_78375_b", "c"};
        public static final String[] GET_INDIRECT_POWER_LEVEL_TO = new String[] {"getIndirectPowerLevelTo", "func_72878_l", "g"};

        public static class Desc {
            // [0]: Deobfuscated
            // [1]: Obfuscated
            public static final String[] GET_VERTEX_STATE = new String[] {"(FFF)Lnet/minecraft/client/shader/TesselatorVertexState;", "(FFF)Lbmi;"};
            public static final String[] ADD_VERTEX = new String[] {"(DDD)V", "(DDD)V"};
            public static final String[] SET_NORMAL = new String[] {"(FFF)V", "(FFF)V"};
            public static final String[] GET_INDIRECT_POWER_LEVEL_TO = new String[] {"(IIII)I", "(IIII)I"};

            public static String get(String[] array) {
                if (MappingHelper.obfuscated) {
                    return array[1];
                } else {
                    return array[0];
                }
            }
        }

        public static boolean equals(MethodNode methodNode, String[] name, String[] desc) {
            return MappingHelper.stringMatches(methodNode.name, name) && MappingHelper.stringMatches(methodNode.desc, desc);
        }
    }

    public static class Field {
        // [0]: Deobfuscated
        // [1]: Searge
        // [2]: Notch
        public static final String[] RAW_BUFFER_INDEX = new String[] {"rawBufferIndex", "field_147569_p", "p"};
        public static final String[] VERTEX_COUNT = new String[] {"vertexCount", "field_78406_i", "g"};
        public static final String[] HAS_TEXTURE = new String[] {"hasTexture", "field_78400_o", "m"};
        public static final String[] HAS_BRIGHTNESS = new String[] {"hasBrightness", "field_78414_p", "n"};
        public static final String[] HAS_NORMALS = new String[] {"hasNormals", "field_78413_q", "o"};
        public static final String[] HAS_COLOR = new String[] {"hasColor", "field_78399_n", "l"};

    }
}
