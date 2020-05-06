package net.fabricmc.example;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.screen.ImguiScreen;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ExampleMod implements ModInitializer {
	private static FabricKeyBinding keyBinding;
	private static boolean isScreenOpen = false;


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");
		InitializeKeybinds();
	}

	private void InitializeKeybinds() {
		KeyBindingRegistry.INSTANCE.addCategory("imguiDemo");

		keyBinding = FabricKeyBinding.Builder.create(
				new Identifier("imgui", "openimgui"),
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"imguiDemo"
		).build();

		ClientTickCallback.EVENT.register(e ->
		{
			if (keyBinding.isPressed()) {
				if (MinecraftClient.getInstance().player != null
						&& MinecraftClient.getInstance().currentScreen == null) {
					MinecraftClient.getInstance().openScreen(new ImguiScreen());
				}
			}
		});
	}
}
