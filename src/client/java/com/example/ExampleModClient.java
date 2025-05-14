package com.example.freezeworldmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod(FreezeWorldMod.MODID)
public class FreezeWorldMod {
    public static final String MODID = "freezeworldmod";
    public static final String NAME = "World Freeze Mod";
    public static final String VERSION = "2.2";
    
    private final KeyBinding toggleFreezeKey = new KeyBinding(
        "key.freezeworldmod.toggle",
        KeyConflictContext.IN_GAME,
        InputMappings.Type.KEYSYM,
        GLFW.GLFW_KEY_F,
        "category.freezeworldmod.keys"
    );
    
    private boolean isWorldFrozen = false;
    private int artificialPing = 0;

    public FreezeWorldMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ClientRegistry.registerKeyBinding(FreezeWorldMod.instance.toggleFreezeKey);
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleFreezeKey.isPressed()) {
            isWorldFrozen = !isWorldFrozen;
            artificialPing = isWorldFrozen ? 2000 : 0;
            
            // Уведомление в чат
            Minecraft.getInstance().player.sendMessage(
                new StringTextComponent(
                    isWorldFrozen ? 
                    TextFormatting.RED + "Мир заморожен!" : 
                    TextFormatting.GREEN + "Мир разморожен!"
                ), 
                Minecraft.getInstance().player.getUUID()
            );
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.font;
        
        // Статус заморозки в правом верхнем углу
        String status = isWorldFrozen ? 
            TextFormatting.RED + "ЗАМОРОЖЕНО" : 
            TextFormatting.GREEN + "АКТИВНО";
        int statusWidth = font.width(status);
        font.drawShadow(
            event.getMatrixStack(), 
            status, 
            mc.getWindow().getGuiScaledWidth() - statusWidth - 5, 
            5, 
            0xFFFFFF
        );

        // Информация об искусственном пинге
        if (isWorldFrozen) {
            String pingInfo = TextFormatting.GRAY + "Искусственный пинг: " + 
                            TextFormatting.WHITE + artificialPing + "мс";
            int pingWidth = font.width(pingInfo);
            font.drawShadow(
                event.getMatrixStack(), 
                pingInfo, 
                mc.getWindow().getGuiScaledWidth() - pingWidth - 5, 
                17, 
                0xAAAAAA
            );
        }
    }

    private static FreezeWorldMod instance;
    
    public FreezeWorldMod() {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static FreezeWorldMod getInstance() {
        return instance;
    }
}
