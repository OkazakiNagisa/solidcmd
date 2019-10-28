package me.nakido.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityOverrider extends AbstractClientPlayerEntity {
	@Shadow
	public float nextNauseaStrength;

	@Shadow
	public float lastNauseaStrength;

	@Shadow
	@Final
	protected MinecraftClient client;

	public ClientPlayerEntityOverrider(ClientWorld clientWorld_1, GameProfile gameProfile_1, MinecraftClient client) {
		super(clientWorld_1, gameProfile_1);
	}

	/**
	 * @author Nakido
	 * @reason Fix Command UI
	 */
	@Overwrite
	private void updateNausea() {
		this.lastNauseaStrength = this.nextNauseaStrength;
		if (this.inPortal) {
			if (this.client.currentScreen != null && !this.client.currentScreen.isPauseScreen()) {
				if (this.client.currentScreen instanceof AbstractContainerScreen) {
					this.closeContainer();
				}
			}

			if (this.nextNauseaStrength == 0.0F) {
				this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F));
			}
			this.nextNauseaStrength += 0.0125F;
			if (this.nextNauseaStrength >= 1.0F) {
				this.nextNauseaStrength = 1.0F;
			}
			this.inPortal = false;
		} else if (this.hasStatusEffect(StatusEffects.NAUSEA) && this.getStatusEffect(StatusEffects.NAUSEA).getDuration() > 60) {
			this.nextNauseaStrength += 0.006666667F;
			if (this.nextNauseaStrength > 1.0F) {
				this.nextNauseaStrength = 1.0F;
			}
		} else {
			if (this.nextNauseaStrength > 0.0F) {
				this.nextNauseaStrength -= 0.05F;
			}

			if (this.nextNauseaStrength < 0.0F) {
				this.nextNauseaStrength = 0.0F;
			}
		}
		this.tickPortalCooldown();
	}
}
