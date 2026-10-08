package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.pokeball.PokeBall;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

/**
 * Helpers for Cobblemon Poké Ball identifiers and translated display names.
 */
public final class PokemonBalls {
    private PokemonBalls() {
    }

    /** Returns the canonical data identifier of a Cobblemon Poké Ball. */
    public static ResourceLocation idOf(PokeBall pokeBall) {
        Objects.requireNonNull(pokeBall, "pokeBall");
        return pokeBall.getName();
    }

    /** Returns the canonical identifier when the item is a Cobblemon Poké Ball. */
    public static Optional<ResourceLocation> idOf(Item item) {
        Objects.requireNonNull(item, "item");
        return item instanceof PokeBallItem pokeBallItem
                ? Optional.of(idOf(pokeBallItem.getPokeBall()))
                : Optional.empty();
    }

    /** Returns the canonical identifier when the stack contains a Cobblemon Poké Ball. */
    public static Optional<ResourceLocation> idOf(ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        return stack.isEmpty() ? Optional.empty() : idOf(stack.getItem());
    }

    /** Returns the Cobblemon-translated item name as a component. */
    public static Component displayName(PokeBall pokeBall) {
        Objects.requireNonNull(pokeBall, "pokeBall");
        return pokeBall.stack(1).getHoverName();
    }

    /** Returns the translated item name when the stack contains a Cobblemon Poké Ball. */
    public static Optional<Component> displayName(ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        return idOf(stack).map(ignored -> stack.getHoverName());
    }
}
