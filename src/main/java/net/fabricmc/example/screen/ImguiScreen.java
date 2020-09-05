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

import java.util.Objects;
import java.util.Vector;

@Environment(EnvType.CLIENT)
public class ImguiScreen extends Screen {

    private static ImGui imgui = ImGui.INSTANCE;

    private static ImplGL3 implGl3;
    private static ImplGlfw implGlfw;

    private static IO ImGuiIO;
    private static Vector<Integer> keyBuffer = new Vector<Integer>();

    public ImguiScreen() {
        super(new LiteralText("ImguiScreen"));
    }

    static {
        ImguiKt.MINECRAFT_BEHAVIORS = true;
        GlfwWindow window = GlfwWindow.from(MinecraftClient.getInstance().getWindow().getHandle());
        window.makeContextCurrent();

        new Context();
        implGlfw = new ImplGlfw(window, false, null);
        implGl3 = new ImplGL3();

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (ImGuiIO.getWantTextInput()) {
            ImGuiIO.addInputCharacter(chr);
        }
        super.charTyped(chr, keyCode);
        return true;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (ImGuiIO.getWantCaptureMouse()) {
            ImGuiIO.setMouseWheel((float) amount);
        }
        super.mouseScrolled(d, e, amount);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ImGuiIO.getWantCaptureKeyboard()) {
            ImGuiIO.getKeysDown()[keyCode] = true;
            keyBuffer.add(keyCode);
        }

        if (keyCode == 256) {
            ImGuiIO.getKeysDown()[256] = false;
        }

        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        ImGuiIO.getKeysDown()[keyCode] = false;
        super.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public void onClose() {
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
