package dmillerw.remoteio.core.helper;

import net.minecraft.entity.Entity;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;

/**
 * Borrowed from OpenModsLib. If there's licensing issues I'm missing, please inform me!
 * @author dmillerw
 */
public class MatrixHelper {

	private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

	public static void loadMatrix(Matrix4f matrix4f) {
		matrix4f.store(buffer);
		buffer.flip();
		GL11.glMultMatrix(buffer);
	}

	public static Matrix4f getRotationMatrix(Entity entity) {
		double yaw = Math.toRadians(entity.rotationYaw - 180);
		double pitch = Math.toRadians(entity.rotationPitch);

		Matrix4f matrix4f = new Matrix4f();
		matrix4f.rotate((float)pitch, new Vector3f(1, 0, 0));
		matrix4f.rotate((float)yaw, new Vector3f(0, 1, 0));
		return matrix4f;
	}

}
