package dmillerw.remoteio.core.helper;

import net.minecraft.entity.Entity;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;

/**
 * Borrowed from OpenModsLib. If there's licensing issues I'm missing, please inform me!
 *
 * @author dmillerw
 */
public class MatrixHelper {

    private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    public static void loadMatrix(Matrix4f matrix4f) {
        if (matrix4f == null) {
            return;
        }

        matrix4f.store(buffer);
        buffer.flip();
        GL11.glMultMatrix(buffer);
    }

    public static Matrix4f multiply(Matrix4f src, Matrix4f mod) {
        Matrix4f result = new Matrix4f();

        float n00 = src.m00 * mod.m00 + src.m01 * mod.m10 + src.m02 * mod.m20 + src.m03 * mod.m30;
        float n01 = src.m00 * mod.m01 + src.m01 * mod.m11 + src.m02 * mod.m21 + src.m03 * mod.m31;
        float n02 = src.m00 * mod.m02 + src.m01 * mod.m12 + src.m02 * mod.m22 + src.m03 * mod.m32;
        float n03 = src.m00 * mod.m03 + src.m01 * mod.m13 + src.m02 * mod.m23 + src.m03 * mod.m33;
        float n10 = src.m10 * mod.m00 + src.m11 * mod.m10 + src.m12 * mod.m20 + src.m13 * mod.m30;
        float n11 = src.m10 * mod.m01 + src.m11 * mod.m11 + src.m12 * mod.m21 + src.m13 * mod.m31;
        float n12 = src.m10 * mod.m02 + src.m11 * mod.m12 + src.m12 * mod.m22 + src.m13 * mod.m32;
        float n13 = src.m10 * mod.m03 + src.m11 * mod.m13 + src.m12 * mod.m23 + src.m13 * mod.m33;
        float n20 = src.m20 * mod.m00 + src.m21 * mod.m10 + src.m22 * mod.m20 + src.m23 * mod.m30;
        float n21 = src.m20 * mod.m01 + src.m21 * mod.m11 + src.m22 * mod.m21 + src.m23 * mod.m31;
        float n22 = src.m20 * mod.m02 + src.m21 * mod.m12 + src.m22 * mod.m22 + src.m23 * mod.m32;
        float n23 = src.m20 * mod.m03 + src.m21 * mod.m13 + src.m22 * mod.m23 + src.m23 * mod.m33;
        float n30 = src.m30 * mod.m00 + src.m31 * mod.m10 + src.m32 * mod.m20 + src.m33 * mod.m30;
        float n31 = src.m30 * mod.m01 + src.m31 * mod.m11 + src.m32 * mod.m21 + src.m33 * mod.m31;
        float n32 = src.m30 * mod.m02 + src.m31 * mod.m12 + src.m32 * mod.m22 + src.m33 * mod.m32;
        float n33 = src.m30 * mod.m03 + src.m31 * mod.m13 + src.m32 * mod.m23 + src.m33 * mod.m33;

        result.m00 = n00;
        result.m01 = n01;
        result.m02 = n02;
        result.m03 = n03;
        result.m10 = n10;
        result.m11 = n11;
        result.m12 = n12;
        result.m13 = n13;
        result.m20 = n20;
        result.m21 = n21;
        result.m22 = n22;
        result.m23 = n23;
        result.m30 = n30;
        result.m31 = n31;
        result.m32 = n32;
        result.m33 = n33;

        return result;
    }

    public static Matrix4f getRotationMatrix(Entity entity) {
        double yaw = Math.toRadians(entity.rotationYaw - 180);
        double pitch = Math.toRadians(entity.rotationPitch);

        Matrix4f matrix4f = new Matrix4f();
        matrix4f.rotate((float) pitch, new Vector3f(1, 0, 0));
        matrix4f.rotate((float) yaw, new Vector3f(0, 1, 0));
        return matrix4f;
    }

    public static Matrix4f getRotationMatrix(float x, float y, float z) {
        Matrix4f result = new Matrix4f();

//		Matrix4f matrixX = new Matrix4f();
//		Matrix4f matrixY = new Matrix4f();
//		Matrix4f matrixZ = new Matrix4f();
        Vector3f vectorX = new Vector3f(1, 0, 0);
        Vector3f vectorY = new Vector3f(0, 1, 0);
        Vector3f vectorZ = new Vector3f(0, 0, 1);
//
        // MATRIX ROTATION
//		matrixX.rotate((float) Math.toRadians(x), vectorX);
//		matrixY.rotate((float) Math.toRadians(y), vectorY);
//		matrixZ.rotate((float) Math.toRadians(z), vectorZ);
//
//		result = multiply(result, matrixX);
//		result = multiply(result, matrixY);
//		result = multiply(result, matrixZ);

        result.rotate((float) Math.toRadians(x), vectorX);
        result.rotate((float) Math.toRadians(y), vectorY);
        result.rotate((float) Math.toRadians(z), vectorZ);

        return result;
    }
}
