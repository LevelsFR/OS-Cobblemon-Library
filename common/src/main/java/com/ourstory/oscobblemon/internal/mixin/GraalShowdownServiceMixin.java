package com.ourstory.oscobblemon.internal.mixin;

import com.ourstory.oscobblemon.internal.BattleWeatherHookStatus;
import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownService;
import com.ourstory.oscobblemon.internal.BattleWeatherRequests;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

/** Cobblemon 1.8.2–1.8.4-only adapter for its bundled Showdown stream. */
@Mixin(value = GraalShowdownService.class, remap = false)
public abstract class GraalShowdownServiceMixin {
    private static final String INSTALL_SCRIPT = """
            (() => {
              const BattleStream = require('./sim/battle-stream').BattleStream;
              if (!BattleStream || !BattleStream.prototype ||
                  typeof BattleStream.prototype._writeLine !== 'function') return false;
              const prototype = BattleStream.prototype;
              if (prototype.__osclAmbientWeatherInstalled) return true;
              const originalWriteLine = prototype._writeLine;
              prototype._writeLine = function(type, message) {
                if (type !== 'osclweather') {
                  return originalWriteLine.call(this, type, message);
                }
                const battle = this.battle;
                if (!battle || battle.ended || !battle.field) return;
                const weatherId = String(message || '').trim().toLowerCase();
                const field = battle.field;

                if (!battle.started) {
                  if (field.weather) return;
                  const initialCondition = battle.dex.conditions.get(weatherId);
                  if (!initialCondition || initialCondition.effectType !== 'Weather') return;
                  const previousWeather = field.weather;
                  const previousState = field.weatherState;
                  const state = {id: initialCondition.id, duration: 0, osclAmbient: true};
                  field.weather = initialCondition.id;
                  field.weatherState = state;
                  try {
                    if (!battle.singleEvent('FieldStart', initialCondition, state, field, null, battle.effect)) {
                      field.weather = previousWeather;
                      field.weatherState = previousState;
                      return;
                    }
                    battle.eachEvent('WeatherChange');
                  } catch (error) {
                    field.weather = previousWeather;
                    field.weatherState = previousState;
                  }
                  return;
                }

                if (!field.weatherState || (field.weather && !field.weatherState.osclAmbient)) return;
                if (weatherId === 'none') {
                  if (!field.weather) return;
                  field.clearWeather();
                  return;
                }

                const condition = battle.dex.conditions.get(weatherId);
                if (!condition || condition.effectType !== 'Weather') return;
                if (field.weather === condition.id && field.weatherState.osclAmbient) return;
                const previousWeather = field.weather;
                const previousState = field.weatherState;
                if (previousWeather) {
                  const previousCondition = field.getWeather();
                  if (previousCondition) {
                    battle.singleEvent('FieldEnd', previousCondition, previousState, field);
                  }
                }
                const state = {id: condition.id, duration: 0, osclAmbient: true};
                field.weather = condition.id;
                field.weatherState = state;
                try {
                  if (!battle.singleEvent('FieldStart', condition, state, field, null, battle.effect)) {
                    field.weather = previousWeather;
                    field.weatherState = previousState;
                    return;
                  }
                  battle.eachEvent('WeatherChange');
                } catch (error) {
                  field.weather = previousWeather;
                  field.weatherState = previousState;
                }
              };
              Object.defineProperty(prototype, '__osclAmbientWeatherInstalled', {value: true});
              return true;
            })()
            """;

    @Inject(method = "boot()V", at = @At("TAIL"), remap = false)
    private void oscl$installWeatherCommand(CallbackInfo callbackInfo) {
        boolean installed = installShowdownPatch(this);
        BattleWeatherHookStatus.markBootPatchInstalled(installed);
        System.getLogger("os_cobblemon_library").log(
                installed ? System.Logger.Level.INFO : System.Logger.Level.ERROR,
                installed
                        ? "Installed the Cobblemon 1.8.2–1.8.4 ambient battle weather hook"
                        : "Cobblemon 1.8.2–1.8.4 ambient battle weather hook is unavailable"
        );
    }

    @ModifyArgs(
            method = "startBattle(Lcom/cobblemon/mod/common/api/battles/model/PokemonBattle;[Ljava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/relocations/graalvm/polyglot/Value;execute([Ljava/lang/Object;)Lcom/cobblemon/mod/relocations/graalvm/polyglot/Value;",
                    remap = false
            ),
            remap = false
    )
    private void oscl$addInitialWeather(Args call) {
        Object[] invocation = call.get(0);
        if (invocation == null || invocation.length < 3
                || !(invocation[1] instanceof String battleId)
                || !(invocation[2] instanceof String[] messages)) {
            return;
        }

        String weatherId = BattleWeatherRequests.consume(UUID.fromString(battleId));
        if (weatherId == null) {
            return;
        }
        int startIndex = -1;
        for (int index = 0; index < messages.length; index++) {
            if (messages[index].startsWith(">start ")) {
                startIndex = index;
                break;
            }
        }
        if (startIndex < 0) {
            return;
        }

        String[] updatedMessages = new String[messages.length + 1];
        System.arraycopy(messages, 0, updatedMessages, 0, startIndex + 1);
        updatedMessages[startIndex + 1] = ">osclweather " + weatherId;
        System.arraycopy(
                messages, startIndex + 1,
                updatedMessages, startIndex + 2,
                messages.length - startIndex - 1
        );
        Object[] updatedInvocation = Arrays.copyOf(invocation, invocation.length);
        updatedInvocation[2] = updatedMessages;
        call.set(0, updatedInvocation);
    }

    private static boolean installShowdownPatch(Object service) {
        try {
            Field contextField = service.getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            Object context = contextField.get(service);
            Method eval = context.getClass().getMethod("eval", String.class, CharSequence.class);
            Object result = eval.invoke(context, "js", INSTALL_SCRIPT);
            Method asBoolean = result.getClass().getMethod("asBoolean");
            return Boolean.TRUE.equals(asBoolean.invoke(result));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            System.getLogger("os_cobblemon_library").log(
                    System.Logger.Level.ERROR,
                    "Unable to install the Cobblemon 1.8.2–1.8.4 ambient battle weather hook",
                    exception
            );
            return false;
        }
    }
}
