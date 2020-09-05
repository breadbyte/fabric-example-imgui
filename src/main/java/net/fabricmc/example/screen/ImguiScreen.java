package net.fabricmc.example.screen;

import imgui.ImGui;
import imgui.ImguiKt;
import imgui.classes.Context;
import imgui.classes.IO;
import imgui.impl.gl.ImplGL3;
import imgui.impl.glfw.ImplGlfw;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import uno.glfw.GlfwWindow;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ImguiScreen extends Screen {

    private static ImGui imgui = ImGui.INSTANCE;

    private static ImplGL3 implGl3;
    private static ImplGlfw implGlfw;

    private static IO ImGuiIO;
    private static HashSet<Integer> keyBuffer = new HashSet<Integer>();

    public ImguiScreen() {
        super(new LiteralText("ImguiScreen"));
    }

    // Initialization for imgui.
    static {
        ImguiKt.MINECRAFT_BEHAVIORS = true;
        GlfwWindow window = GlfwWindow.from(MinecraftClient.getInstance().getWindow().getHandle());
        window.makeContextCurrent();

        new Context();
        implGlfw = new ImplGlfw(window, false, null);
        implGl3 = new ImplGL3();
    }

    // Prevents Minecraft from pausing the game whenever we open the GUI.
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // Tells imgui to enter a character, when typing on a textbox or similar.
    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (ImGuiIO.getWantTextInput()) {
            ImGuiIO.addInputCharacter(chr);
        }
        
        super.charTyped(chr, keyCode);
        return true;
    }

    // Passes mouse scrolling to imgui.
    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (ImGuiIO.getWantCaptureMouse()) {
            ImGuiIO.setMouseWheel((float) amount);
        }

        super.mouseScrolled(d, e, amount);
        return true;
    }

    // Passes keypresses for imgui to handle.
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ImGuiIO.getWantCaptureKeyboard()) {
            ImGuiIO.getKeysDown()[keyCode] = true;
            keyBuffer.add(keyCode);
        }

        // Skip handling of the ESC key, because Minecraft uses it to close the screen.
        if (keyCode == 256) {
            ImGuiIO.getKeysDown()[256] = false;
        }

        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    // Tells imgui the keys pressed have been released.
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        ImGuiIO.getKeysDown()[keyCode] = false;
        keyBuffer.remove(keyCode);

        super.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public void onClose() {
        // When Minecraft closes the screen, clear the key buffer.
        for (int keyCode: keyBuffer) {
            ImGuiIO.getKeysDown()[keyCode] = false;
        }
        keyBuffer.clear();

        super.onClose();
    }

    @Override
    public void render(int x, int y, float partialTicks) {
        ImGuiIO = imgui.getIo();

        implGl3.newFrame();
        implGlfw.newFrame();
        imgui.newFrame();

        imgui.showDemoWindow(new boolean[]{true});

        imgui.render();
        implGl3.renderDrawData(Objects.requireNonNull(imgui.getDrawData()));
    }
}
