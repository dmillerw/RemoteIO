package remoteio.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;

/**
 * @author dmillerw
 */
public class SoundHandler {

    public static final SoundHandler INSTANCE = new SoundHandler();

    public int x;
    public int y;
    public int z;

    private boolean translate = false;

    public void translateNextSound(int x, int y, int z) {
        translate = true;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @SubscribeEvent
    public void onSoundPlayed(PlaySoundEvent17 event) {
        if (translate) {
            event.result = new PositionedSoundRecord(event.sound.getPositionedSoundLocation(), event.sound.getVolume(), event.sound.getPitch(), x, y, z);
            translate = false;
        }
    }
}
