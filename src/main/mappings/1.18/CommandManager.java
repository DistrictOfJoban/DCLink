import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Consumer;

public class CommandManager {

    public static void registerCommand(Consumer<CommandDispatcher<ServerCommandSource>> callback) {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            callback.accept(dispatcher);
        }));
    }
}
