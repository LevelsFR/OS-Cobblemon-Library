package com.ourstory.oscobblemon.internal.mixin;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.spongepowered.asm.mixin.injection.selectors.MatchResult;
import org.spongepowered.asm.mixin.injection.struct.MemberInfo;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GraalShowdownServiceMixinTest {
    @Test
    void initialWeatherSelectsExactlyOneRealCallAndAcceptsBothLoaderNamespaces() throws IOException {
        ClassNode mixin = readClass("com/ourstory/oscobblemon/internal/mixin/GraalShowdownServiceMixin.class");
        var handler = mixin.methods.stream().filter(method -> method.name.equals("oscl$addInitialWeather"))
                .findFirst().orElseThrow();
        AnnotationNode injection = handler.visibleAnnotations.stream()
                .filter(annotation -> annotation.desc.endsWith("/ModifyArgs;"))
                .findFirst().orElseThrow();
        AnnotationNode at = (AnnotationNode) value(injection, "at");
        MemberInfo selector = MemberInfo.parse((String) value(at, "target"), null).validate();

        ClassNode service = readClass("com/cobblemon/mod/common/battles/runner/graal/GraalShowdownService.class");
        var start = service.methods.stream().filter(method -> method.name.equals("startBattle"))
                .findFirst().orElseThrow();
        int matches = 0;
        for (var instruction : start.instructions) {
            if (instruction instanceof MethodInsnNode call
                    && selector.matches(call.owner, call.name, call.desc) == MatchResult.EXACT_MATCH) {
                matches++;
            }
        }
        assertEquals(1, matches);
        for (String owner : new String[]{"org/graalvm/polyglot/Value",
                "com/cobblemon/mod/relocations/graalvm/polyglot/Value"}) {
            assertEquals(MatchResult.EXACT_MATCH, selector.matches(owner, "execute",
                    "([Ljava/lang/Object;)L" + owner + ";"));
        }
    }

    private static Object value(AnnotationNode annotation, String name) {
        return annotation.values.get(annotation.values.indexOf(name) + 1);
    }

    private static ClassNode readClass(String name) throws IOException {
        try (var input = GraalShowdownServiceMixinTest.class.getClassLoader().getResourceAsStream(name)) {
            assertNotNull(input, name);
            ClassNode node = new ClassNode();
            new ClassReader(input).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return node;
        }
    }
}
