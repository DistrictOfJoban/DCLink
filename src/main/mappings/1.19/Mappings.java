import net.minecraft.text.MutableText;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class Mappings {
    public static MutableText literalText(String str) {
        return Text.literal(str);
    }

    public static ServerPlayerEntity getPlayer(ServerPlayNetworkHandler handler) {
        return handler.getPlayer();
    }

    public static ServerWorld getServerWorld(ServerPlayerEntity player) {
        return player.getWorld();
    }
}